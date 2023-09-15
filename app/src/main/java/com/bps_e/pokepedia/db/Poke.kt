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

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Relation

object Poke {
    const val TABLE_POKEMON = "pokemon"
    @Entity(tableName = TABLE_POKEMON)
    data class PokemonEntry(
        @PrimaryKey val id: Int = 0,
        val name: String,

        val types: List<String>,
        val abilities: List<String>,

        val height: Int, // 1/10
        val weight: Int, // 1/10

        val hp: Int,
        val attack: Int,
        val defense: Int,
        @ColumnInfo(name = "special-attack") val special_attack: Int,
        @ColumnInfo(name = "special-defense") val special_defense: Int,
        val speed: Int,

        val images: Map<String, String>,

        // species

        val gender_rate: Int,
    )
    const val TABLE_POKEMON_NAME = "pokemon_name"
    @Entity(tableName = TABLE_POKEMON_NAME)
    data class PokemonName(
        @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "id") val count: Int = 0,
        @ColumnInfo(name = "name_id") val id: Int,
        val language: String,
        val name: String,
    )
    const val TABLE_GENERA = "genera"
    @Entity(tableName = TABLE_GENERA)
    data class Genera(
        @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "id") val count: Int = 0,
        @ColumnInfo(name = "name_id") val id: Int,
        val language: String,
        val genus: String,
    )
    const val TABLE_POKEMON_FLAVOR = "pokemon_flavor"
    @Entity(tableName = TABLE_POKEMON_FLAVOR)
    data class PokemonFlavor(
        @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "id") val count: Int = 0,
        @ColumnInfo(name = "name_id") val id: Int,
        val language: String,
        val version: String,
        val text: String,
    )
    data class Pokemon(
        @Embedded val entry: PokemonEntry,
        @Relation(
            parentColumn = "id",
            entityColumn = "name_id",
            entity = PokemonName::class
        )
        val names: List<PokemonName>,
    )

    const val TABLE_ABILITY_NAME = "ability_name"
    @Entity(tableName = TABLE_ABILITY_NAME)
    data class AbilityName(
        @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "id") val count: Int = 0,
        @ColumnInfo(name = "name_id") val id: Int,
        val language: String,
        val name: String,
    )
    const val TABLE_ABILITY = "ability"
    @Entity(tableName = TABLE_ABILITY)
    data class AbilityEntry(
        @PrimaryKey val id: Int = 0,
        val name: String,
    )
    data class Ability(
        @Embedded val entry: AbilityEntry,
        @Relation(
            parentColumn = "id",
            entityColumn = "name_id",
            entity = AbilityName::class
        )
        val names: List<AbilityName>,
    )

    const val TABLE_TYPE_NAME = "type_name"
    @Entity(tableName = TABLE_TYPE_NAME)
    data class TypeName(
        @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "id") val count: Int = 0,
        @ColumnInfo(name = "name_id") val id: Int,
        val language: String,
        val name: String,
    )
    const val TABLE_TYPE = "type"
    @Entity(tableName = TABLE_TYPE)
    data class TypeEntry(
        @PrimaryKey val id: Int,
        val name: String,

    )
    data class Type(
        @Embedded val entry: TypeEntry,
        @Relation(
            parentColumn = "id",
            entityColumn = "name_id",
            entity = TypeName::class
        )
        val names: List<TypeName>,
    )

    const val TABLE_STAT_NAME = "stat_name"
    @Entity(tableName = TABLE_STAT_NAME)
    data class StatName(
        @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "id") val count: Int = 0,
        @ColumnInfo(name = "name_id") val id: Int,
        val language: String,
        val name: String,
    )
    const val TABLE_STAT = "stat"
    @Entity(tableName = TABLE_STAT)
    data class StatEntry(
        @PrimaryKey val id: Int,
        val name: String,
    )
    data class Stat(
        @Embedded val entry: StatEntry,
        @Relation(
            parentColumn = "id",
            entityColumn = "name_id",
            entity = StatName::class
        )
        val names: List<StatName>,
    )
}

