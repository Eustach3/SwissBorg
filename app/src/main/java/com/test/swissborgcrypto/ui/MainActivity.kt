package com.test.swissborgcrypto.ui

import android.os.Bundle
import android.view.View
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.animation.Animation.AnimationListener
import android.view.animation.AnimationSet
import android.view.animation.TranslateAnimation
import androidx.appcompat.app.AppCompatActivity
import com.test.swissborgcrypto.R
import com.test.swissborgcrypto.R.layout
import com.test.swissborgcrypto.utils.cryptoDataFragment
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layout.activity_main)
        setTranslateToLeftAnim()
        setTranslateToRightAnim()
    }

    private fun setTranslateToLeftAnim() {
        val set = AnimationSet(true)
        val leftTranslateAnim: Animation = TranslateAnimation(300F, 0F, 0F, 0F)
        leftTranslateAnim.setDuration(3500)
        set.addAnimation(leftTranslateAnim)
        val alphaAnim: Animation = AlphaAnimation(0.0f, 1.0f)
        alphaAnim.duration = 4300
        set.addAnimation(alphaAnim)
        logo_borg.startAnimation(set)
    }
    private fun setTranslateToRightAnim() {
        val set = AnimationSet(true)
        val rightTranslateAnim: Animation = TranslateAnimation(-300F, 0F, 0F, 0F)
        rightTranslateAnim.setDuration(3500)
        set.addAnimation(rightTranslateAnim)
        val alphaAnim: Animation = AlphaAnimation(0.0f, 1.0f)
        alphaAnim.duration = 4300
        set.addAnimation(alphaAnim)
        logo_swiss.startAnimation(set)
        set.setAnimationListener(object : AnimationListener {
            override fun onAnimationStart(animation: Animation?) {
            }

            override fun onAnimationEnd(animation: Animation?) {
                logo_layout.visibility = View.GONE
                val beginTransaction = supportFragmentManager.beginTransaction()
                beginTransaction.add(R.id.root_layout, DataFragment.newInstance(), cryptoDataFragment).addToBackStack(null).commit()
            }

            override fun onAnimationRepeat(animation: Animation?) {
            }
        })
    }
}