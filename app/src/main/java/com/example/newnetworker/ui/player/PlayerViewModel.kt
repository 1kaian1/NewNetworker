package com.example.newnetworker.ui.player

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.newnetworker.LoginResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class PlayerViewModel : ViewModel() {

    private val DATABASE_URL = "https://newnetworker-56eb7-default-rtdb.firebaseio.com/"
    private val _loginResult = MutableLiveData<LoginResult>()
    val loginResult: LiveData<LoginResult> = _loginResult

    fun login_player(invitor: String) {
        _loginResult.value = LoginResult.Loading
        viewModelScope.launch {
            try {
                val auth = FirebaseAuth.getInstance()
                auth.signInAnonymously().await()
                val uid = auth.currentUser?.uid ?: throw FirebaseAuthException("error_no_user", "User not found after anonymous sign-in")
                checkInvitorAndSavePlayerData(uid, invitor)
            } catch (e: Exception) {
                _loginResult.postValue(LoginResult.Error("Login failed: ${e.message}"))
            }}
    }

    private suspend fun checkInvitorAndSavePlayerData(uid: String, invitor: String) {
        try {
            val database = FirebaseDatabase.getInstance(DATABASE_URL)
            val invitorRef = database.getReference("Players/$invitor")
            val snapshot = invitorRef.get().await()

            if (snapshot.exists()) {
                val playersRef = database.getReference("Players").child(uid)
                val playerData = HashMap<String, Any>()
                playerData["invitor"] = invitor
                playersRef.setValue(playerData).await() // Use await() for data saving
                _loginResult.postValue(LoginResult.Success)
            } else {
                _loginResult.postValue(LoginResult.Error("Invitor not found."))
                FirebaseAuth.getInstance().currentUser?.delete()?.await()
            }
        } catch (e: Exception) {
            _loginResult.postValue(LoginResult.Error("Database error: ${e.message}"))
        }
    }

    fun resetLoginResult() {
        _loginResult.value = LoginResult.Idle
    }
}
