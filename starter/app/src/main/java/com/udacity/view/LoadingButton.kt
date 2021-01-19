package com.udacity.view

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import android.view.animation.DecelerateInterpolator
import androidx.core.animation.doOnEnd
import com.udacity.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber
import kotlin.properties.Delegates

class LoadingButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private var progress = 0
    private var widthSize = 0
    private var heightSize = 0

    private val downloadTextPosition = PointF(0.0f, 0.0f)
    private val downloadTextString = context.getString(R.string.download)

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        textAlign = Paint.Align.CENTER
        textSize = 55.0f
        typeface = Typeface.create("", Typeface.BOLD)
    }

    private val valueAnimator = ValueAnimator()

    private var buttonState: ButtonState by Delegates.observable<ButtonState>(ButtonState.Completed) { p, old, new ->

    }

    private val scope = CoroutineScope(Dispatchers.Default)

    init {
        this.isClickable = true
    }

    private fun onStartProgressAnimation() {
        Timber.i("onStartProgressAnimation")
        valueAnimator.apply {
            setIntValues(0, 100)
            duration = 2500
            interpolator = DecelerateInterpolator()
            addUpdateListener {
                progress = it.animatedValue as Int
                invalidate()
            }

            doOnEnd {
                scope.launch {
                    delay(500)
                    progress = 0
                    invalidate()
                }
            }

            start()
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        drawDownloadButton(canvas)
        drawLoadingButton(canvas)
        drawLoadingCircle(canvas)
    }

    private fun drawDownloadButton(canvas: Canvas?) {
        paint.color = context.getColor(R.color.colorPrimary)
        canvas?.drawRect(0F, 0F, widthSize.toFloat(), heightSize.toFloat(), paint)

        paint.color = Color.WHITE
        canvas?.drawText(downloadTextString, downloadTextPosition.x, downloadTextPosition.y, paint)
    }

    private fun drawLoadingButton(canvas: Canvas?) {
        paint.color = context.getColor(R.color.colorPrimaryDark)

        val progressWidth = widthSize * (progress / 100F)
        canvas?.drawRect(0F, 0F, progressWidth, heightSize.toFloat(), paint)
    }

    private fun drawLoadingCircle(canvas: Canvas?) {
        paint.color = context.getColor(R.color.colorAccent)

        val progressAngle = 360 * (progress / 100F)
        val radius = 48
        val diameter = radius * 2
        val margin = 16
        val left = widthSize - diameter - margin
        val top = (heightSize - diameter + margin) / 2
        canvas?.drawArc(
            left.toFloat(), top.toFloat(),
            (left + diameter).toFloat(), (top + diameter).toFloat(), 0F, progressAngle, true, paint
        )
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val minw: Int = paddingLeft + paddingRight + suggestedMinimumWidth
        val w: Int = resolveSizeAndState(minw, widthMeasureSpec, 1)
        val h: Int = resolveSizeAndState(MeasureSpec.getSize(w), heightMeasureSpec, 0)

        widthSize = w
        heightSize = h
        setMeasuredDimension(w, h)

        downloadTextPosition.x = w.toFloat() / 2
        downloadTextPosition.y = h.toFloat() / 2
    }

    override fun performClick(): Boolean {
        super.performClick()
        Timber.i("performClick")
        onStartProgressAnimation()

        return true
    }
}