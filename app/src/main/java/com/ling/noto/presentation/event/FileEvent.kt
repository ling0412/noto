package com.ling.noto.presentation.event

sealed interface FileEvent {
    data class Edit(val key: String, val value: String = "") : FileEvent
    data object SwitchType : FileEvent
    data object Save : FileEvent
}