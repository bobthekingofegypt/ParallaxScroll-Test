package org.bobstuff.parallaxscroll

import android.content.Context
import android.graphics.Bitmap
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import android.widget.FrameLayout

/**
 * Created by bob
 */

class ParallaxScrollView: FrameLayout {
    lateinit var scrollImageView: ScrollImageView
    lateinit var recyclerView: RecyclerView
    var itemWidth = 0
    var decorationWidth = 0
    /**
     * Controls how far the background image can move in comparison to the recyclerview.  If all the
     * recyclerview content is 100px but the background image is 5000px a 10px scroll on recycler
     * results in a huge scroll of the background image, this value lets you limit that to a maximum.
     *
     * Scale is calculated as:
     * (<image width> - <image views measuredWidth>) /
     *      (<width of all items and decoration in recyclerview> - <image views measuredWidth>)
     */
    var maxScaleDifference = -1f

    constructor(context: Context) : super(context)

    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet)

    override fun onFinishInflate() {
        super.onFinishInflate()

        recyclerView = getChildAt(0) as RecyclerView
        recyclerView.addOnScrollListener(object: RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                scrollBackgroundImage()
                super.onScrolled(recyclerView, dx, dy)
            }
        })

        recyclerView.addItemDecoration(SimpleItemDecoration(decorationWidth))

        scrollImageView = ScrollImageView(context)
        scrollImageView.layoutParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT)
        addView(scrollImageView, 0)
    }

    fun setBackgroundImage(image: Bitmap) {
        scrollImageView.image = image
    }

    fun setRecyclerViewSizes(itemWidth: Int, decorationWidth: Int) {
        this.itemWidth = itemWidth
        this.decorationWidth = decorationWidth

        scrollBackgroundImage()
    }

    private fun scrollBackgroundImage() {
        if (recyclerView.adapter == null) {
            return
        }

        val totalWidth = (itemWidth * recyclerView.adapter.itemCount) +
                (recyclerView.adapter.itemCount * decorationWidth) +
                decorationWidth
        val llm = recyclerView.layoutManager as LinearLayoutManager
        val first = llm.findFirstVisibleItemPosition()
        val offset = llm.findViewByPosition(first).left

        val currentOffset = (first * itemWidth) + (first * decorationWidth) - offset

        scrollImageView.scroll(currentOffset, totalWidth, maxScaleDifference)
    }
}