package com.monaco.app.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.monaco.app.R

class AdminPanelActivity : AppCompatActivity() {



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_panel)

        val btnManageUsers= findViewById<Button>(R.id.btnManageUsers)
        val btnManageStores = findViewById<Button>(R.id.btnManageStores)
        val btnAdminPanel = findViewById<Button>(R.id.btnAdminPanel)
        val btnBackMenu = findViewById<Button>(R.id.btnBackMenu)

        btnManageUsers.setOnClickListener {
            val intent = Intent(this, UserManagementActivity::class.java)
            startActivity(intent)
        }

        btnManageStores.setOnClickListener {

            val intent = Intent(this, AdminStoresActivity::class.java)
            startActivity(intent)
        }

        btnAdminPanel.setOnClickListener {
            startActivity(Intent(this, AdminproductosActivity::class.java))
        }

        btnBackMenu.setOnClickListener {
            finish() // Volver al menú anterior
        }
    }
}
