package com.deepeshac.shimmerlibrary

import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.view.View
import android.view.ViewGroup
import androidx.core.view.children
import com.facebook.shimmer.ShimmerFrameLayout
import java.util.LinkedList
import java.util.Queue

/**
 * This is a Shimmer Helper which will help simplifying the shimmer
 * effect in the activities/fragments. This serves as a plug and play
 * solution.
 */
class ShimmerHelper(private val parentShimmerGroup: ShimmerFrameLayout) {
    private val viewMap = mutableMapOf<View, PreviousViewState>()

    private fun isViewEligibleForShimmer(view: View?): Boolean = view != null && (view.visibility == View.VISIBLE)

    /**
     * This will remember the views previous background state so that after loading
     * is completed we can revert to that state.
     */
    private fun rememberViewStates() {
        val queue: Queue<View> = LinkedList()
        queue.addAll(parentShimmerGroup.children)
        while (queue.isNotEmpty()) {
            val view = queue.poll()
            if (isViewEligibleForShimmer(view)) {
                val isClickable = view!!.hasOnClickListeners()
                when (view.background) {
                    null -> {
                        // Do nothing
                    }

                    is ColorDrawable -> {
                        val prevViewState = PreviousViewState((view.background as ColorDrawable).color, isClickable)
                        viewMap[view] = prevViewState
                    }

                    else -> {
                        val prevViewState = PreviousViewState(view.background, isClickable)
                        viewMap[view] = prevViewState
                    }
                }
                if (view is ViewGroup) {
                    queue.addAll(view.children)
                }
            }
        }
    }

    /**
     * This will toggle the shimmer effect for the views contained within
     * parentShimmerGroup.
     */
    fun toggleLoading(isLoading: Boolean) {
        if (isLoading) {
            parentShimmerGroup.showShimmer(false)
            // Refreshing the view states here as the UI might have changed.
            rememberViewStates()
        } else {
            parentShimmerGroup.hideShimmer()
        }
        for (entry in viewMap) {
            if (isLoading) {
                entry.key.setBackgroundColor(
                    parentShimmerGroup.context.getColor(
                        if (entry.key is ViewGroup)
                            androidx.appcompat.R.color.material_grey_600 else androidx.appcompat.R.color.material_grey_300
                    )
                )
                if (entry.value.isClickable) {
                    entry.key.isClickable = false
                }
            } else {
                if (entry.value.background is Drawable) {
                    entry.key.background = entry.value.background as Drawable
                } else if (entry.value.background is Int) {
                    entry.key.setBackgroundColor(entry.value.background as Int)
                }

                if (entry.value.isClickable) {
                    entry.key.isClickable = entry.value.isClickable
                }
            }
        }
        if (!isLoading) {
            // Clearing the view map to avoid leaks.
            viewMap.clear()
        }
    }
}
