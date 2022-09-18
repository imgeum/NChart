package com.neoguri.nchart.chart

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewTreeObserver
import android.widget.ImageView
import android.widget.RelativeLayout
import androidx.annotation.AttrRes
import androidx.core.content.ContextCompat
import com.neoguri.nchart.R
import java.util.*


class NChart @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    @AttrRes defStyleAttr: Int = 0
) : RelativeLayout (context, attrs, defStyleAttr), View.OnTouchListener {

    enum class CUTMODE {
        NONE,
        ALL,
        TOP,
        BOTTOM
    }

    private var mCutMode: CUTMODE = CUTMODE.NONE

    private lateinit var mChartLayout: RelativeLayout

    private var mTopBackgroundColor = ContextCompat.getColor(context, R.color.transparency)

    private var mFloatArray =
        ArrayList<ArrayList<Float>>() // 0은 맨처음 임시 여유분, 1~24까지 진짜, 25는 맨 마지막 임시 여유분
    private val mListData = ArrayList<Int>()

    private val paint: Paint = Paint()
    private val path: Path = Path() //터치된 좌표를 저장하기 위한 Path 객체 생성

    private lateinit var mCircleImage: ImageView

    private var mChartLineStrokeWidth = 10
    private var mChartCircleWidth = 15

    private var mWidth = 0f

    private var mValue = ""
    private var mLineColor = 0
    private var mCutColor = 0
    private var mPosition = 0

    private var mMovePosision = 0

    private var mScrollX = 0

    private var isCircle = false
    private var isMove = false

    private var mBottomBackground: Drawable? = null
    private var mCircleBackground: Drawable? = null

    init {

        if (attrs != null) {
            val typedArr = context.obtainStyledAttributes(attrs, R.styleable.NChart)

            mBottomBackground = typedArr.getDrawable(R.styleable.NChart_chartBottomBackgroundDrawable)

            mTopBackgroundColor = typedArr.getColor(
                R.styleable.NChart_chartTopBackgroundColor,
                ContextCompat.getColor(context, R.color.transparency)
            )
            mLineColor = typedArr.getColor(
                R.styleable.NChart_chartLineColor, ContextCompat.getColor(context, R.color.white)
            )
            mCutColor = typedArr.getColor(
                R.styleable.NChart_chartCutColor, ContextCompat.getColor(context, R.color.purple_200)
            )
            mCircleBackground = typedArr.getDrawable(R.styleable.NChart_chartCircleDrawable)

            mChartLineStrokeWidth = typedArr.getDimensionPixelSize(
                R.styleable.NChart_chartLineStrokeWidth,
                context.resources.getDimensionPixelSize(R.dimen.chart_line_stroke_width)
            )
            mChartCircleWidth = typedArr.getDimensionPixelSize(
                R.styleable.NChart_chartCircleWidth,
                context.resources.getDimensionPixelSize(R.dimen.chart_circle_width)
            )
            mCutMode = CUTMODE.values()[typedArr.getInt(R.styleable.NChart_cutMode, 0)]

            val infService = Context.LAYOUT_INFLATER_SERVICE
            val li = getContext().getSystemService(infService) as LayoutInflater
            val v: View = li.inflate(R.layout.chart_layout, this, false)
            mCircleImage = v.findViewById(R.id.circle_image)

            mChartLayout = v.findViewById(R.id.chart_layout)

            mCircleImage.setOnTouchListener(this)

            paint.isAntiAlias = true
            paint.style = Paint.Style.STROKE
            paint.strokeCap = Paint.Cap.ROUND
            paint.strokeJoin = Paint.Join.ROUND

            addView(v)
            setWillNotDraw(false)
        }

    }

    override fun onDraw(canvas: Canvas) {
        canvas.drawPath(path, paint)
        super.onDraw(canvas)
    }

    fun drawableBackgroundSet(bottomBackground: Drawable?) {
        mBottomBackground = bottomBackground
    }

    fun setCircleEnabled(boolean: Boolean) {
        isCircle = boolean
    }

    fun setCircleMoveEnabled(boolean: Boolean) {
        isMove = boolean
    }

    fun chartSet(
        value: String
    ) {
        mValue = value
        chartSet()
    }

    fun chartSet(
        value: String,
        cut: CUTMODE
    ) {
        mTopBackgroundColor = ContextCompat.getColor(context, R.color.transparency)
        mLineColor = ContextCompat.getColor(context, R.color.white)
        mCutColor = ContextCompat.getColor(context, R.color.purple_200)
        mCircleBackground = ContextCompat.getDrawable(context, R.drawable.round_circle)
        mChartLineStrokeWidth = resources.getDimensionPixelSize(R.dimen.chart_line_stroke_width)
        mChartCircleWidth = resources.getDimensionPixelSize(R.dimen.chart_circle_width)
        mValue = value
        mPosition = 0
        mCutMode = cut
        chartSet()
    }

    fun chartSet(
        value: String,
        position: Int,
        cut: CUTMODE
    ) {
        mTopBackgroundColor = ContextCompat.getColor(context, R.color.transparency)
        mLineColor = ContextCompat.getColor(context, R.color.white)
        mCutColor = ContextCompat.getColor(context, R.color.purple_200)
        mCircleBackground = ContextCompat.getDrawable(context, R.drawable.round_circle)
        mChartLineStrokeWidth = resources.getDimensionPixelSize(R.dimen.chart_line_stroke_width)
        mChartCircleWidth = resources.getDimensionPixelSize(R.dimen.chart_circle_width)
        mValue = value
        mPosition = position
        mCutMode = cut
        chartSet()
    }

    fun chartSet(
        topBackgroundColor: Int,
        lineColor: Int,
        cutColor: Int,
        circleBackground: Drawable?,
        chartLineStrokeWidth: Int,
        chartCircleWidth: Int,
        value: String,
        position: Int,
        cut: CUTMODE,
        bottomBackground: Drawable?
    ) {
        mTopBackgroundColor = topBackgroundColor
        mLineColor = lineColor
        mCutColor = cutColor
        mCircleBackground = circleBackground
        mChartLineStrokeWidth = chartLineStrokeWidth
        mChartCircleWidth = chartCircleWidth
        mValue = value
        mPosition = position
        mCutMode = cut
        mBottomBackground = bottomBackground
        chartSet()
    }

    private fun chartSet() {

        mFloatArray.clear()
        mListData.clear()
        background = null

        paint.color = mCutColor

        if (null != mCircleBackground) {
            mCircleImage.background = mCircleBackground
        }
        paint.strokeWidth = mChartLineStrokeWidth.toFloat()
        mCircleImage.layoutParams.width = mChartCircleWidth
        mCircleImage.layoutParams.height = mChartCircleWidth
        mCircleImage.requestLayout()

        if (null == mBottomBackground) {
            setBackgroundColor(ContextCompat.getColor(context, R.color.transparency))
        } else {
            background = mBottomBackground
        }

        viewTreeObserver.addOnGlobalLayoutListener(object :
            ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                when (visibility) {
                    VISIBLE -> {
                        viewSet()
                        viewTreeObserver.removeOnGlobalLayoutListener(this)
                    }
                    INVISIBLE -> {
                        viewSet()
                        viewTreeObserver.removeOnGlobalLayoutListener(this)
                    }
                    GONE -> {

                    }
                }
            }
        })

    }

    fun viewSet() {
        val split = mValue.split(",")

        splitSet(split)

        if(mCutMode == CUTMODE.NONE){
            background = BitmapDrawable(
                resources,
                getBitmapFromView(this@NChart)
            )
        } else if(mCutMode == CUTMODE.TOP){
            background = BitmapDrawable(
                resources,
                makeTransparentTop(mCutColor, getBitmapFromView(this@NChart))
            )
        } else if(mCutMode == CUTMODE.BOTTOM){
            background = BitmapDrawable(
                resources,
                makeTransparentBottom(mCutColor, getBitmapFromView(this@NChart))
            )
        }

        paint.color = mLineColor
        mChartLayout.visibility = View.VISIBLE
        posiotionSet()
    }

    private fun splitSet(split: List<String>) {

        for (value in split) {
            mListData.add(value.toInt())
        }

        val max = Collections.max(mListData)
        val min = Collections.min(mListData)

        var height: Float = ((height / 10) * 7).toFloat() // Y축 하단
        val width: Float = (width / split.count()).toFloat()

        mWidth = width / 2

        var xx = width / 2
        val yy: Float = ((height / 10) * 6) / max // Y축 상단

        val chartArray = ArrayList<Float>()

        for (i in 0 until max) {
            chartArray.add(height)
            height -= yy
        }

        val floatArray1 = ArrayList<Float>()
        floatArray1.add(0f)
        floatArray1.add(chartArray[split[0].toInt() - min])
        mFloatArray.add(floatArray1)

        for (i in split.indices) {
            val floatArray = ArrayList<Float>()
            floatArray.add(xx)
            floatArray.add(chartArray[split[i].toInt() - min])
            mFloatArray.add(floatArray)
            xx += width
        }

        val floatArray2 = ArrayList<Float>()
        floatArray2.add(xx + (width / 2))
        floatArray2.add(chartArray[split[split.size - 1].toInt() - min])
        mFloatArray.add(floatArray2)

        lineSet(mFloatArray)

    }

    private fun posiotionSet() {
        mMovePosision = mPosition
        imageSet(mFloatArray[mPosition + 1])
        mChartEvent?.onScrollSetEvent(this, mFloatArray[mPosition + 1], mWidth)
        mChartEvent?.onChartValueEvent(this, mListData[mPosition])
    }

    private fun lineSet(floatArray: ArrayList<ArrayList<Float>>) {
        mFloatArray = floatArray
        path.reset()
        for (i in 0 until floatArray.size) {
            if (i < floatArray.size - 1) {
                path.moveTo(floatArray[i][0], floatArray[i][1])
                path.lineTo(floatArray[i + 1][0], floatArray[i + 1][1])
            }
        }
        invalidate() //다시 onDraw() 시켜주는 부분
    }

    fun horizontalXScrollSet(scrollX: Int) {
        mScrollX = if (scrollX <= 0) {
            0
        } else {
            scrollX
        }
    }

    private fun imageSet(arrayList: ArrayList<Float>) {
        mCircleImage.viewTreeObserver.addOnGlobalLayoutListener(object :
            ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                mCircleImage.x = arrayList[0] - (mCircleImage.width / 2)
                mCircleImage.y = arrayList[1] - (mCircleImage.height / 2)
                if(isCircle){
                    mCircleImage.visibility = View.VISIBLE
                } else {
                    mCircleImage.visibility = View.GONE
                }
                mCircleImage.viewTreeObserver.removeOnGlobalLayoutListener(this)
            }
        })
    }

    private fun imageMoveSet(arrayList: ArrayList<Float>) {
        mCircleImage.x = arrayList[0] - (mCircleImage.width / 2)
        mCircleImage.y = arrayList[1] - (mCircleImage.height / 2)
    }

    override fun onTouch(v: View, event: MotionEvent): Boolean {

        if (v.id == mCircleImage.id) {
            val action = event.action
            when (action and MotionEvent.ACTION_MASK) {
                MotionEvent.ACTION_DOWN -> {
                    mChartEvent?.onChartDownUpEvent(this, false)
                }
                MotionEvent.ACTION_MOVE -> {
                    if(!isMove){
                        return true
                    }
                    for (i in 1 until mFloatArray.size - 1) {
                        if (i != mMovePosision) {
                            if (mFloatArray[i][0].toInt() - mWidth < event.rawX.toInt() + mScrollX && event.rawX.toInt() + mScrollX < mFloatArray[i][0].toInt() + mWidth) {
                                imageMoveSet(mFloatArray[i])
                                mChartEvent?.onChartValueEvent(this, mListData[i - 1])
                                mMovePosision = i
                            }
                        }
                    }
                }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_POINTER_UP -> {
                    mChartEvent?.onChartDownUpEvent(this, true)
                    v.performClick()
                }
            }
        }

        return true

    }

    override fun performClick(): Boolean {
        super.performClick()
        doSomething()
        return true
    }

    private fun doSomething() {
        //Toast.makeText(context, "did something", Toast.LENGTH_SHORT).show()
    }

    private fun getBitmapFromView(v: View): Bitmap {
        val b = Bitmap.createBitmap(
            v.width, v.height,
            Bitmap.Config.ARGB_8888
        )
        val c = Canvas(b)
        v.draw(c)
        return b
    }

    private fun makeTransparentTop(lineColor: Int, bit: Bitmap): Bitmap {
        val width = bit.width
        val height = bit.height
        val myBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val allpixels = IntArray(myBitmap.height * myBitmap.width)
        bit.getPixels(allpixels, 0, myBitmap.width, 0, 0, myBitmap.width, myBitmap.height)
        myBitmap.setPixels(allpixels, 0, width, 0, 0, width, height)
        for (i in myBitmap.width downTo 0) {
            allpixels[i] = mTopBackgroundColor
            for (j in (myBitmap.height * myBitmap.width) -i - 1 downTo 0 step myBitmap.width) {
                if (allpixels[j] == lineColor) {
                    break
                }
                allpixels[j] = mTopBackgroundColor
            }
        }

        myBitmap.setPixels(allpixels, 0, myBitmap.width, 0, 0, myBitmap.width, myBitmap.height)
        return myBitmap
    }

    private fun makeTransparentBottom(lineColor: Int, bit: Bitmap): Bitmap {
        val width = bit.width
        val height = bit.height
        val myBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val allpixels = IntArray(myBitmap.height * myBitmap.width)
        bit.getPixels(allpixels, 0, myBitmap.width, 0, 0, myBitmap.width, myBitmap.height)
        myBitmap.setPixels(allpixels, 0, width, 0, 0, width, height)
        for (i in 0 until myBitmap.width) {
            allpixels[i] = mTopBackgroundColor
            for (j in i + myBitmap.width + 1 until myBitmap.height * myBitmap.width step myBitmap.width) {
                if (allpixels[j] == lineColor) {
                    break
                }
                allpixels[j] = mTopBackgroundColor
            }
        }

        myBitmap.setPixels(allpixels, 0, myBitmap.width, 0, 0, myBitmap.width, myBitmap.height)
        return myBitmap
    }

    private fun sharpen(lineColor: Int, src: Bitmap): Bitmap {
        val width = src.width
        val height = src.height
        val color = mTopBackgroundColor

        val bmOut = Bitmap.createBitmap(width, height, src.config)
        bmOut.setHasAlpha(true)

        for (x in 0 until width) {
            for (y in 0 until height) {
                bmOut.setPixel(x, y, color)
                if (src.getPixel(x, y) == lineColor) {
                    break
                }
            }
        }

        return bmOut
    }

    private var mChartEvent: ChartEventListener? = null

    fun setOnChartEvent(listener: ChartEventListener) {
        mChartEvent = listener
    }

    interface ChartEventListener {
        fun onChartDownUpEvent(view: View, boolean: Boolean)
        fun onScrollSetEvent(view: View, arrayList: ArrayList<Float>, width: Float)
        fun onChartValueEvent(view: View, value: Int)
    }

}