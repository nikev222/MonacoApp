package com.monaco.app.activities

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
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
    private lateinit var btnTakePhoto: Button
    private lateinit var imgPreview: ImageView
    private lateinit var btnCancel: Button
    private lateinit var productDAO: ProductDAO

    private var selectedImageUri: Uri? = null
    private var photoFile: File? = null
    private var currentImagePath: String? = null
    private var productId: Int = -1

    private val PICK_IMAGE_REQUEST = 100
    private val CAMERA_REQUEST = 101

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_product)

        // Inicializar vistas
        etName = findViewById(R.id.etName)
        etDescription = findViewById(R.id.etDescription)
        etPrice = findViewById(R.id.etPrice)
        btnUpdate = findViewById(R.id.btnUpdate)
        btnSelectImage = findViewById(R.id.btnSelectImage)
        btnTakePhoto = findViewById(R.id.btnTakePhoto)
        imgPreview = findViewById(R.id.imgPreview)
        btnCancel = findViewById(R.id.btnCancel)

        productDAO = ProductDAO(this)

        // Cargar datos del producto
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

        // Seleccionar imagen desde galería
        btnSelectImage.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, PICK_IMAGE_REQUEST)
        }

        // Tomar foto con cámara
        btnTakePhoto.setOnClickListener {
            val file = File(filesDir, "product_${System.currentTimeMillis()}.jpg")
            photoFile = file
            val uri = FileProvider.getUriForFile(
                this,
                "${packageName}.fileprovider",
                file
            )
            val intent = Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE)
            intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, uri)
            startActivityForResult(intent, CAMERA_REQUEST)
        }

        // Cancelar edición
        btnCancel.setOnClickListener {
            Toast.makeText(this, "Edición cancelada", Toast.LENGTH_SHORT).show()
            finish()
        }

        // Guardar cambios
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
                imagePath = selectedImageUri?.toString() ?: currentImagePath
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
        if (resultCode != Activity.RESULT_OK) return

        when(requestCode) {
            PICK_IMAGE_REQUEST -> {
                val uri = data?.data ?: return
                val path = copyUriToInternalFile(uri)
                selectedImageUri = Uri.fromFile(File(path))
                imgPreview.setImageURI(selectedImageUri)
            }

            CAMERA_REQUEST -> {
                photoFile?.let { file ->
                    val rotatedBitmap = fixImageOrientation(file)
                    FileOutputStream(file).use { out -> rotatedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out) }
                    selectedImageUri = Uri.fromFile(file)
                    imgPreview.setImageBitmap(rotatedBitmap)
                }
            }
        }
    }

    private fun copyUriToInternalFile(uri: Uri): String {
        val inputStream: InputStream? = contentResolver.openInputStream(uri)
        val file = File(filesDir, "product_${System.currentTimeMillis()}.jpg")
        FileOutputStream(file).use { out -> inputStream?.copyTo(out) }
        inputStream?.close()
        return file.absolutePath
    }

    private fun fixImageOrientation(file: File): Bitmap {
        val exif = ExifInterface(file.absolutePath)
        val rotation = when(exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)) {
            ExifInterface.ORIENTATION_ROTATE_90 -> 90f
            ExifInterface.ORIENTATION_ROTATE_180 -> 180f
            ExifInterface.ORIENTATION_ROTATE_270 -> 270f
            else -> 0f
        }

        val bitmap = android.graphics.BitmapFactory.decodeFile(file.absolutePath)
        if (rotation == 0f) return bitmap

        val matrix = Matrix()
        matrix.postRotate(rotation)
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }
}
