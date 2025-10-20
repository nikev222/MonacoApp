package com.monaco.app

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class MenuActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)

        findViewById<Button>(R.id.btnRegistrar).setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        findViewById<Button>(R.id.btnLogin).setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }

        findViewById<Button>(R.id.btnPerfil).setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }
    }
}
