package com.riverheadny.budget.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.riverheadny.budget.data.LoadState

/** Shared loading/error scaffolding so every asset-backed screen behaves consistently. */
@Composable
fun <T> LoadStateView(
    state: LoadState<T>,
    content: @Composable (T) -> Unit,
) {
    when (state) {
        is LoadState.Loading -> Box(Modifier.fillMaxSize().padding(32.dp), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        is LoadState.Error -> Box(Modifier.fillMaxSize().padding(32.dp), contentAlignment = Alignment.Center) {
            Text("Couldn't load this data: ${state.message}", color = Color.DarkGray)
        }
        is LoadState.Success -> content(state.data)
    }
}
