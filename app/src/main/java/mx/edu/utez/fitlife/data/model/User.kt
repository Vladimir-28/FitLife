package mx.edu.utez.fitlife.data.model

import com.google.gson.annotations.SerializedName

data class User(
    @SerializedName("id")
    val id: Int? = null,
    @SerializedName("name")
    val name: String,
    @SerializedName("email")
    val email: String
)

data class LoginRequest(
    val email: String,
    val password: String
)

data class RegisterRequest(
    val name: String,
    val email: String,
    val password: String
)

data class AuthResponse(
    @SerializedName("message")
    val message: String,
    @SerializedName("user")
    val user: User,
    @SerializedName("token")
    val token: String
)

