package com.bps_e.pokepedia.db.dao

import androidx.room.Dao
import androidx.room.Query
import com.bps_e.db.DatabaseConnector
import com.bps_e.pokepedia.db.Poke
import kotlinx.coroutines.flow.Flow

import com.bps_e.pokepedia.db.Poke.AbilityName as T

@Dao
interface AbilityNameDao : DatabaseConnector<T> {
    companion object { const val TABLE = Poke.TABLE_ABILITY_NAME }

    @Query("SELECT * FROM $TABLE")
    override fun getAll(): Flow<List<T>>
    @Query("SELECT COUNT(*) FROM $TABLE")
    override suspend fun count(): Long
    @Query("DELETE FROM $TABLE")
    override suspend fun deleteAll()
}
