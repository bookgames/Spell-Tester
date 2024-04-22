package com.example.spelltester.data.db

import com.example.spelltester.data.db.quiz.*
import com.example.spelltester.data.db.word.*
import org.json.*
object JsonConverter{
    const val KEY_WORDS="words"
    const val KEY_ENGLISH_WORD="english"
    const val KEY_ARABIC_WORD="arabic"
    const val KEY_QUIZZES="quizzes"
    const val KEY_QUIZ_ID="id"
    const val KEY_QUIZ_NAME="name"
    const val KEY_QUIZ_WORDS="words"
    const val KEY_VERSION="version"
    const val DATA_FILE_NAME="data.json"


fun jsonToWords(json: String): List<Word> {
    val root= JSONObject(json)
    val words=root.getJSONArray(KEY_WORDS)
    val wordsList= mutableListOf<Word>()
    for (i in 0 until words.length()){
        val word=words.getJSONObject(i)
        wordsList.add(
            Word(
                word.getString(KEY_ENGLISH_WORD),
                word.getString(KEY_ARABIC_WORD),
            )
        )
    }
    return wordsList
}
fun jsonTOQuizzes(json: String): List<Quiz> {
    val root= JSONObject(json)
    val quizzes=root.getJSONArray(KEY_QUIZZES)
    val quizzesList= mutableListOf<Quiz>()
    for (i in 0 until quizzes.length()){
        val quiz=quizzes.getJSONObject(i)
        val quizId=quiz.getInt(KEY_QUIZ_ID)
        val quizName=quiz.getString( KEY_QUIZ_NAME)
        val wordsId=quiz.getJSONArray(KEY_QUIZ_WORDS)
        val wordsIdList= mutableListOf<Int>()
        for (j in 0 until wordsId.length()){
            wordsIdList.add(wordsId.getInt(j))
        }
        quizzesList.add(
            Quiz(
                quizName,
                wordsIdList.toIntArray(),
                quizId,
            )
        )
    }
    return quizzesList
}}