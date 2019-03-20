package com.example.beru

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.google.firebase.database.*

class MainActivity : AppCompatActivity() {

    lateinit var dataBase: DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        dataBase = FirebaseDatabase.getInstance().getReference("HW")
    }

    fun send(view: View){
        val key = dataBase.push().key
        val hw = HW("Graphics Card", 700.0,30.0,30.0,30.0, key.toString() )
        dataBase.child(key.toString()).setValue(hw).addOnCompleteListener{
            Toast.makeText(this, "Your package is on the way", Toast.LENGTH_LONG).show()
        }
    }
    fun getData(view: View){
        val dataListener = object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                val hw = p0.getValue(HW::class.java)
                Toast.makeText(applicationContext, hw.type, Toast.LENGTH_LONG).show()
            }

            override fun onCancealled(p0: DatabaseError){

            }
        }
        dataBase.addValueEventListener(dataListener)
    }
}
