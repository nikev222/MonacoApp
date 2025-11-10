package com.monaco.app.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.monaco.app.R
import com.monaco.app.adapters.UserAdapter
import com.monaco.app.data.dao.UserDAO

class UserManagementActivity : AppCompatActivity() {

    private lateinit var recyclerUsers: RecyclerView
    private lateinit var adapter: UserAdapter
    private lateinit var userDAO: UserDAO

    private lateinit var btnGoWelcome: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_management)

        recyclerUsers = findViewById(R.id.recyclerUsers)
        recyclerUsers.layoutManager = LinearLayoutManager(this)

        userDAO = UserDAO(this)
        btnGoWelcome = findViewById(R.id.btnGoWelcome)


        btnGoWelcome.setOnClickListener {
            val intent = Intent(this, WelcomeActivity::class.java)
            startActivity(intent)
        }


        loadUsers()
    }



    private fun loadUsers() {
        val users = userDAO.getAllUsers()
        adapter = UserAdapter(users) { userId ->
            // Eliminar usuario
            val success = userDAO.deleteUser(userId)
            if (success) {
                Toast.makeText(this, "Usuario eliminado", Toast.LENGTH_SHORT).show()
                loadUsers() // refresca la lista
            } else {
                Toast.makeText(this, "Error al eliminar", Toast.LENGTH_SHORT).show()
            }
        }
        recyclerUsers.adapter = adapter
    }
}
