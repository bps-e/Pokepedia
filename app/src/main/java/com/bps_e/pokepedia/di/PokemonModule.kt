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
package com.bps_e.pokepedia.di

import android.content.Context
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.room.Room
import com.bps_e.pokepedia.data.PAGE_SIZE
import com.bps_e.pokepedia.data.PokeRemoteMediator
import com.bps_e.pokepedia.data.PokeRepository
import com.bps_e.pokepedia.db.AppDatabase
import com.bps_e.pokepedia.db.Poke
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.qualifiers.ApplicationContext

@Module
@InstallIn(ViewModelComponent::class)
object PokemonModule {
    @Provides
    fun provideDatabase(
        @ApplicationContext context: Context
    ) = Room.databaseBuilder(context, AppDatabase::class.java, "app.db").build()

    /*
    @Provides
    fun provideDao(db: AppDatabase) = db.dao()
    @Provides
    fun providePokemonNameDao(db: AppDatabase) = db.pokemonNameDao()
    @Provides
    fun providePokemonFlavorDao(db: AppDatabase) = db.pokemonFlavorDao()
    @Provides
    fun provideGeneraDao(db: AppDatabase) = db.generaDao()
    @Provides
    fun provideAbilityDao(db: AppDatabase) = db.abilityDao()
    @Provides
    fun provideAbilityNameDao(db: AppDatabase) = db.abilityNameDao()
    @Provides
    fun provideTypeDao(db: AppDatabase) = db.typeDao()
    @Provides
    fun provideTypeNameDao(db: AppDatabase) = db.typeNameDao()
    @Provides
    fun provideStatDao(db: AppDatabase) = db.statDao()
    @Provides
    fun provideStatNameDao(db: AppDatabase) = db.statNameDao()*/

    @OptIn(ExperimentalPagingApi::class)
    @Provides
    fun providePager(
        db: AppDatabase,
    ): Pager<Int, Poke.Pokemon> {
        return Pager(
            config = PagingConfig(pageSize = PAGE_SIZE),
            remoteMediator = PokeRemoteMediator(db = db),
            pagingSourceFactory = {
                db.dao().pagingSource()
            },
        )
    }

    @Provides
    fun provideRepository(db: AppDatabase, pager: Pager<Int, Poke.Pokemon>) = PokeRepository(db, pager)
}