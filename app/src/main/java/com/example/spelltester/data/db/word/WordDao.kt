package com.example.spelltester.data.db.word

import androidx.room.*

@Dao
interface WordDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun upsertAll(word: Word)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun upsertAll(words: List<Word>)

    @Query("SELECT * FROM words")
    fun getAllWords(): List<Word>

    @Delete
    fun deleteWord(word: Word)

    @Query("SELECT * FROM words WHERE wordId = :wordId")
    fun getWordByWordId(wordId: Int): Word?

}