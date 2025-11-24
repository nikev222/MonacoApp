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

class AddProductActivity : AppCompatActivity() {

    private lateinit var productDAO: ProductDAO
    private var selectedImageUri: Uri? = null
    private var photoFile: File? = null
    private val PICK_IMAGE_REQUEST = 100
    private val CAMERA_REQUEST = 101

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_product)

        productDAO = ProductDAO(this)

        val etName = findViewById<EditText>(R.id.etName)
        val etDescription = findViewById<EditText>(R.id.etDescription)
        val etPrice = findViewById<EditText>(R.id.etPrice)
        val imageProduct = findViewById<ImageView>(R.id.imageProduct)
        val btnSave = findViewById<Button>(R.id.btnSave)
        val btnCancel = findViewById<Button>(R.id.btnCancel)
        val btnTakePhoto = findViewById<Button>(R.id.btnTakePhoto)

        // Galería
        imageProduct.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, PICK_IMAGE_REQUEST)
        }

        // Cámara
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

        btnSave.setOnClickListener {
            val name = etName.text.toString().trim()
            val description = etDescription.text.toString().trim()
            val priceText = etPrice.text.toString().trim()

            if (name.isEmpty() || description.isEmpty() || priceText.isEmpty() || selectedImageUri == null) {
                Toast.makeText(this, "Completa todos los campos y selecciona una imagen", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val price = priceText.toDoubleOrNull()
            if (price == null) {
                Toast.makeText(this, "Precio inválido", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val product = Product(
                name = name,
                description = description,
                price = price,
                imagePath = selectedImageUri.toString()
            )

            val success = productDAO.insertProduct(product)
            if (success) {
                Toast.makeText(this, "Producto agregado", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                Toast.makeText(this, "Error al agregar el producto", Toast.LENGTH_SHORT).show()
            }
        }

        btnCancel.setOnClickListener { finish() }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode != Activity.RESULT_OK) return

        when(requestCode) {
            PICK_IMAGE_REQUEST -> {
                val uri = data?.data ?: return
                val path = copyUriToInternalFile(uri)
                selectedImageUri = Uri.fromFile(File(path))
                findViewById<ImageView>(R.id.imageProduct).setImageURI(selectedImageUri)
            }

            CAMERA_REQUEST -> {
                photoFile?.let { file ->
                    val rotatedBitmap = fixImageOrientation(file)
                    FileOutputStream(file).use { out -> rotatedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out) }
                    selectedImageUri = Uri.fromFile(file)
                    findViewById<ImageView>(R.id.imageProduct).setImageBitmap(rotatedBitmap)
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
