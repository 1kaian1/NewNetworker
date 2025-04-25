package com.example.newnetworker

import android.content.Intent
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.navigation.findNavController
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.appcompat.app.AppCompatActivity
import com.example.newnetworker.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth

interface OnFragmentInteractionListener {
    fun onFragmentResumed(title: String)
}

class LoginActivity : AppCompatActivity(), OnFragmentInteractionListener {
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Checkfor current user
        val auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser

        if (currentUser != null) {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        } else {
            binding = ActivityLoginBinding.inflate(layoutInflater)
            setContentView(binding.root)
            setSupportActionBar(binding.appBarLogin.toolbarLogin)
            val navController = findNavController(R.id.nav_host_fragment_content_login)
            setupActionBarWithNavController(navController)
    }
}

    override fun onFragmentResumed(title: String) {
        supportActionBar?.title = title
    }
}
