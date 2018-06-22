package org.bobstuff.parallaxscroll

/**
 * Created by bob
 */


import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.util.Log
import android.view.Display
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager

class ScrollImageView : View {
    var image: Bitmap? = null
        set(value) {
            field = value
            invalidate()
        }

    private var offsetX = 0f

    constructor(context: Context) : super(context)

    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet)

    fun scroll(total: Int, fullWidth: Int, maxScaleDifference: Float) {
        if (image == null) {
            return
        }
        var scale = (image!!.width-measuredWidth)/(fullWidth.toFloat()-measuredWidth)
        if (maxScaleDifference >= 0) {
            scale = Math.min(scale, maxScaleDifference)
        }

        offsetX = (total*scale)
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        if (image == null) {
            return
        }

        var constrainedOffsetX = offsetX
        if (0 > offsetX) {
            constrainedOffsetX = 0f
        }
        if (offsetX > image!!.width - measuredWidth) {
            constrainedOffsetX = (image!!.width - measuredWidth).toFloat()
        }

        val top = measuredHeight - image!!.height

        val paint = Paint()
        canvas.drawBitmap(image!!, -constrainedOffsetX, top.toFloat(), paint)
    }
}