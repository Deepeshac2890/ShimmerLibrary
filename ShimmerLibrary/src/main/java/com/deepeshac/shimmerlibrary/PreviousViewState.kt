package com.deepeshac.shimmerlibrary

/**
 * This data class is used to store the previous view state
 * which is used by [ShimmerHelper] to restore to older state once
 * loading is completed.
 */
data class PreviousViewState(
    var background: Any,
    var isClickable: Boolean
)
