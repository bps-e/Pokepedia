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

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import com.bps_e.pokepedia.db.AppDatabase
import com.bps_e.pokepedia.db.Poke
import com.bps_e.pokepedia.service.PokeApiWrapper
import javax.inject.Inject

const val PAGE_SIZE = 5

@OptIn(ExperimentalPagingApi::class)
class PokeRemoteMediator @Inject constructor(
    private val db: AppDatabase,
) : RemoteMediator<Int, Poke.Pokemon>() {
    override suspend fun initialize(): InitializeAction {
        //PokeApiWrapper.RequestStat(db)
        //PokeApiWrapper.RequestType(db)
        return if (db.dao().count() == 0L) InitializeAction.LAUNCH_INITIAL_REFRESH else InitializeAction.SKIP_INITIAL_REFRESH
    }

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, Poke.Pokemon>,
    ): MediatorResult {
        return try {
            val dao = db.dao()
            var count = dao.count()
            val offset = when (loadType) {
                LoadType.REFRESH -> if (count == 0L) 0L else return MediatorResult.Success(true)
                LoadType.PREPEND -> return MediatorResult.Success(true)
                LoadType.APPEND -> count
            }

            // 読込み出来てなかった場合のリトライ
            PokeApiWrapper.RequestStat(db)
            PokeApiWrapper.RequestType(db)

            PokeApiWrapper.RequestPokemon(db, PAGE_SIZE, offset.toInt())

            val count2 = dao.count()

            return MediatorResult.Success(count2 == count)
            //return MediatorResult.Success(true)
        }
        catch (e: Exception) {
            MediatorResult.Error(e)
        }
    }
}