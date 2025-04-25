package com.example.newnetworker.ui.creator

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.newnetworker.LoginResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CreatorViewModel : ViewModel() {

    private val DATABASE_URL = "https://newnetworker-56eb7-default-rtdb.firebaseio.com/"
    private val _loginResult = MutableLiveData<LoginResult>()
    val loginResult: LiveData<LoginResult> = _loginResult

    fun login_creator(email: String, password: String) {

        _loginResult.value = LoginResult.Loading

        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                try {
                    val auth = FirebaseAuth.getInstance()
                    auth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                val user = auth.currentUser
                                val uid = user?.uid ?: return@addOnCompleteListener

                                val database = FirebaseDatabase.getInstance(DATABASE_URL)
                                val userRef = database.getReference("Players").child(uid) // CHANGE TO CREATORS
                                val creatorData = HashMap<String, Any>()
                                creatorData["email"] = email
                                creatorData["password"] = password
                                userRef.setValue(creatorData)
                                    .addOnSuccessListener {
                                        _loginResult.postValue(LoginResult.Success)
                                    }
                                    .addOnFailureListener {
                                        _loginResult.postValue(LoginResult.Error("Database error: ${it.message}"))
                                    }
                            } else {
                                _loginResult.postValue(LoginResult.Error("Authentication failed: ${task.exception?.message}"))
                            }
                        }
                } catch (e: Exception) {
                    LoginResult.Error("Login failed: ${e.message}")
                }
            }
        }
    }

    fun resetLoginResult() {
        _loginResult.value = LoginResult.Idle
    }
}