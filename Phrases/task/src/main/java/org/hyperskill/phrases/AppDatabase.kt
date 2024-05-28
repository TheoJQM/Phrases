package org.hyperskill.phrases

import androidx.room.Dao
import androidx.room.Database
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.RoomDatabase

@Database(entities = [Phrase::class], version = 1)
abstract class AppDatabase: RoomDatabase() {
    abstract fun getPhraseDao(): PhraseDao
}


@Dao
interface PhraseDao {

    @Insert
    fun insert(vararg phrase: Phrase)

    @Delete
    fun delete(phrase: Phrase)

    @Query("SELECT * FROM phrases WHERE id = :id")
    fun get(id: Int): Phrase

    @Query("SELECT * FROM phrases")
    fun getAll(): List<Phrase>

    @Query("SELECT phrase FROM phrases ORDER BY RANDOM()")
    fun getRandom(): String
}