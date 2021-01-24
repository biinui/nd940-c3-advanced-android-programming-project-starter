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
    private var loadingButtonWidth = 0F
    private var widthSize = 0F
    private var heightSize = 0F

    private val downloadTextPosition = PointF(0.0f, 0.0f)
    private val downloadTextString = context.getString(R.string.download)
    private val loadingTextString = context.getString(R.string.button_loading)

    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        textAlign = Paint.Align.CENTER
        textSize = 55.0f
        typeface = Typeface.create("", Typeface.BOLD)
        color = Color.WHITE
    }

    private val downloadButtonPaint = Paint(textPaint).apply {
        color = context.getColor(R.color.colorPrimary)
    }
    private val loadingButtonPaint = Paint(textPaint).apply {
        color = context.getColor(R.color.colorPrimaryDark)
    }
    private val loadingCirclePaint = Paint(textPaint).apply {
        color = context.getColor(R.color.colorAccent)
    }

    private val valueAnimator = ValueAnimator()

    private var buttonState: ButtonState by Delegates.observable<ButtonState>(ButtonState.Completed) { p, old, new ->
        Timber.i("$p, $old, $new")
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
                calculateLoadingButton()
                calculateLoadingCircle()
                invalidate()
            }

            doOnEnd {
                scope.launch {
                    delay(888)
                    progress = 0
                    calculateLoadingButton()
                    calculateLoadingCircle()
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
        canvas?.drawRect(0F, 0F, widthSize, heightSize, downloadButtonPaint)
        canvas?.drawText(downloadTextString, downloadTextPosition.x, downloadTextPosition.y, textPaint)
    }

    private fun calculateLoadingButton() {
        loadingButtonWidth = widthSize * (progress / 100F)
    }

    private fun drawLoadingButton(canvas: Canvas?) {
        canvas?.drawRect(0F, 0F, loadingButtonWidth, heightSize, loadingButtonPaint)
        canvas?.drawText(loadingTextString, downloadTextPosition.x, downloadTextPosition.y, textPaint)
    }

    private var progressAngle = 0F
    private val radius = 48
    private val diameter = radius * 2
    private var loadingCircleLeft = 48F
    private var loadingCircleTop = 48F
    private var loadingCircleRight = 48F
    private var loadingCircleBottom = 48F
    private val margin = 16F

    private fun calculateLoadingCircle() {
        progressAngle = 360 * (progress / 100F)
    }

    private fun setLoadingCirclePosition() {
        loadingCircleLeft = widthSize - diameter - margin
        loadingCircleTop = (heightSize / 2) - radius
        loadingCircleRight = loadingCircleLeft + diameter
        loadingCircleBottom = loadingCircleTop + diameter
    }

    private fun drawLoadingCircle(canvas: Canvas?) {
        canvas?.drawArc(loadingCircleLeft, loadingCircleTop, loadingCircleRight, loadingCircleBottom, 0F, progressAngle, true, loadingCirclePaint)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val minw: Int = paddingLeft + paddingRight + suggestedMinimumWidth
        val w: Int = resolveSizeAndState(minw, widthMeasureSpec, 1)
        val h: Int = resolveSizeAndState(MeasureSpec.getSize(w), heightMeasureSpec, 0)

        widthSize = w.toFloat()
        heightSize = h.toFloat()
        setMeasuredDimension(w, h)

        downloadTextPosition.x = w.toFloat() / 2
        downloadTextPosition.y = h.toFloat() / 2

        setLoadingCirclePosition()
    }

    override fun performClick(): Boolean {
        super.performClick()
        Timber.i("performClick")
        onStartProgressAnimation()

        return true
    }
}