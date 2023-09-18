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

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import coil.compose.rememberAsyncImagePainter
import com.bps_e.pokeapi.PokeApiHelper
import com.bps_e.pokepedia.R
import com.bps_e.pokepedia.db.Poke
import com.bps_e.pokepedia.service.DataAccessStateType
import kotlinx.coroutines.runBlocking

@Composable
fun PokeScreen(
    modifier: Modifier = Modifier,
    viewModel: PokeViewModel = hiltViewModel(),
) {
    val items = viewModel.poke.collectAsLazyPagingItems()
    val requestState by viewModel.requestState.collectAsState()

    Box(
        modifier
            .fillMaxSize()
            .padding(0.dp, 8.dp)) {
        LazyColumn(Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(
                count = items.itemCount,
                key = items.itemKey { it.entry.id },
            ) { index ->
                val poke = items[index]
                poke?.let {
                    PokeCard(poke = it, viewModel = viewModel)
                }
            }
        }

        AnimatedVisibility (
            visible = requestState.state == DataAccessStateType.Loading,
            enter = expandVertically(),
            exit = shrinkVertically()
        ) {
            Column(
                Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF, 0xFF, 0xFF, 0x80))) {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(0.dp, 8.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator()
                }
            }
        }
    }
}

@Composable
fun PokeCard(
    poke: Poke.Pokemon,
    viewModel: PokeViewModel,
    modifier: Modifier = Modifier,
) {
    poke.entry.apply {
        var localName by remember { mutableStateOf("") }
        var localGenera by remember { mutableStateOf("") }
        var localType by remember { mutableStateOf("") }
        var localFlavor by remember { mutableStateOf("") }
        var localAbility by remember { mutableStateOf("") }
        LaunchedEffect(Unit) {
            runBlocking {
                localName = viewModel.localPokemonName(name)
                localGenera = viewModel.localGenera(id)
                localFlavor = viewModel.localPokemonFlavor(id)

                types.forEach {
                    val local = viewModel.localTypeName(it)

                    if (localType.length > 0) localType += " / "
                    localType += local
                }

                abilities.forEach {
                    val split = it.split("@")
                    val local = viewModel.localAbilityName(split[0])

                    if (localAbility.length > 0) localAbility += " / "
                    if (split.size > 1) localAbility += "(夢)"
                    localAbility += local
                }
            }
        }
        val statTitle by remember { mutableStateOf(viewModel.statTitle) }

        OutlinedCard(
            modifier.padding(8.dp, 0.dp).fillMaxWidth()) {
            CardImage(poke)
            Divider(color = Color.LightGray)

            ListItem(
                overlineContent = {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("No. ${id.toString().padStart(4, '0')}")
                        Text("$localType")
                    }
                },
                headlineContent = {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Bottom) {
                        Text("$localName")
                        Text("$localGenera")
                    }
                },
                supportingContent = {
                    val style = MaterialTheme.typography.labelSmall

                    Divider(Modifier.padding(0.dp, 2.dp), color = Color.LightGray)
                    Text(localAbility, style = style)
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(String.format("高さ: %.1f m", (height * 0.1)), style = style)
                            Text(String.format("重さ: %.1f kg", (weight * 0.1)), style = style)
                        }
                        Text(PokeApiHelper.ConvertGanderRate(gender_rate))
                    }

                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Column(horizontalAlignment = Alignment.End) {
                            Text("${statTitle.hp}:${poke.entry.hp}", style = style)
                            Text("${statTitle.special_attack}:${poke.entry.special_attack}", style = style)
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text("${statTitle.attack}:${poke.entry.attack}", style = style)
                            Text("${statTitle.special_defense}:${poke.entry.special_defense}", style = style)
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text("${statTitle.defense}:${poke.entry.defense}", style = style)
                            Text("${statTitle.speed}:${poke.entry.speed}", style = style)
                        }
                    }
                    Divider(Modifier.padding(0.dp, 2.dp), color = Color.LightGray)
                    Text(localFlavor, modifier.fillMaxWidth(), style = style)
                }
            )
        }
    }
}

// io.coil-kt:coil-compose
@Composable
fun CardImage(poke: Poke.Pokemon, modifier: Modifier = Modifier) {
    poke.entry.apply {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
        ) {
            Image(
                painter = painterResource(R.drawable.back),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                contentScale = ContentScale.FillWidth
            )

            Row(
                Modifier
                    .padding(10.dp, 0.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                Box(
                    modifier = Modifier
                        .padding(0.dp, 10.dp)
                        .height(120.dp), contentAlignment = Alignment.BottomStart
                ) {
                    Image(
                        painter = rememberAsyncImagePainter(images["back_default"]!!),
                        contentDescription = null,
                        modifier = Modifier.size(80.dp, 80.dp),
                        contentScale = ContentScale.FillHeight
                    )
                }

                Box(
                    modifier = Modifier
                        .padding(0.dp, 10.dp)
                        .height(120.dp), contentAlignment = Alignment.TopStart
                ) {
                    Image(
                        painter = rememberAsyncImagePainter(images["front_default"]!!),
                        contentDescription = null,
                        modifier = Modifier.size(80.dp, 80.dp),
                        contentScale = ContentScale.FillHeight
                    )
                }
            }
        }
    }
}
