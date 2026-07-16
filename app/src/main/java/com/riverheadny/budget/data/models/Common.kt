package com.riverheadny.budget.data.models

import kotlinx.serialization.Serializable

@Serializable
data class SourceRef(
    val title: String? = null,
    val url: String? = null,
)
