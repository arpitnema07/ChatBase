package com.android.chatbase

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.util.*

class MainActivity : AppCompatActivity() {
    lateinit var firebaseUser: FirebaseUser
    lateinit var reference: DatabaseReference
    override fun onStart() {
        super.onStart()
        // check if user is null
        if(FirebaseAuth.getInstance().currentUser!=null){
            firebaseUser = FirebaseAuth.getInstance().currentUser!!
        } else{
            startActivity(Intent(this@MainActivity, StartActivity::class.java))
            finish()
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        firebaseUser = FirebaseAuth.getInstance().currentUser!!
        reference =
            FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.uid)
        val navView:BottomNavigationView = findViewById(R.id.bottomNavigationView)
        val navController = findNavController(R.id.nav_host_fragment_activity_main)

        navView.setupWithNavController(navController)

        val logout:ImageView = findViewById(R.id.btn_logout)
        logout.setOnClickListener {
            signOut()
        }
    }


    private fun signOut(){
        FirebaseAuth.getInstance().signOut()
        startActivity(Intent(this@MainActivity, StartActivity::class.java))
        finish()
    }


    private fun status(status: String) {
        reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.uid)
        val hashMap = HashMap<String, Any>()
        hashMap["status"] = status
        reference.updateChildren(hashMap) { _, _ -> }
    }

    override fun onResume() {
        super.onResume()
        status("online")
    }

    override fun onPause() {
        super.onPause()
        status("offline")
    }
}