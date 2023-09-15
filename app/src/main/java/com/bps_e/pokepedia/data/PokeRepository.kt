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
package com.bps_e.pokepedia.data

import androidx.paging.Pager
import com.bps_e.pokepedia.db.AppDatabase
import com.bps_e.pokepedia.db.Poke.Pokemon
import com.bps_e.pokepedia.service.PokeApiWrapper
import javax.inject.Inject

class PokeRepository @Inject constructor(
    private val db: AppDatabase,
    private val pager: Pager<Int, Pokemon>,
) {
    fun getPagingStream() = pager.flow
    fun getRequestState() = PokeApiWrapper.RequestState
    suspend fun RequestStat() = PokeApiWrapper.RequestStat(db)
    suspend fun RequestType() = PokeApiWrapper.RequestType(db)

    suspend fun localPokemonName(name: String): String = PokeApiWrapper.localPokemonName(db, name)
    suspend fun localGenera(id: Int): String = PokeApiWrapper.localGenera(db, id)
    suspend fun localPokemonFlavor(id: Int): String = PokeApiWrapper.localPokemonFlavor(db, id)
    suspend fun localStatName(name: String): String = PokeApiWrapper.localStatName(db, name)
    suspend fun localTypeName(name: String): String = PokeApiWrapper.localTypeName(db, name)
    suspend fun localAbilityName(name: String): String = PokeApiWrapper.localAbilityName(db, name)
}