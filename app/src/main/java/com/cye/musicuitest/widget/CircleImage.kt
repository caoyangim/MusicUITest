package com.cye.musicuitest.widget

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView
import com.cye.musicuitest.R


open class CircleImage :AppCompatImageView{

    private var borderColor:Int = 0
    private var borderWidth = 0f
    private var shape: Int? = 0
    private var shapeRadius: Float = 0f
    private var mHeight = 0
    private var mWidth = 0
    private var centerX: Float = 0f
    private var centerY: Float = 0f
    private var mBitmap: Bitmap? = null
    private val BITMAP_CONFIG = Bitmap.Config.ARGB_8888
    private val DEFAULT_DRAWABLE_DIMENSION = 2

    private lateinit var layerRectF: RectF
    private lateinit var shapeRectF: RectF
    private lateinit var bitmapPaint: Paint
    private lateinit var shapePaint: Paint
    private lateinit var borderPaint: Paint

    private var porterDuffXfermode: PorterDuffXfermode = PorterDuffXfermode(PorterDuff.Mode.DST_IN)

    constructor(context:Context):super(context)
    constructor(context:Context,attrs:AttributeSet):this(context,attrs,0)
    constructor(context:Context,attrs: AttributeSet,defStyleAttr:Int):super(context,attrs,defStyleAttr){
        readAttrs(attrs)
        init()
    }

    private fun init() {
        shapePaint = Paint(Paint.ANTI_ALIAS_FLAG)
        bitmapPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        borderPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        borderPaint.color = borderColor
        borderPaint.strokeWidth = borderWidth
        borderPaint.style = Paint.Style.STROKE
    }

    private fun readAttrs(attrs: AttributeSet) {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.CircleImage)
        shape = typedArray.getInt(R.styleable.CircleImage_shape, 0)
        borderColor = typedArray.getColor(R.styleable.CircleImage_border_color, 0xffff0000.toInt())
        borderWidth = typedArray.getDimension(R.styleable.CircleImage_border_width, 5f)
        typedArray.recycle()
    }

    @SuppressLint("DrawAllocation")
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        mHeight = MeasureSpec.getSize(heightMeasureSpec)
        mWidth = MeasureSpec.getSize(widthMeasureSpec)
        centerX = mWidth/2f
        centerY = mHeight/2f
        shapeRadius = Math.min(mHeight,mWidth).toFloat()

        layerRectF = RectF(0f, 0f, mWidth.toFloat(), mHeight.toFloat())
        shapeRectF = RectF(
            borderPaint.strokeWidth / 2f,
            borderPaint.strokeWidth / 2f,
            mWidth.toFloat() - borderPaint.strokeWidth / 2f,
            mHeight.toFloat() - borderPaint.strokeWidth / 2f
        )
    }

    override fun onDraw(canvas: Canvas?) {
        // 第一种方式 ： clipPath
//        canvas?.save()
//        canvas?.clipPath(path)
//        super.onDraw(canvas)
//        canvas?.restore()

        // 第二种：xfermode
        val layer = canvas?.saveLayer(layerRectF, null, Canvas.ALL_SAVE_FLAG)
        super.onDraw(canvas)
        bitmapPaint.xfermode = porterDuffXfermode
        canvas?.drawBitmap(getBitmap(), 0f, 0f, bitmapPaint)
        bitmapPaint.xfermode = null
        canvas?.restoreToCount(layer!!)
        if (borderWidth != 0f) {
            if (shape == 0) {
                canvas?.drawCircle(centerX, centerY, Math.min(mWidth, mHeight) / 2f - borderPaint.strokeWidth / 2f, borderPaint)
            } else if (shape == 1) {
                canvas?.drawRoundRect(shapeRectF, shapeRadius, shapeRadius, borderPaint)
            }
        }
    }

    private fun getBitmap(): Bitmap {
        val bitmap = Bitmap.createBitmap(mWidth, mHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        if (shape == 0) {
            canvas.drawCircle(centerX, centerY, Math.min(mWidth, mHeight) / 2f, shapePaint)
        } else if (shape == 1) {
            canvas.drawRoundRect(shapeRectF, shapeRadius, shapeRadius, shapePaint)
        }
        return bitmap
    }

}