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
import com.monaco.app.data.models.User

class UserManagementActivity : AppCompatActivity() {

    private lateinit var recyclerUsers: RecyclerView
    private lateinit var adapter: UserAdapter
    private lateinit var userDAO: UserDAO
    private lateinit var btnGoWelcome: Button

    private val EDIT_USER_REQUEST = 200 // código de solicitud para edición

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_management)

        recyclerUsers = findViewById(R.id.recyclerUsers)
        recyclerUsers.layoutManager = LinearLayoutManager(this)

        userDAO = UserDAO(this)
        btnGoWelcome = findViewById(R.id.btnGoWelcome)

        btnGoWelcome.setOnClickListener {
            finish()
        }

        loadUsers()
    }

    // Función para cargar usuarios
    private fun loadUsers() {
        val users = userDAO.getAllUsers()
        adapter = UserAdapter(
            users,
            onDelete = { userId ->
                val success = userDAO.deleteUser(userId)
                if (success) {
                    Toast.makeText(this, "Usuario eliminado", Toast.LENGTH_SHORT).show()
                    loadUsers()
                } else {
                    Toast.makeText(this, "Error al eliminar usuario", Toast.LENGTH_SHORT).show()
                }
            },
            onEdit = { user ->
                val intent = Intent(this, EditUserActivity::class.java)
                intent.putExtra("USER_ID", user.id)
                intent.putExtra("USER_NAME", user.name)
                intent.putExtra("USER_EMAIL", user.email)
                intent.putExtra("USER_PHONE", user.phone)
                startActivityForResult(intent, EDIT_USER_REQUEST) // importante para refrescar
            }
        )
        recyclerUsers.adapter = adapter
    }

    // Refrescar lista al volver de la edición
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == EDIT_USER_REQUEST && resultCode == RESULT_OK) {
            loadUsers() // recarga la lista con los cambios
        }
    }
}
