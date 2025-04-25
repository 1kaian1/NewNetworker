package com.example.newnetworker

import android.app.Application
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
        Firebase.auth.useEmulator("10.0.2.2", 9099) // Use Firebase Emulator
    }
}