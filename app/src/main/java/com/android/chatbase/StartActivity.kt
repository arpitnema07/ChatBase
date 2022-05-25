package com.android.chatbase

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class StartActivity : AppCompatActivity() {
    lateinit var login: Button
    lateinit var register: Button
    lateinit var firebaseUser: FirebaseUser
    override fun onStart() {
        super.onStart()
        //        check if user is null
        if(FirebaseAuth.getInstance().currentUser!=null){
            firebaseUser = FirebaseAuth.getInstance().currentUser!!
            startActivity(Intent(this@StartActivity, MainActivity::class.java))
            finish()
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)
        login = findViewById(R.id.login)
        register = findViewById(R.id.register)
        login.setOnClickListener {
            startActivity(
                Intent(
                    this@StartActivity,
                    LoginActivity::class.java
                )
            )
        }
        register.setOnClickListener {
            startActivity(
                Intent(
                    this@StartActivity,
                    RegisterActivity::class.java
                )
            )
        }
    }
}