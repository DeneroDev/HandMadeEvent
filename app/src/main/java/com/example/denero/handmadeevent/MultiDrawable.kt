package com.example.denero.handmadeevent

import android.graphics.Canvas
import android.graphics.ColorFilter
import android.graphics.PixelFormat
import android.graphics.drawable.Drawable

/**
 * Created by Denero on 16.03.2018.
 */
class MultiDrawable(var mDrawables:List<Drawable>):Drawable() {
    override fun draw(canvas: Canvas) {
        if (mDrawables.size == 1) {
            mDrawables.get(0).draw(canvas)
            return
        }
        val width = bounds.width()
        val height = bounds.height()

        canvas.save()
        canvas.clipRect(0, 0, width, height)

        if (mDrawables.size == 2 || mDrawables.size == 3) {
            // Paint left half
            canvas.save()
            canvas.clipRect(0, 0, width / 2, height)
            canvas.translate((-width / 4).toFloat(), 0f)
            mDrawables.get(0).draw(canvas)
            canvas.restore()
        }
        if (mDrawables.size == 2) {
            // Paint right half
            canvas.save()
            canvas.clipRect(width / 2, 0, width, height)
            canvas.translate((width / 4).toFloat(), 0f)
            mDrawables.get(1).draw(canvas)
            canvas.restore()
        } else {
            // Paint top right
            canvas.save()
            canvas.scale(.5f, .5f)
            canvas.translate(width.toFloat(), 0f)
            mDrawables.get(1).draw(canvas)

            // Paint bottom right
            canvas.translate(0f, height.toFloat())
            mDrawables.get(2).draw(canvas)
            canvas.restore()
        }

        if (mDrawables.size >= 4) {
            // Paint top left
            canvas.save()
            canvas.scale(.5f, .5f)
            mDrawables.get(0).draw(canvas)

            // Paint bottom left
            canvas.translate(0f, height.toFloat())
            mDrawables.get(3).draw(canvas)
            canvas.restore()
        }

        canvas.restore()
    }

    override fun setAlpha(alpha: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getOpacity(): Int {
        return PixelFormat.UNKNOWN
    }

    override fun setColorFilter(colorFilter: ColorFilter?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}