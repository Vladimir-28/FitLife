package mx.edu.utez.fitlife.data.repository

import com.google.gson.Gson
import mx.edu.utez.fitlife.data.Api.ApiClient
import mx.edu.utez.fitlife.data.model.*
import okhttp3.ResponseBody

class AuthRepository {
    private val authApiService = ApiClient.authInstance
    private val gson = Gson()
    
    suspend fun login(email: String, password: String, deviceId: String? = null): Result<AuthResponse> {
        return try {
            val request = LoginRequest(email, password, deviceId)
            val response = authApiService.login(request)
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    Result.success(body)
                } else {
                    Result.failure(Exception("Respuesta vacía del servidor"))
                }
            } else {
                val errorMessage = getErrorMessage(response.errorBody())
                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(Exception("Error de conexión: ${e.message ?: "Verifica tu conexión a internet"}"))
        }
    }
    
    suspend fun register(name: String, email: String, password: String, deviceId: String? = null): Result<AuthResponse> {
        return try {
            val request = RegisterRequest(name, email, password, deviceId)
            val response = authApiService.register(request)
            
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    Result.success(body)
                } else {
                    Result.failure(Exception("Respuesta vacía del servidor"))
                }
            } else {
                val errorMessage = getErrorMessage(response.errorBody())
                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(Exception("Error de conexión: ${e.message ?: "Verifica tu conexión a internet"}"))
        }
    }
    
    suspend fun forgotPassword(email: String): Result<ForgotPasswordResponse> {
        return try {
            val request = ForgotPasswordRequest(email)
            val response = authApiService.forgotPassword(request)
            
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    Result.success(body)
                } else {
                    Result.failure(Exception("Respuesta vacía del servidor"))
                }
            } else {
                val errorMessage = getErrorMessage(response.errorBody())
                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(Exception("Error de conexión: ${e.message ?: "Verifica tu conexión a internet"}"))
        }
    }
    
    suspend fun verifyResetToken(email: String, token: String): Result<VerifyTokenResponse> {
        return try {
            val request = VerifyTokenRequest(email, token)
            val response = authApiService.verifyResetToken(request)
            
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    Result.success(body)
                } else {
                    Result.failure(Exception("Respuesta vacía del servidor"))
                }
            } else {
                val errorMessage = getErrorMessage(response.errorBody())
                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(Exception("Error de conexión: ${e.message ?: "Verifica tu conexión a internet"}"))
        }
    }
    
    suspend fun resetPassword(email: String, token: String, newPassword: String): Result<ResetPasswordResponse> {
        return try {
            val request = ResetPasswordRequest(email, token, newPassword)
            val response = authApiService.resetPassword(request)
            
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    Result.success(body)
                } else {
                    Result.failure(Exception("Respuesta vacía del servidor"))
                }
            } else {
                val errorMessage = getErrorMessage(response.errorBody())
                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(Exception("Error de conexión: ${e.message ?: "Verifica tu conexión a internet"}"))
        }
    }
    
    private fun getErrorMessage(errorBody: ResponseBody?): String {
        return try {
            errorBody?.string()?.let { bodyString ->
                if (bodyString.contains("\"error\"")) {
                    try {
                        val errorJson = gson.fromJson(bodyString, Map::class.java)
                        (errorJson["error"] as? String) ?: "Error desconocido"
                    } catch (e: Exception) {
                        val startIndex = bodyString.indexOf("\"error\"") + 9
                        val endIndex = bodyString.indexOf("\"", startIndex)
                        if (endIndex > startIndex) {
                            bodyString.substring(startIndex, endIndex)
                        } else {
                            "Error del servidor"
                        }
                    }
                } else {
                    "Error desconocido"
                }
            } ?: "Error de conexión"
        } catch (e: Exception) {
            "Error al procesar respuesta"
        }
    }
}
