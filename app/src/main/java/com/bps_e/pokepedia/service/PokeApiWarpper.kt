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
package com.bps_e.pokepedia.service

import android.util.Log
import androidx.room.withTransaction
import com.bps_e.pokeapi.PokeApi
import com.bps_e.pokeapi.PokeApiData
import com.bps_e.pokepedia.db.AppDatabase
import com.bps_e.pokepedia.db.Poke
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.runBlocking

class PokeApiWrapper {
    companion object {
        val RequestState = MutableStateFlow(DataAccessState(DataAccessStateType.None, ""))
        val target_lang = listOf("ja-Hrkt", "ja", "en")

        private fun isTargetLanguage(language: String) = (language == "ja" || language == "en" || language == "ja-Hrkt")

        suspend fun RequestPokemon(db: AppDatabase, limit: Int, offset: Int) {
            val dao = db.dao()
            if (dao.count() > offset) return
            RequestState.emit(DataAccessState(DataAccessStateType.Loading, "RequestPokemon"))
            PokeApi.Pokemon(limit = limit, offset = offset, onError = {
                Log.e("App", it.toString())
            }) { res ->
                runBlocking {
                    res.results.forEach { ret ->
                        RequestPokemon(db, ret.url)
                    }
                }
            }
            RequestState.emit(DataAccessState(DataAccessStateType.Success, "RequestPokemon"))
        }

        private suspend fun insertSpecies(db: AppDatabase, species: PokeApiData.PokemonSpecies) {
            val names = species.names.filter {
                isTargetLanguage(it.language.name)
            }.map {
                Poke.PokemonName(id = species.id, language = it.language.name, name = it.name)
            }

            val texts = species.flavor_text_entries.filter {
                isTargetLanguage(it.language.name)
            }.map {
                Poke.PokemonFlavor(id = species.id, language = it.language.name, version = it.version.name, text = it.flavor_text)
            }

            val generas = species.genera.filter {
                isTargetLanguage(it.language.name)
            }.map {
                Poke.Genera(id = species.id, language = it.language.name, genus = it.genus)
            }

            db.withTransaction {
                db.pokemonNameDao().insert(names)
                db.pokemonFlavorDao().insert(texts)
                db.generaDao().insert(generas)
            }
        }

        private suspend fun RequestAbility(db: AppDatabase, abilities: List<PokeApiData.Pokemon.Ability>) {
            abilities.forEach { abi ->
                val name = abi.ability.name
                val check = db.abilityDao().get(name)
                if (check.isEmpty()) {
                    PokeApi.Ability(name = "", api = abi.ability.url, onError = {
                        Log.e("App", it.toString())
                    }) { data ->
                        runBlocking {
                            val abilities = data.names.filter {
                                isTargetLanguage(it.language.name)
                            }.map {
                                Poke.AbilityName(id = data.id, language = it.language.name, name = it.name)
                            }

                            db.withTransaction {
                                db.abilityDao().insert(Poke.AbilityEntry(data.id, data.name))
                                db.abilityNameDao().insert(abilities)
                            }
                        }
                    }
                }
            }
        }

        private suspend fun RequestPokemon(db: AppDatabase, url: String) {
            PokeApi.Pokemon(name = "", api = url, onError = {
                Log.e("App", it.toString())
            }) { poke ->
                runBlocking {
                    var species: PokeApiData.PokemonSpecies? = null
                    PokeApi.PokemonSpecies(name = "", api = poke.species.url, onError = {
                        Log.e("App", it.toString())
                    }) {
                        species = it
                    }

                    species?.let {
                        insertSpecies(db, it)
                    }

                    RequestAbility(db, poke.abilities)

                    //
                    val images = mutableMapOf<String, String>()
                    images["front_default"] = poke.sprites.front_default ?: ""
                    images["front_female"] = poke.sprites.front_female ?: ""
                    images["front_shiny"] = poke.sprites.front_shiny ?: ""
                    images["front_shiny_female"] = poke.sprites.front_shiny_female ?: ""
                    images["back_default"] = poke.sprites.back_default ?: ""
                    images["back_female"] = poke.sprites.back_female ?: ""
                    images["back_shiny"] = poke.sprites.back_shiny ?: ""
                    images["back_shiny_female"] = poke.sprites.back_shiny_female ?: ""
                    images["official"] = poke.sprites.other.official_artwork.front_default ?: ""
                    images["official_shiny"] = poke.sprites.other.official_artwork.front_shiny ?: ""

                    val types = poke.types.map { it.type.name }
                    val abilities = poke.abilities.map {
                        if (it.is_hidden) "${it.ability.name}@hidden" else it.ability.name
                    }
                    db.withTransaction {
                        db.dao().insert(
                            Poke.PokemonEntry(
                                id = poke.id,
                                name = poke.name,

                                types = types,
                                abilities = abilities,

                                height = poke.height,
                                weight = poke.weight,

                                hp = poke.stats.filter { it.stat.name == "hp" }.first().base_stat,
                                attack = poke.stats.filter { it.stat.name == "attack" }.first().base_stat,
                                defense = poke.stats.filter { it.stat.name == "defense" }.first().base_stat,
                                special_attack = poke.stats.filter { it.stat.name == "special-attack" }.first().base_stat,
                                special_defense = poke.stats.filter { it.stat.name == "special-defense" }.first().base_stat,
                                speed = poke.stats.filter { it.stat.name == "speed" }.first().base_stat,

                                images = images,

                                gender_rate = if (species != null) species!!.gender_rate else -1
                            )
                        )
                    }
                }
            }
        }

        suspend fun RequestType(db: AppDatabase) {
            if (db.typeDao().count() > 0) return
            RequestState.emit(DataAccessState(DataAccessStateType.Loading, "RequestType"))

            var resource: PokeApiData.ResourceList? = null
            PokeApi.Type(onError = {
                Log.e("App", it.toString())
            }) {
                resource = it
            }

            resource?.results?.forEach { result ->
                PokeApi.Type(name = "", api = result.url, onError = {
                    Log.e("App", it.toString())
                }) { data ->
                    runBlocking {
                        val names =
                            data.names.filter {
                                isTargetLanguage(it.language.name)
                            }.map {
                                Poke.TypeName(id = data.id, language = it.language.name, name = it.name)
                            }

                        db.withTransaction {
                            db.typeNameDao().insert(names)
                            db.typeDao().insert(Poke.TypeEntry(data.id, data.name))
                        }
                    }
                }
            }
            RequestState.emit(DataAccessState(DataAccessStateType.Success, "RequestType"))
        }

        suspend fun RequestStat(db: AppDatabase) {
            if (db.statDao().count() > 0) return
            RequestState.emit(DataAccessState(DataAccessStateType.Loading, "RequestStat"))

            var resource: PokeApiData.ResourceList? = null
            PokeApi.Stat(onError = {
                Log.e("App", it.toString())
            }) {
                resource = it
            }

            resource?.results?.forEach { result ->
                PokeApi.Stat(name = "", api = result.url, onError = {
                    Log.e("App", it.toString())
                }) { data ->
                    runBlocking {
                        val names =
                            data.names.filter {
                                isTargetLanguage(it.language.name)
                            }.map {
                                Poke.StatName(id = data.id, language = it.language.name, name = it.name)
                            }

                        db.withTransaction {
                            db.statNameDao().insert(names)
                            db.statDao().insert(Poke.StatEntry(data.id, data.name))
                        }
                    }
                }
            }
            RequestState.emit(DataAccessState(DataAccessStateType.Success, "RequestStat"))
        }

        suspend fun localPokemonName(db: AppDatabase, name: String): String = coroutineScope {
            return@coroutineScope async(Dispatchers.IO) {
                var result = name
                try {
                    val type = db.dao().getLocalName(name).first()
                    run loop@{
                        target_lang.forEach { lang ->
                            result = type.names.filter { it.language == lang }.map { it.name }.first()
                            return@loop
                        }
                    }
                } catch (_: Exception) {}
                result
            }.await()
        }

        suspend fun localTypeName(db: AppDatabase, name: String): String = coroutineScope {
            return@coroutineScope async(Dispatchers.IO) {
                var result = name
                try {
                    val type = db.typeDao().getLocalName(name).first()
                    run loop@{
                        target_lang.forEach { lang ->
                            result = type.names.filter { it.language == lang }.map { it.name }.first()
                            return@loop
                        }
                    }
                } catch (_: Exception) {}
                result
            }.await()
        }

        suspend fun localStatName(db: AppDatabase, name: String): String = coroutineScope {
            return@coroutineScope async(Dispatchers.IO) {
                var result = name
                try {
                    val stat = db.statDao().getLocalName(name).first()
                    run loop@{
                        target_lang.forEach { lang ->
                            result = stat.names.filter { it.language == lang }.map { it.name }.first()
                            return@loop
                        }
                    }
                } catch (_: Exception) {}
                result
            }.await()
        }

        suspend fun localGenera(db: AppDatabase, id: Int): String = coroutineScope {
            return@coroutineScope async(Dispatchers.IO) {
                var result = ""
                try {
                    val genera = db.generaDao().getLocal(id)
                    run loop@{
                        target_lang.forEach { lang ->
                            result = genera.filter { it.language == lang }.map { it.genus }.first()
                            return@loop
                        }
                    }
                } catch (_: Exception) {}
                result
            }.await()
        }

        suspend fun localPokemonFlavor(db: AppDatabase, id: Int): String = coroutineScope {
            return@coroutineScope async(Dispatchers.IO) {
                var result = ""
                try {
                    val flavor = db.pokemonFlavorDao().getLocal(id)
                    run loop@{
                        target_lang.forEach { lang ->
                            // バージョン順に並んでいるとして最新を返す
                            result = flavor.filter { it.language == lang }.map { "${it.version}: ${it.text}" }.last()
                            return@loop
                        }
                    }
                } catch (_: Exception) {}
                result
            }.await()
        }

        suspend fun localAbilityName(db: AppDatabase, name: String): String = coroutineScope {
            return@coroutineScope async(Dispatchers.IO) {
                var result = name
                try {
                    val ability = db.abilityDao().getLocalName(name).first()
                    run loop@{
                        target_lang.forEach { lang ->
                            result = ability.names.filter { it.language == lang }.map { it.name }.last()
                            return@loop
                        }
                    }
                } catch (_: Exception) {}
                result
            }.await()
        }

    }
}
