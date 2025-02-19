package com.example.rentago

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.rentago.Response.ItemResponse
import com.example.rentago.Response.RegistrationResponse
import com.example.rentago.Retrofit.Registration.Item.RetrofitItemClient
import com.example.rentago.databinding.ActivityMainBinding
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.ByteArrayOutputStream
import java.io.File


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var selectedImageUri: Uri? = null
    private val PICK_IMAGE_REQUEST = 1
    private val CAMERA_REQUEST = 2
    private val CAMERA_PERMISSION_REQUEST = 100
    private val currentOwner = "CurrentLoggedInUser" // Replace with actual user info

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.fab.setOnClickListener {
            showImagePickerDialog()
        }
    }

    private fun showImagePickerDialog() {
        val options = arrayOf("Choose from Gallery", "Take a Photo")
        AlertDialog.Builder(this)
            .setTitle("Select Image")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> openGallery()
                    1 -> checkCameraPermission() // Ask for permission first
                }
            }
            .show()
    }

    private fun checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.CAMERA),
                CAMERA_PERMISSION_REQUEST
            )
        } else {
            openCamera()
        }
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    private fun openCamera() {
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(cameraIntent, CAMERA_REQUEST)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == CAMERA_PERMISSION_REQUEST) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCamera()
            } else {
                Toast.makeText(this, "Camera permission denied!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                PICK_IMAGE_REQUEST -> {
                    selectedImageUri = data?.data
                    showItemInputDialog()
                }
                CAMERA_REQUEST -> {
                    val bitmap = data?.extras?.get("data") as Bitmap
                    selectedImageUri = getImageUri(bitmap)
                    showItemInputDialog()
                }
            }
        }
    }

    private fun getImageUri(bitmap: Bitmap): Uri {
        val bytes = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path = MediaStore.Images.Media.insertImage(contentResolver, bitmap, "CapturedImage", null)
        return Uri.parse(path)
    }

    private fun showItemInputDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_add_item, null)
        val nameInput = dialogView.findViewById<EditText>(R.id.itemName)
        val priceInput = dialogView.findViewById<EditText>(R.id.itemPrice)
        val descriptionInput = dialogView.findViewById<EditText>(R.id.itemDescription)
        val itemImage = dialogView.findViewById<ImageView>(R.id.itemImage)

        selectedImageUri?.let { uri ->
            itemImage.setImageURI(uri)
        }

        AlertDialog.Builder(this)
            .setTitle("Add Item")
            .setView(dialogView)
            .setPositiveButton("Save") { _, _ ->
                val name = nameInput.text.toString().trim()
                val price = priceInput.text.toString().trim()
                val description = descriptionInput.text.toString().trim()

                if (name.isNotEmpty() && price.isNotEmpty() && description.isNotEmpty() && selectedImageUri != null) {
                    saveItem(name, price, description, currentOwner, selectedImageUri!!)
                } else {
                    Toast.makeText(this, "Fill all fields and select an image!", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun saveItem(name: String, price: String, description: String, owner: String, images: Uri) {
        Toast.makeText(this, "Item Saved:\n$name\n$price\n$description\nOwner: $owner\nImage: $images", Toast.LENGTH_LONG).show()
        val filePath = getRealPathFromURI(images)
        if (filePath == null) {
            Toast.makeText(this, "Error selecting image", Toast.LENGTH_SHORT).show()
            return
        }

        val file = File(filePath)
        val requestFile = RequestBody.create(MediaType.parse("image/*"), file)
        val imagePart = MultipartBody.Part.createFormData("images", file.name, requestFile)

        // Convert text inputs to request bodies
        val itemName = RequestBody.create(MediaType.parse("text/plain"), name)
        val itemDescription = RequestBody.create(MediaType.parse("text/plain"), description)
        val itemPrice = RequestBody.create(MediaType.parse("text/plain"), price)

        val sharedPreferences = getSharedPreferences("auth_prefs", MODE_PRIVATE)
        val token = sharedPreferences.getString("accessToken", null)
        Log.e("TOKEN", "Token retrieved: $token")
        if (token != null) {
            Log.d("TOKEN_CHECK", "Token retrieved: $token")
        } else {
            Log.e("TOKEN_CHECK", "No token found, user needs to log in")
        }
        RetrofitItemClient.instance.uploadItem("Bearer $token"
            ,imagePart, itemName, itemDescription, itemPrice)
            .enqueue(object : Callback<ItemResponse> {
                override fun onResponse(call: Call<ItemResponse>, response: Response<ItemResponse>) {
                    if (response.isSuccessful) {
                        Log.e("UPLOAD", "Item uploaded successfully")
                    } else {
                        Log.e("UPLOAD", "Upload failed: ${response.errorBody()?.string()}")
                    }
                }
                override fun onFailure(call: Call<ItemResponse>, t: Throwable) {
                    Log.e("UPLOAD", "Error: ${t.message}")
                }
            })
    }

    private fun getRealPathFromURI(uri: Uri): String? {
        val cursor = contentResolver.query(uri, null, null, null, null)
        return cursor?.use {
            val idx = it.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
            if (idx != -1) {
                it.moveToFirst()
                it.getString(idx)
            } else {
                null
            }
        }
    }
}
