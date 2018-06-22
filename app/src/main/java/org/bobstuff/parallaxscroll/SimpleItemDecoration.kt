package org.bobstuff.parallaxscroll

import android.graphics.Rect
import android.support.v7.widget.RecyclerView
import android.view.View

/**
 * Created by bob
 */

class SimpleItemDecoration(private val dividerSizeInPixels: Int): RecyclerView.ItemDecoration() {

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State?) {
        val position = parent.getChildAdapterPosition(view)
        val itemCount = parent.adapter.itemCount

        getHorizontalItemOffsets(outRect, position, itemCount)
    }

    private fun getHorizontalItemOffsets(outRect: Rect, position: Int, itemCount: Int) {
        when (position) {
            0 -> outRect.set(dividerSizeInPixels, 0, dividerSizeInPixels/2, 0)
            itemCount - 1 -> outRect.set(dividerSizeInPixels/2, 0, dividerSizeInPixels, 0)
            else -> outRect.set(dividerSizeInPixels/2, 0, dividerSizeInPixels/2, 0)
        }
    }

}