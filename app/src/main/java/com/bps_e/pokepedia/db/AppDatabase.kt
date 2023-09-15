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
package com.bps_e.pokepedia.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.bps_e.db.ListTypeConverter
import com.bps_e.db.MapTypeConverter
import com.bps_e.pokepedia.db.dao.*

@Database(
    entities = [
        Poke.PokemonEntry::class, Poke.PokemonName::class,
        Poke.Genera::class, Poke.PokemonFlavor::class,
        Poke.TypeEntry::class, Poke.TypeName::class,
        Poke.StatEntry::class, Poke.StatName::class,
        Poke.AbilityEntry::class,Poke.AbilityName::class,
    ],
    version = 1, exportSchema = false
)
@TypeConverters(ListTypeConverter::class, MapTypeConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun dao(): PokemonDao
    abstract fun pokemonNameDao(): PokemonNameDao
    abstract fun pokemonFlavorDao(): PokemonFlavorDao
    abstract fun generaDao(): GeneraDao
    abstract fun typeDao(): TypeDao
    abstract fun typeNameDao(): TypeNameDao
    abstract fun statDao(): StatDao
    abstract fun statNameDao(): StatNameDao
    abstract fun abilityDao(): AbilityDao
    abstract fun abilityNameDao(): AbilityNameDao
}