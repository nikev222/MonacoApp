package com.monaco.app.activities

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.monaco.app.R
import com.monaco.app.data.dao.ProductDAO
import com.monaco.app.data.models.Product
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

class EditProductActivity : AppCompatActivity() {

    private lateinit var etName: EditText
    private lateinit var etDescription: EditText
    private lateinit var etPrice: EditText
    private lateinit var btnUpdate: Button
    private lateinit var btnSelectImage: Button
    private lateinit var imgPreview: ImageView
    private lateinit var productDAO: ProductDAO
    private lateinit var btnCancel: Button





    private var imageUri: Uri? = null
    private var currentImagePath: String? = null
    private var productId: Int = -1

    private val PICK_IMAGE_REQUEST = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_product)

        btnCancel = findViewById(R.id.btnCancel)

        etName = findViewById(R.id.etName)
        etDescription = findViewById(R.id.etDescription)
        etPrice = findViewById(R.id.etPrice)
        btnUpdate = findViewById(R.id.btnUpdate)
        btnSelectImage = findViewById(R.id.btnSelectImage)
        imgPreview = findViewById(R.id.imgPreview)


        productDAO = ProductDAO(this)


        productId = intent.getIntExtra("PRODUCT_ID", -1)
        val productName = intent.getStringExtra("PRODUCT_NAME") ?: ""
        val productDescription = intent.getStringExtra("PRODUCT_DESCRIPTION") ?: ""
        val productPrice = intent.getDoubleExtra("PRODUCT_PRICE", 0.0)
        currentImagePath = intent.getStringExtra("PRODUCT_IMAGE")



        etName.setText(productName)
        etDescription.setText(productDescription)
        etPrice.setText(productPrice.toString())


        if (!currentImagePath.isNullOrEmpty()) {
            try {
                val uri = Uri.parse(currentImagePath)
                imgPreview.setImageURI(uri)
            } catch (e: Exception) {
                imgPreview.setImageResource(R.drawable.logo_monaco)
            }
        }


        btnSelectImage.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, PICK_IMAGE_REQUEST)
        }

        btnCancel.setOnClickListener {
            Toast.makeText(this, "Edición cancelada", Toast.LENGTH_SHORT).show()
            finish()
        }



        btnUpdate.setOnClickListener {
            val newName = etName.text.toString().trim()
            val newDescription = etDescription.text.toString().trim()
            val newPrice = etPrice.text.toString().toDoubleOrNull() ?: 0.0

            if (newName.isEmpty() || newDescription.isEmpty() || newPrice <= 0) {
                Toast.makeText(this, "Completa todos los campos correctamente", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val updatedProduct = Product(
                id = productId,
                name = newName,
                description = newDescription,
                price = newPrice,
                imagePath = imageUri?.toString() ?: currentImagePath
            )

            val updated = productDAO.updateProduct(updatedProduct)

            if (updated) {
                Toast.makeText(this, "Producto actualizado correctamente", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                Toast.makeText(this, "Error al actualizar producto", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            val selectedUri = data.data
            if (selectedUri != null) {
                val localPath = copyUriToLocalFile(selectedUri)
                if (localPath != null) {
                    imageUri = Uri.fromFile(File(localPath))
                    imgPreview.setImageURI(imageUri)
                } else {
                    Toast.makeText(this, "Error al procesar imagen", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    // Copia segura del contenido del URI
    private fun copyUriToLocalFile(uri: Uri): String? {
        return try {
            val inputStream: InputStream? = contentResolver.openInputStream(uri)
            val file = File(cacheDir, "image_${System.currentTimeMillis()}.jpg")
            val outputStream = FileOutputStream(file)
            inputStream?.copyTo(outputStream)
            inputStream?.close()
            outputStream.close()
            file.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
