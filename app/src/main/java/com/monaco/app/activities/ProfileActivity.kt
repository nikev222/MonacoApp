package com.monaco.app.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.monaco.app.R
import com.monaco.app.data.SessionManager
import com.monaco.app.data.dao.UserDAO

class ProfileActivity : AppCompatActivity() {

    private lateinit var session: SessionManager
    private lateinit var userDAO: UserDAO

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        session = SessionManager(this)
        userDAO = UserDAO(this)

        val tvName = findViewById<TextView>(R.id.tvProfileName)
        val tvEmail = findViewById<TextView>(R.id.tvProfileEmail)
        val btnBack = findViewById<Button>(R.id.btnBackWelcome)
        val btnCerrarSesion = findViewById<Button>(R.id.btnCerrarSesionProfile)


        if (session.isLoggedIn()) {
            val userId = session.getUserId()
            val user = userDAO.getAllUsers().find { it.id == userId }

            if (user != null) {
                tvName.text = "Nombre: ${user.name}"
                tvEmail.text = "Email: ${user.email}"
            } else {
                tvName.text = "Usuario no encontrado"
                tvEmail.text = ""
            }
        } else {
            tvName.text = "No hay sesión iniciada"
            tvEmail.text = ""
        }

        btnBack.setOnClickListener {
            startActivity(Intent(this, WelcomeActivity::class.java))
            finish()
        }

        btnCerrarSesion.setOnClickListener {
            session.clearSession()
            startActivity(Intent(this, WelcomeActivity::class.java))
            finish()
        }
    }
}
