package com.ling.noto.domain.usecase

sealed class OrderType {
    data object Ascending : OrderType()
    data object Descending : OrderType()
}