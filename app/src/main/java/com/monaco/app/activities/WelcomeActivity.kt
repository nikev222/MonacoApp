package com.monaco.app.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.monaco.app.R
import com.monaco.app.data.SessionManager

class WelcomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)

        val session = SessionManager(this)
        val tvWelcomeUser = findViewById<TextView>(R.id.tvWelcomeUser)
        val layoutNoSession = findViewById<LinearLayout>(R.id.layoutNoSession)
        val layoutSessionActive = findViewById<LinearLayout>(R.id.layoutSessionActive)

        val btnLogin = findViewById<Button>(R.id.btnLogin)
        val btnRegister = findViewById<Button>(R.id.btnRegister)
        val btnVerPerfil = findViewById<Button>(R.id.btnVerPerfil)
        val btnCerrarSesion = findViewById<Button>(R.id.btnCerrarSesion)
        val btnIngresar = findViewById<Button>(R.id.btnIngresar)
        val btnAdminPanel = findViewById<Button>(R.id.btnAdminPanel)

        if (session.isLoggedIn()) {
            layoutNoSession.visibility = View.GONE
            layoutSessionActive.visibility = View.VISIBLE
            tvWelcomeUser.text = "¡Hola, ${session.getUserName()}!"

            // Botón Ingresar al menú
            btnIngresar.setOnClickListener {
                startActivity(Intent(this, MenuActivity::class.java))
                finish()
            }

            // Botón panel de administración
            btnAdminPanel.setOnClickListener {
                startActivity(Intent(this, AdminPanelActivity::class.java))
            }



            // Botón Ver perfil
            btnVerPerfil.setOnClickListener {
                val intent = Intent(this, ProfileActivity::class.java)
                intent.putExtra("USER_ID", session.getUserId())
                startActivity(intent)
            }

            // Botón Cerrar sesión
            btnCerrarSesion.setOnClickListener {
                session.clearSession()
                recreate() // recarga la pantalla
            }





        } else {
            layoutNoSession.visibility = View.VISIBLE
            layoutSessionActive.visibility = View.GONE
            tvWelcomeUser.text = getString(R.string.welcome_message)


            btnLogin.setOnClickListener {
                startActivity(Intent(this, LoginActivity::class.java))
            }

            btnRegister.setOnClickListener {
                startActivity(Intent(this, RegisterActivity::class.java))
            }
        }
    }
}

