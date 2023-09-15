/**
 * Copyright 2023 bps-e.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.bps_e.db

import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

/**
 * ```
 * import ... as T
 * interface ... : DatabaseConnector<T> {
 *   companion object { const val TABLE = ... }
 * }
 * ```
 */
interface DatabaseConnector<T: Any> {
    // @Query("SELECT * FROM $TABLE")
    //fun pagingSource(): PagingSource<Int, T>
    // @Query("SELECT * FROM $TABLE")
    fun getAll(): Flow<List<T>>
    // @Query("SELECT * FROM $TABLE WHERE id = :id")
    //fun get(id: Int): Flow<T>
    // @Query("SELECT COUNT(*) FROM $TABLE")
    suspend fun count(): Long
    // @Query("DELETE FROM $TABLE")
    suspend fun deleteAll()

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(data: T)
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(list: List<T>)
    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun update(data: T)
    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun update(list: List<T>)
    @Delete
    suspend fun delete(data: T)
}