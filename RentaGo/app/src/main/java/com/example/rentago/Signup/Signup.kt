package com.example.rentago.Signup

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import com.example.rentago.R
import com.example.rentago.Response.RegistrationResponse
import com.example.rentago.Retrofit.Registration.Registration.RetrofitRegistrationClient
import com.example.rentago.databinding.ActivitySignupBinding
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class Signup : AppCompatActivity() {
    private lateinit var binding: ActivitySignupBinding
    private var imageUri: Uri? = null
    private val PICK_IMAGE_REQUEST = 1
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Open gallery when clicking the avatar
        binding.avatarImage.setOnClickListener {
            pickImageFromGallery()
        }

        // Handle signup button click
        binding.signupbutton.setOnClickListener {
            if (imageUri != null) {
                registerUser()
            } else {
                Toast.makeText(this, "Please select an image", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Function to open gallery
    private fun pickImageFromGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    // Handle image selection result
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK) {
            data?.data?.let {
                imageUri = it
                binding.avatarImage.setImageURI(it)
            }
        }
    }

    // Function to register user with image
    private fun registerUser() {
        val filePath = getRealPathFromURI(imageUri!!)
        if (filePath == null) {
            Toast.makeText(this, "Error selecting image", Toast.LENGTH_SHORT).show()
            return
        }

        val file = File(filePath)
        val requestFile = RequestBody.create(MediaType.parse("image/*"), file)
        val imagePart = MultipartBody.Part.createFormData("avatar", file.name, requestFile)

        // Convert text inputs to request bodies
        val fullnameBody = RequestBody.create(MediaType.parse("text/plain"), binding.fullname.text.toString())
        val emailBody = RequestBody.create(MediaType.parse("text/plain"), binding.email.text.toString())
        val usernameBody = RequestBody.create(MediaType.parse("text/plain"), binding.username.text.toString())
        val phoneNumberBody = RequestBody.create(MediaType.parse("text/plain"), binding.phonenumber.text.toString())
        val passwordBody = RequestBody.create(MediaType.parse("text/plain"), binding.password.text.toString())

        // Call API
        RetrofitRegistrationClient.instance.registerUser(
            fullnameBody, emailBody, usernameBody, phoneNumberBody, passwordBody, imagePart
        ).enqueue(object : Callback<RegistrationResponse> {
            override fun onResponse(call: Call<RegistrationResponse>, response: Response<RegistrationResponse>) {
                if (response.isSuccessful && response.body() != null) {
                    Toast.makeText(this@Signup, response.body()!!.message, Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this@Signup, "Signup Failed", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<RegistrationResponse>, t: Throwable) {
                Toast.makeText(this@Signup, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                Log.e("SignupError", t.message.toString())
            }
        })
    }

    // Function to get the real file path from URI safely
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