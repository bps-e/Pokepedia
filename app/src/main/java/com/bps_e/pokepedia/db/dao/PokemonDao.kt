package com.bps_e.pokepedia.db.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.bps_e.db.DatabaseConnector
import com.bps_e.pokepedia.db.Poke
import kotlinx.coroutines.flow.Flow

import com.bps_e.pokepedia.db.Poke.PokemonEntry as T

@Dao
interface PokemonDao : DatabaseConnector<T> {
    companion object { const val TABLE = Poke.TABLE_POKEMON }

    @Transaction
    @Query("SELECT * FROM $TABLE")
    fun pagingSource(): PagingSource<Int, Poke.Pokemon>
    @Transaction
    @Query("SELECT * FROM $TABLE WHERE name = :name")
    fun getLocalName(name: String): List<Poke.Pokemon>

    @Query("SELECT * FROM $TABLE")
    override fun getAll(): Flow<List<T>>
    @Query("SELECT COUNT(*) FROM $TABLE")
    override suspend fun count(): Long
    @Query("DELETE FROM $TABLE")
    override suspend fun deleteAll()
}

