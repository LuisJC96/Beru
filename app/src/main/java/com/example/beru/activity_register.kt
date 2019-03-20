package com.example.beru

import android.app.Activity
import android.app.Activity.*
import android.content.Intent
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_register.*
import java.util.*

class activity_register : AppCompatActivity() {
    data class Cliente(val uid: String, val username: String, val profileImageUrl: String)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        register_button.setOnClickListener{
            doRegister()
        }
        have_an_account.setOnClickListener {
            val intent = Intent(this, activity_login::class.java)
            startActivity(intent)
        }
    }
    private fun doRegister(){
        val email = email_edittext_login.text.toString()//username_edittext
        val password = password_edittext_login.text.toString()
        val username = username_edittext.text.toString()
        if (email.isEmpty() || password.isEmpty() || username.isEmpty()) {
            Toast.makeText(this, "Please fill out all the fields/pw.", Toast.LENGTH_SHORT).show()
            return
        }
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email,password)
            .addOnCompleteListener{
                Toast.makeText(this, "Created", Toast.LENGTH_SHORT).show()
                uploadImageToFirebaseStorage()

                val intent = Intent(this, activity_login::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }.addOnFailureListener{
                Toast.makeText(this, "Failed", Toast.LENGTH_SHORT).show()
            }
    }
    var selectedPhotoUri: Uri? = null
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode==666 && resultCode == Activity.RESULT_OK && data != null){

            selectedPhotoUri = data.data
            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, selectedPhotoUri)
            photo_user_button.setImageBitmap(bitmap)
        }
    }
    private fun uploadImageToFirebaseStorage() {
        if (selectedPhotoUri == null) return

        val filename = UUID.randomUUID().toString()
        val ref = FirebaseStorage.getInstance().getReference("/images/$filename")

        ref.putFile(selectedPhotoUri!!)
            .addOnSuccessListener {
                Log.d("Beru", "Successfully uploaded image: ${it.metadata?.path}")


                ref.downloadUrl.addOnSuccessListener {
                    Log.d("Beru", "File Location: $it")

                    saveClienteToFirebaseDatabase(it.toString())
                }
            }
            .addOnFailureListener {
                Log.d("Beru", "Failed to upload image to storage: ${it.message}")
            }
    }

    private fun saveClienteToFirebaseDatabase(profileImageUrl: String) {
        val uid = FirebaseAuth.getInstance().uid ?: ""
        val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")

        val user = Cliente(uid, username_edittext.text.toString(), profileImageUrl)

        ref.setValue(user)
            .addOnSuccessListener {
                Log.d("Beru", "Cliente uploaded")
            }
            .addOnFailureListener {
                Log.d("Beru", "FAILED: ${it.message}")
            }
    }

}
