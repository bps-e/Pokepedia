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
package com.bps_e.pokepedia.ui.poke

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.bps_e.pokepedia.data.PokeRepository
import com.bps_e.pokepedia.data.StateTitle
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PokeViewModel @Inject constructor(
    private val repo: PokeRepository,
) : ViewModel() {
    val poke = repo.getPagingStream().cachedIn(viewModelScope)
    val requestState = repo.getRequestState()

    suspend fun localPokemonName(name: String): String = repo.localPokemonName(name)
    suspend fun localGenera(id: Int): String = repo.localGenera(id)
    suspend fun localPokemonFlavor(id: Int): String = repo.localPokemonFlavor(id)
    suspend fun localTypeName(name: String): String = repo.localTypeName(name)
    suspend fun localAbilityName(name: String): String = repo.localAbilityName(name)

    val statTitle = StateTitle()
    init {
        viewModelScope.launch {
            repo.RequestStat()
            repo.RequestType()

            statTitle.hp = repo.localStatName("hp")
            statTitle.attack = repo.localStatName("attack")
            statTitle.defense = repo.localStatName("defense")
            statTitle.special_attack = repo.localStatName("special-attack")
            statTitle.special_defense = repo.localStatName("special-defense")
            statTitle.speed = repo.localStatName("speed")
        }
    }
}