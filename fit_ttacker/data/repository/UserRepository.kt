package com.example.fit_ttacker.data.repository

import android.content.Context
import android.content.SharedPreferences
import org.json.JSONArray
import org.json.JSONObject

data class User(
    val username: String,
    val email: String,
    val password: String
)

class UserRepository(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("PhotoFilterUsers", Context.MODE_PRIVATE)

    private val USERS_KEY = "users"

    // Registrar nuevo usuario
    fun registerUser(username: String, email: String, password: String): Result<String> {
        // Validaciones
        if (username.isBlank() || email.isBlank() || password.isBlank()) {
            return Result.failure(Exception("Todos los campos son obligatorios"))
        }

        if (password.length < 6) {
            return Result.failure(Exception("La contraseña debe tener al menos 6 caracteres"))
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            return Result.failure(Exception("Email inválido"))
        }

        // Verificar si el usuario ya existe
        val users = getUsers()
        if (users.any { it.username == username }) {
            return Result.failure(Exception("El usuario ya existe"))
        }

        if (users.any { it.email == email }) {
            return Result.failure(Exception("El email ya está registrado"))
        }

        // Guardar usuario
        val newUser = User(username, email, password)
        users.add(newUser)
        saveUsers(users)

        return Result.success("Usuario registrado exitosamente")
    }

    // Login
    fun loginUser(username: String, password: String): Result<User> {
        if (username.isBlank() || password.isBlank()) {
            return Result.failure(Exception("Por favor completa todos los campos"))
        }

        val users = getUsers()
        val user = users.find { it.username == username && it.password == password }

        return if (user != null) {
            Result.success(user)
        } else {
            Result.failure(Exception("Usuario o contraseña incorrectos"))
        }
    }

    // Obtener todos los usuarios
    private fun getUsers(): MutableList<User> {
        val usersJson = prefs.getString(USERS_KEY, "[]") ?: "[]"
        val jsonArray = JSONArray(usersJson)
        val users = mutableListOf<User>()

        for (i in 0 until jsonArray.length()) {
            val userJson = jsonArray.getJSONObject(i)
            users.add(
                User(
                    username = userJson.getString("username"),
                    email = userJson.getString("email"),
                    password = userJson.getString("password")
                )
            )
        }

        return users
    }

    // Guardar usuarios
    private fun saveUsers(users: List<User>) {
        val jsonArray = JSONArray()
        users.forEach { user ->
            val userJson = JSONObject().apply {
                put("username", user.username)
                put("email", user.email)
                put("password", user.password)
            }
            jsonArray.put(userJson)
        }

        prefs.edit().putString(USERS_KEY, jsonArray.toString()).apply()
    }

    // Verificar si hay usuarios registrados
    fun hasUsers(): Boolean {
        return getUsers().isNotEmpty()
    }
}