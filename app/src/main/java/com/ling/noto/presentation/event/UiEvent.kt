package com.ling.noto.presentation.event

sealed interface UiEvent {
    data object NavigateBack : UiEvent
}