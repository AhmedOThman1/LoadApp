package com.othman.loadapp

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import android.view.animation.LinearInterpolator
import android.view.animation.OvershootInterpolator
import kotlin.math.min
import kotlin.math.max
import kotlin.properties.Delegates

class LoadingButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private var widthSize = 0
    private var heightSize = 0
    private var circleProgress = 0
    private var bgProgress = 0

    private var rectBackgroundColor = Color.BLACK
    private var progreesRectColor = Color.BLACK
    private var radius = 8f

    private var circleMargin = 20f
    private val circleRadius = 20f
    private var circleColor = Color.GREEN

    private var textColor = Color.WHITE
    private var textSize = 20f

    private var IDLE_TEXT = "Download"
    private var Downloading_TEXT = "We Are Loading"
    private lateinit var text: String

    private var textPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var rectPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var progressRectPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var circleRectPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var rect: RectF = RectF()
    private var progressRect: RectF = RectF()
    private var CircleRect: RectF = RectF()

    //    val animationEnd = 1000
    val animationEnd = 360 * 4

    private val progressValueAnimator = ValueAnimator.ofInt(0, animationEnd).apply {
        duration = 1500
        addUpdateListener {
            bgProgress = it.animatedValue as Int
            circleProgress = bgProgress / 2;
            if (circleProgress <= 720) {
                circleProgress -= 360;
            }
            interpolator = LinearInterpolator()
            repeatCount = ValueAnimator.INFINITE
            repeatMode = ValueAnimator.RESTART
            invalidate()
        }
    }

    enum class ButtonState {
        IDLE,
        DOWNLOADING
    }

    var buttonState: ButtonState by Delegates.observable<ButtonState>(ButtonState.IDLE) { _, _, new ->
        when (new) {
            ButtonState.IDLE -> {
                progressValueAnimator.cancel()
                text = IDLE_TEXT
                invalidate()
            }
            ButtonState.DOWNLOADING -> {
                progressValueAnimator.start()
                text = Downloading_TEXT
                invalidate()
            }
        }
    }


    init {
        attrs?.let {
            val arr =
                context.obtainStyledAttributes(attrs, R.styleable.LoadingButton, defStyleAttr, 0)
            rectBackgroundColor = arr.getColor(
                R.styleable.LoadingButton_lb_background_color,
                context.resources.getColor(R.color.colorPrimary)
            )
            progreesRectColor = arr.getColor(
                R.styleable.LoadingButton_lb_progress_color,
                context.resources.getColor(R.color.colorPrimary)
            )
            textColor = arr.getColor(
                R.styleable.LoadingButton_lb_text_color,
                context.resources.getColor(R.color.white)
            )
            circleColor = arr.getColor(
                R.styleable.LoadingButton_lb_circle_color,
                context.resources.getColor(R.color.white)
            )
            radius = arr.getDimension(R.styleable.LoadingButton_lb_radius, 8f)
            textSize = arr.getDimension(R.styleable.LoadingButton_lb_textSize, 20f)
            circleMargin = arr.getDimension(R.styleable.LoadingButton_lb_circle_margin, 20f)
            IDLE_TEXT = arr.getString(R.styleable.LoadingButton_lb_idle_text).toString()
            Downloading_TEXT =
                arr.getString(R.styleable.LoadingButton_lb_downloading_text).toString()


        }

        rectPaint.color = rectBackgroundColor
        progressRectPaint.color = progreesRectColor
        circleRectPaint.color = circleColor

        textPaint.color = textColor
        textPaint.style = Paint.Style.FILL;
        textPaint.textAlign = Paint.Align.CENTER
        textPaint.textSize = textSize;
        text = IDLE_TEXT

    }

    var bounds = Rect()

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        //////////////////////////// Draw base background //////////////////////////////////////////
        rect.top = 0f
        rect.left = 0f
        rect.bottom = heightSize.toFloat()
        rect.right = widthSize.toFloat()
        canvas?.drawRoundRect(rect, radius, radius, rectPaint)
        ////////////////////////////////////////////////////////////////////////////////////////////

        //////////////////////////// Draw Progress background //////////////////////////////////////
        if (buttonState == ButtonState.DOWNLOADING) {
            val v = ((widthSize * bgProgress) / (animationEnd / 2))
            val start = max(0, v - widthSize).toFloat()
            val end = min(widthSize, v).toFloat()
            progressRect.top = 0f
            progressRect.left = start
            progressRect.bottom = heightSize.toFloat()
            progressRect.right = end
            canvas?.drawRoundRect(progressRect, radius, radius, progressRectPaint)
        }
        ////////////////////////////////////////////////////////////////////////////////////////////

        //////////////////////////// Draw Text /////////////////////////////////////////////////////
        val xPos = widthSize / 2
        val yPos = (heightSize / 2 - (textPaint.descent() + textPaint.ascent()) / 2).toInt()
        canvas?.drawText(text, xPos.toFloat(), yPos.toFloat(), textPaint)
        ////////////////////////////////////////////////////////////////////////////////////////////

        //////////////////////////// Draw ARC /////////////////////////////////////////////////////
        if (buttonState == ButtonState.DOWNLOADING) {
            textPaint.getTextBounds(text, 0, text.length, bounds);

            val circleXStart = (widthSize / 2) + (bounds.width() / 2) + circleRadius + circleMargin
            val circleYStart = heightSize / 2
            CircleRect = RectF(
                circleXStart - circleRadius,
                circleYStart - circleRadius,
                circleXStart + circleRadius,
                circleYStart + circleRadius
            )
            canvas?.drawArc(
                CircleRect,
                270f, circleProgress.toFloat(), true,
                circleRectPaint
            )

//            canvas?.drawCircle(
//                circleXStart.toFloat(),
//                circleYStart.toFloat(),
//                circleRadius,
//                circleRectPaint
//            )
        }
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