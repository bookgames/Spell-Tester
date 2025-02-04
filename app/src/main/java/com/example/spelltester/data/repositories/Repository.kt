package com.example.spelltester.data.repositories

import androidx.lifecycle.*
import com.example.spelltester.data.db.attempt.*
import com.example.spelltester.data.db.quiz.*
import com.example.spelltester.data.db.word.*
import kotlinx.coroutines.flow.*

interface Repository {
    fun fetchRemoteData(success: (String) -> Unit)
    fun upload2Firebase(text: String,path:String, success: (String) -> Unit, failure: (String) -> Unit)
    //words
    fun getWordByWordId(wordId: Int): Word?
    fun deleteWord(word: Word)
    fun upsert(word: Word)
    fun upsertAllWords(words: List<Word>)
    fun getWordsByWordsId(wordsId: IntArray): List<Word>


    //attempts
    fun getAttemptsByQuizId(quizId: Int): List<Attempt>
    fun getAllAttempt(): List<Attempt>
    fun upsert(attempt: Attempt)
    fun deleteAttempt(attempt: Attempt)
    fun getAttemptByWordId(wordId: Int): Attempt?

    //quiz's
    fun upsert(quiz: Quiz)
    fun upsert(quizzes: List<Quiz>)
    fun delete(quiz: Quiz)
    fun getAllQuiz(): LiveData<List<Quiz>>
    fun getQuizByQuizId(quizId: Int): Quiz?
    fun deleteAttempts()


}