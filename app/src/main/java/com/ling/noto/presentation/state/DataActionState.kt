package com.ling.noto.presentation.state

import androidx.compose.runtime.Stable

@Stable
data class DataActionState(
    val loading: Boolean = false,
    val progress: Float = 0f,
    val infinite: Boolean = false,
    val message: String = ""
)