package com.example.demo

import android.content.Context
import android.graphics.Color
import android.os.Handler
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.Scroller
import android.widget.TextView
import androidx.core.view.get
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.ScreenUtils

/**
 * Levine
 * 2020-05-27 10:55
 * 1483232332
 */
class TextScroll : FrameLayout {


    private val texts: ArrayList<String> = arrayListOf()

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, a: AttributeSet) : super(context, a) {
        init()
    }

    constructor(context: Context, a: AttributeSet, i: Int) : super(context, a, i) {
        init()
    }

    var scroller: Scroller? = null


    private fun init() {
        scroller = Scroller(context)
        texts.add("1111111111111111")
        texts.add("22222222222222222")
        texts.add("3333333333333333")
        texts.add("444444444444444")
        texts.add("5555555555555555")
        texts.add("66666666666666")
        texts.add("77777777777777")
        texts.add("88888888888888")
        texts.add("99999999999999")
        texts.add("100000000000000")
        texts.add("110000000000000")
        texts.add("120000000000000")
        texts.add("130000000000000")
        texts.add("140000000000000")
        texts.add("150000000000000")
        for (text in texts) {
            val textView = TextView(context)
            textView.text = text
            textView.setBackgroundColor(Color.RED)
            textView.layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            addView(textView)
        }
        setBackgroundColor(Color.BLUE)

    }

    fun setTexts(texts: ArrayList<String>) {
        removeAllViews()
        for (text in texts) {
            val textView = TextView(context)
            textView.text = text
            textView.setBackgroundColor(Color.RED)
            addView(textView)
        }
        mTop = 0
        isStartScroll = false
        mScrollPosition = 0
        mScrollY = 0
        mOffset = 0
        mFixOffset = 0
        scroller?.abortAnimation()
        requestLayout()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
//        for (i in 0 until childCount) {
//            val view = getChildAt(i)
//            measureChild(view, widthMeasureSpec, heightMeasureSpec)
//        }
    }

    var mTop = 0
    var isStartScroll = false
    var mScrollPosition = 0
    var mScrollY = 0
    var mOffset = 0
    var mFixOffset = 0
    var isScrollHeader = false

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        for (i in 0 until childCount) {
            if (mScrollPosition == 0) {
                val view = getChildAt(i)
                view.layout(0, mTop, r, mTop + view.measuredHeight)
                mTop += view.measuredHeight
                if (mTop > b && mFixOffset == 0) {
                    mFixOffset = mTop - b
                    mOffset = mFixOffset
                }
            } else {
                if (i + mScrollPosition >= childCount) {
                    val view = getChildAt(-(childCount - (i + mScrollPosition)))
                    view.layout(0, mTop, r, mTop + view.measuredHeight)
                    mTop += view.measuredHeight
                } else {
                    val view = getChildAt(i + mScrollPosition)
                    view.layout(0, mTop, r, mTop + view.measuredHeight)
                    mTop += view.measuredHeight
                }
            }
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        postDelayed({ startScroll() }, 1000)
    }

    private fun startScroll() {
        if (isStartScroll) return
        isStartScroll = true
        val scrollY =
            if (mFixOffset > 0) -mFixOffset else -getChildAt(
                mScrollPosition
            ).measuredHeight
        val startY = if (mFixOffset > 0) 0 else -mOffset
        scroller?.startScroll(0, startY, 0, scrollY, 1000)
        if (mFixOffset > 0) mFixOffset = -1
        post(scrollRun)
    }

    private val scrollRun = object : Runnable {
        override fun run() {
            if (scroller?.computeScrollOffset()!!) {
                mTop = scroller?.currY!!
                mScrollY = mTop
                post { requestLayout() }
                postDelayed(this, 10)
            } else {
                postDelayed({
                    isStartScroll = false
                    mScrollPosition += 1
                    if (mScrollPosition >= childCount) {
                        mScrollPosition = 0
                    }
                    startScroll()
                }, 1000)
            }
        }
    }
}