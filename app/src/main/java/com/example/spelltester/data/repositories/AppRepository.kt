package com.example.spelltester.data.repositories

import android.util.*
import com.example.spelltester.data.db.*
import com.example.spelltester.data.db.attempt.*
import com.example.spelltester.data.db.quiz.*
import com.example.spelltester.data.db.word.*
import com.example.spelltester.data.remote.*

class AppRepository(
    private val db: AppDatabase
) : Repository {
    private val TAG = "KH_APP_REPO"
    override fun fetchRemoteData() {
        RemoteService.getData("data.json") {
            val wordsList = JsonToWords(it)
            upsertAllWords(wordsList)
            Log.d(TAG, "update words, size: ${wordsList.size}")
            val quizzesList = JsonToQuizzes(it)
            upsert(quizzesList)
            Log.d(TAG, "update quizzes, size: ${quizzesList.size}")
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

    //attempts
    override fun getAttemptsByQuizId(quizId: Int) = db.quizDao().getAttemptsByQuizId(quizId)
    override fun getAllAttempt() = db.attemptDao().getAttempt()
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