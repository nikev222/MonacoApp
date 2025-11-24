package com.monaco.app.activities

import android.app.Activity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.monaco.app.R
import com.monaco.app.data.dao.UserDAO
import com.monaco.app.data.models.User

class EditUserActivity : AppCompatActivity() {

    private lateinit var etName: EditText
    private lateinit var etEmail: EditText
    private lateinit var etPhone: EditText
    private lateinit var btnUpdate: Button
    private lateinit var btnCancel: Button
    private lateinit var userDAO: UserDAO

    private var userId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_user)

        etName = findViewById(R.id.etName)
        etEmail = findViewById(R.id.etEmail)
        etPhone = findViewById(R.id.etPhone)
        btnUpdate = findViewById(R.id.btnUpdate)
        btnCancel = findViewById(R.id.btnCancel)

        userDAO = UserDAO(this)

        // Obtener datos enviados desde UserManagementActivity
        userId = intent.getIntExtra("USER_ID", -1)
        val userName = intent.getStringExtra("USER_NAME") ?: ""
        val userEmail = intent.getStringExtra("USER_EMAIL") ?: ""
        val userPhone = intent.getStringExtra("USER_PHONE") ?: ""

        // Cargar datos en los campos
        etName.setText(userName)
        etEmail.setText(userEmail)
        etPhone.setText(userPhone)

        // Guardar cambios
        btnUpdate.setOnClickListener {
            val newName = etName.text.toString().trim()
            val newEmail = etEmail.text.toString().trim()
            val newPhone = etPhone.text.toString().trim()

            if (newName.isEmpty() || newEmail.isEmpty()) {
                Toast.makeText(this, "Nombre y correo son obligatorios", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val updatedUser = User(
                id = userId,
                name = newName,
                email = newEmail,
                password = "" , // la contraseña no se edita
                phone = if (newPhone.isEmpty()) null else newPhone
            )

            val success = userDAO.updateUser(updatedUser)
            if (success) {
                Toast.makeText(this, "Usuario actualizado", Toast.LENGTH_SHORT).show()
                setResult(Activity.RESULT_OK) // devuelve OK para refrescar la lista
                finish()
            } else {
                Toast.makeText(this, "Error al actualizar usuario", Toast.LENGTH_SHORT).show()
            }
        }

        // Cancelar edición
        btnCancel.setOnClickListener {
            Toast.makeText(this, "Edición cancelada", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
}
