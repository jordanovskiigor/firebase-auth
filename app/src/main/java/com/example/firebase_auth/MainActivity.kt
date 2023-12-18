package com.example.firebase_auth

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    lateinit var auth:FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        auth = FirebaseAuth.getInstance()
        val signupLink = findViewById<TextView>(R.id.tv_signup)

        signupLink.setOnClickListener {
            val intent = (Intent(this@MainActivity,RegisterActivity::class.java))
            startActivity(intent)
        }
    }


}