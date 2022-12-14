package com.example.book

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        val hehamTvSplash = findViewById<ImageView>(R.id.heham_tv_splash)
        val scaleDownAnim = AnimationUtils.loadAnimation(this, R.anim.scale_down)

        Handler().postDelayed({
            hehamTvSplash.startAnimation(scaleDownAnim)
            Handler().postDelayed({
                startActivity(Intent(this, MainScreenActivity::class.java))
                finish()
            }, 510)
        }, 1500)
    }
}