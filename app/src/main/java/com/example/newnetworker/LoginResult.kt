package com.example.newnetworker

sealed class LoginResult {
    object Loading : LoginResult()
    object Success : LoginResult()
    data class Error(val message: String) : LoginResult()
    object Idle : LoginResult()
}