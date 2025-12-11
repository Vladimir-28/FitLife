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
    val password: String,
    val deviceId: String? = null
)

data class RegisterRequest(
    val name: String,
    val email: String,
    val password: String,
    val deviceId: String? = null
)

data class AuthResponse(
    @SerializedName("message")
    val message: String,
    @SerializedName("user")
    val user: User,
    @SerializedName("token")
    val token: String
)

data class ForgotPasswordRequest(
    val email: String
)

data class ForgotPasswordResponse(
    @SerializedName("message")
    val message: String,
    @SerializedName("success")
    val success: Boolean,
    @SerializedName("resetToken")
    val resetToken: String? = null,
    @SerializedName("email")
    val email: String? = null
)

data class VerifyTokenRequest(
    val email: String,
    val token: String
)

data class VerifyTokenResponse(
    @SerializedName("message")
    val message: String,
    @SerializedName("valid")
    val valid: Boolean
)

data class ResetPasswordRequest(
    val email: String,
    val token: String,
    val newPassword: String
)

data class ResetPasswordResponse(
    @SerializedName("message")
    val message: String,
    @SerializedName("success")
    val success: Boolean
)

data class ErrorResponse(
    @SerializedName("error")
    val error: String
)
