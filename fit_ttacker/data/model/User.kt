package com.example.fit_ttacker.data.model

data class User(
    val username: String,
    val email: String,
    val timestamp: Long = System.currentTimeMillis()
)