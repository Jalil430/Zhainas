package com.example.book

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.book.databinding.ConnectionLostBinding

class ConnectionLost : AppCompatActivity() {

    private var connectionLiveData: ConnectionLiveData? = null
    private var binding: ConnectionLostBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.connection_lost)
        binding = ConnectionLostBinding.bind(findViewById(R.id.rootConnectionLost))

        connectionLiveData = ConnectionLiveData(application)

        binding?.apply {
            connectionLiveData?.observe(this@ConnectionLost) { isConnected ->
                if (isConnected) {
                    layoutConnected.visibility = View.VISIBLE
                    layoutDisconnected.visibility = View.GONE
                    Handler().postDelayed({
                        startActivity(Intent(
                            this@ConnectionLost,
                            MainScreenActivity::class.java)
                        )
                        finish()
                    }, 700)
                }
            }
        }

        val isConnected = intent.getBooleanExtra("isConnected", false)

        binding?.apply {
            if (!isConnected) {
                layoutDisconnected.visibility = View.VISIBLE
                layoutConnected.visibility = View.GONE
            }
        }
    }
}