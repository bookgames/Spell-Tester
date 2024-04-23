package com.example.spelltester.data.repositories

import android.util.*
import com.example.spelltester.data.db.*
import com.example.spelltester.data.db.JsonConverter.DATA_FILE_NAME
import com.example.spelltester.data.db.attempt.*
import com.example.spelltester.data.db.quiz.*
import com.example.spelltester.data.db.word.*
import com.example.spelltester.data.remote.*
import com.example.spelltester.data.storage.*
import com.example.spelltester.data.db.JsonConverter.KEY_VERSION
import com.example.spelltester.data.db.JsonConverter.jsonTOQuizzes
import com.example.spelltester.data.db.JsonConverter.jsonToWords
import org.json.JSONObject


class AppRepository(
    private val db: AppDatabase
) : Repository {
    private val TAG = "KH_APP_REPO"
    fun fetchRemoteData(){
        fetchRemoteData {
            Log.d(TAG, it)
        }
    }
    override fun fetchRemoteData(result: (String) -> Unit){
        RemoteService.getData(DATA_FILE_NAME) {
            val currentVersion = LocalStorage.getInstance().getVersion()
            val version = JSONObject(it).getInt(KEY_VERSION)
            if (version <= currentVersion){
                result("version is up to date")
                return@getData
            }
            LocalStorage.getInstance().saveVersion(version)
            val wordsList = jsonToWords(it)
            upsertAllWords(wordsList)
            val quizzesList = jsonTOQuizzes(it)
            upsert(quizzesList)
            result("version updated $version")
        }
    }

    //words
    override fun getWordByWordId(wordId: Int): Word? = db.wordDao().getWordByWordId(wordId)
    override fun deleteWord(word: Word) = db.wordDao().deleteWord(word)
    override fun upsert(word: Word) = db.wordDao().upsertAll(word)
    override fun upsertAllWords(words: List<Word>) = db.wordDao().upsertAll(words)
    override fun getWordsByWordsId(wordsId: IntArray): List<Word> {
        val words = mutableListOf<Word>()
        wordsId.forEach {
            words.add(db.wordDao().getWordByWordId(it)!!)
        }
        return words
    }

    override fun deleteAttempts() = db.attemptDao().deleteAllAttempts()

    //attempts
    override fun getAttemptsByQuizId(quizId: Int) = db.quizDao().getAttemptsByQuizId(quizId)
    override fun  getAllAttempt() = db.attemptDao().getAttempt()
    override fun upsert(attempt: Attempt) = db.attemptDao().upsert(attempt)
    override fun deleteAttempt(attempt: Attempt) = db.attemptDao().deleteAttempt(attempt)
    override fun getAttemptByWordId(wordId: Int) =
        db.attemptDao().getAttemptByWordId(wordId)

    //quiz's
    override fun upsert(quiz: Quiz) = db.quizDao().upsert(quiz)
    override fun upsert(quizzes: List<Quiz>) = db.quizDao().upsert(quizzes)
    override fun delete(quiz: Quiz) = db.quizDao().deleteQuiz(quiz)
    override fun getAllQuiz() = db.quizDao().getAllQuiz()
    override fun getQuizByQuizId(quizId: Int): Quiz? = db.quizDao().getQuizByQuizId(quizId)


    companion object {
        @Volatile
        private var instance: AppRepository? = null
        private val LOCK = Any()

        operator fun invoke(database: AppDatabase) = instance
            ?: synchronized(LOCK) {
                instance ?: AppRepository(database).also { instance = it }
            }

        fun getInstance(): AppRepository = instance!!
    }
}