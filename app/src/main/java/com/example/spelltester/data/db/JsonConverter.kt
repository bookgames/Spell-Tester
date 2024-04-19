package com.example.spelltester.data.db

import com.example.spelltester.data.db.quiz.*
import com.example.spelltester.data.db.word.*
import org.json.*

fun JsonToWords(json: String): List<Word> {
    val root= JSONObject(json)
    val words=root.getJSONArray("words")
    val wordsList= mutableListOf<Word>()
    for (i in 0 until words.length()){
        val word=words.getJSONObject(i)
        wordsList.add(
            Word(
                word.getString("english"),
                word.getString("arabic")
            )
        )
    }
    return wordsList
}
fun JsonToQuizzes(json: String): List<Quiz> {
    val root= JSONObject(json)
    val quizzes=root.getJSONArray("quizzes")
    val quizzesList= mutableListOf<Quiz>()
    for (i in 0 until quizzes.length()){
        val quiz=quizzes.getJSONObject(i)
        val quizId=quiz.getInt("id")
        val quizName=quiz.getString("name")
        val wordsId=quiz.getJSONArray("words")
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
}