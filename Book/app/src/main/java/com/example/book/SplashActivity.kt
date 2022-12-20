package com.example.book

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.animation.Animation
import android.view.animation.Animation.AnimationListener
import android.view.animation.AnimationUtils
import android.widget.ImageView

@Suppress("DEPRECATION")
class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        Handler().postDelayed({
            val hehamTvSplash = findViewById<ImageView>(R.id.heham_tv_splash)
            val scaleDownAnim = AnimationUtils.loadAnimation(this, R.anim.scale_down_to_center)

            Handler().postDelayed({
                startActivity(Intent(this@SplashActivity, MainScreenActivity::class.java))
            }, 400)

            hehamTvSplash.startAnimation(scaleDownAnim)
            scaleDownAnim.setAnimationListener(object: AnimationListener {
                override fun onAnimationStart(p0: Animation?) {
                }

                override fun onAnimationEnd(p0: Animation?) {
                    hehamTvSplash.visibility = View.GONE
                    finish()
                }

                override fun onAnimationRepeat(p0: Animation?) {
                }
            })
        }, 2000)
    }
}