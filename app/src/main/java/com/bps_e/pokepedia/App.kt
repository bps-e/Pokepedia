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
package com.bps_e.pokepedia

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.bps_e.pokepedia.ui.poke.PokeScreen
import com.bps_e.pokepedia.ui.theme.AppTheme

object App {
    private val startDestination = NavRoute.Poke.name
    enum class NavRoute {
        Poke,
    }

    private fun NavGraphBuilder.navGraph(route: NavRoute, onBack: (String) -> Unit, onNavigate: (String) -> Unit) {
        composable(route = route.name) {
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                containerColor = MaterialTheme.colorScheme.background,
                topBar = { topAppBar(route, onBack) },
            ) { paddingValues ->
                when(route) {
                    NavRoute.Poke -> PokeScreen(Modifier.padding(paddingValues))
                }
            }
        }
    }

//region Common
    @Composable
    operator fun invoke(appState: AppState = rememberAppState()) {
        AppTheme {
            AppNavHost(appState.navController, appState::onBack, appState::onNavigate)
        }
    }

    @Composable
    fun AppNavHost(navController: NavHostController, onBack: (String) -> Unit, onNavigate: (String) -> Unit) {
        NavHost(navController = navController, startDestination = startDestination) {
            NavRoute.values().forEach { navGraph(it, onBack, onNavigate) }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun topAppBar(route: NavRoute, onBack: (String) -> Unit) {
        CenterAlignedTopAppBar(
            title = {
                val title = if (route.name == startDestination) stringResource(R.string.app_name) else route.name
                Text(title)
            },
            navigationIcon = {
                if (route.name != startDestination) {
                    IconButton(onClick = { onBack("") }) {
                        Icon(Icons.Default.ArrowBack, null, tint = MaterialTheme.colorScheme.onBackground)
                    }
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primary)
        )
    }

    @Stable
    class AppState(val navController: NavHostController) {
        fun onNavigate(route: String) {
            navController.navigate(route)
        }

        fun onBack(route: String = "") {
            if (route.isEmpty()) navController.navigateUp()
            else navController.popBackStack(route, inclusive = false, saveState = false)
        }
    }
    @Composable fun rememberAppState(navController: NavHostController = rememberNavController()): AppState = remember(navController) { AppState(navController) }
//endregion Common
}
