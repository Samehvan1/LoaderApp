package com.udacity

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.View
import android.view.animation.LinearInterpolator
import kotlin.properties.Delegates

class LoadingButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private var widthSize = 0
    private var heightSize = 0
    private var inProgress = false
    private var downProgress: Double = 0.0
    private var backColor = Color.CYAN
    private var inActionColor = Color.BLUE
    private var valueAnimator = ValueAnimator()
    private var curAngle=0
    private var curTxt: String = ""
    private var buttonState: ButtonState by Delegates.observable<ButtonState>(ButtonState.Completed) { p, old, new ->
        if (old != new) {
            valueAnimator.cancel()
            when (new) {
                ButtonState.Completed -> {
                    inProgress = false
                    curTxt = context.getString(R.string.button_name)
                    //isEnabled=true
                    upDateProgress(0.0)
                }
                ButtonState.Loading -> {
                    inProgress = true
                    curTxt = context.getString(R.string.button_loading)
                    valueAnimator.start()
                    //isEnabled=false
                    upDateProgress(0.01)
                }
                ButtonState.Failed -> {
                    inProgress = false
                    curTxt = context.getString(R.string.button_name)
                    //isEnabled=true
                    upDateProgress(0.5)
                }
            }
        }
    }

    fun updateState(newState: ButtonState) {
        buttonState = newState
    }

    init {
        isClickable = true
        downProgress = 0.0
        curTxt = context.getString(R.string.button_name)
        valueAnimator = ValueAnimator.ofInt(0, 360).apply {
            duration = 500
            interpolator = LinearInterpolator()
            addUpdateListener { curAngle= animatedValue as Int
                upDateProgress(curAngle.toDouble()) }
        }
    }

    fun upDateProgress(pro: Double) {
        downProgress = pro
        invalidate()
    }

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        textAlign = Paint.Align.CENTER
        textSize = 40.0f
        typeface = Typeface.create("", Typeface.BOLD)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        paint.color = backColor
        canvas?.drawRect(
            0.0f,
            0.0f,
            width.toFloat(),
            height.toFloat(),
            paint
        )
        paint.color = inActionColor
        canvas?.drawRect(0.0f, 0.0f, (widthSize/360f * downProgress).toFloat(), height.toFloat(), paint)
        paint.color = Color.BLACK
        canvas?.drawText(curTxt, width.toFloat() / 2, height.toFloat() / 2, paint)
        paint.color=Color.YELLOW
        var rectF=RectF(width.toFloat()-250f,height.toFloat()/4,width.toFloat()-100f,height.toFloat()/1.5f)
        canvas?.drawArc(rectF,180f,curAngle.toFloat()*2f,true,paint)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val minw: Int = paddingLeft + paddingRight + suggestedMinimumWidth
        val w: Int = resolveSizeAndState(minw, widthMeasureSpec, 1)
        val h: Int = resolveSizeAndState(
            MeasureSpec.getSize(w),
            heightMeasureSpec,
            0
        )
        widthSize = w
        heightSize = h
        setMeasuredDimension(w, h)
    }

}