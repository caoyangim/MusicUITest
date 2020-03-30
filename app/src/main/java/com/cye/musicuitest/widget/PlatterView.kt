package com.cye.musicuitest.widget

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.animation.LinearInterpolator

class PlatterView : CircleImage {
    var isAnimation = false
    var duration:Long = 8000
    var time = 0L
    var objectAnimator = ObjectAnimator.ofFloat(this,"rotation",0f,360f)

    constructor(context:Context):super(context)
    constructor(context:Context,attrs: AttributeSet):this(context,attrs,0)
    constructor(context:Context, attrs: AttributeSet, defStyleAttr:Int):super(context,attrs,defStyleAttr){
        initAnim()
    }
    fun initAnim(){
        objectAnimator.duration = duration
        objectAnimator.interpolator = LinearInterpolator()
        objectAnimator.repeatCount = ValueAnimator.INFINITE
    }

    fun playAnimation(){
        if (isAnimation) return
        objectAnimator.start()
        objectAnimator.currentPlayTime = time
    }

    fun pauseAnimation(){
        objectAnimator.pause()
        time = objectAnimator.currentPlayTime
    }
}