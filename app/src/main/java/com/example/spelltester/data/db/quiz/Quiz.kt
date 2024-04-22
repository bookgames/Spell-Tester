package com.example.spelltester.data.db.quiz

import androidx.room.*
import com.example.spelltester.data.db.word.*
import com.example.spelltester.data.repositories.*

@Entity(tableName = "quiz")
data class Quiz(
    val name: String,
    @TypeConverters(IntArrayConverter::class)
    val wordsId: IntArray,
    @PrimaryKey(autoGenerate = true)
    var quizId: Int = 1,
) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Quiz

        if (name != other.name) return false
        if (!wordsId.contentEquals(other.wordsId)) return false
        return quizId == other.quizId
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + wordsId.contentHashCode()
        result = 31 * result + quizId
        return result
    }

}

