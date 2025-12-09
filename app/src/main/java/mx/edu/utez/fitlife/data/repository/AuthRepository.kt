package mx.edu.utez.fitlife.data.repository

import com.google.gson.Gson
import mx.edu.utez.fitlife.data.Api.ApiClient
import mx.edu.utez.fitlife.data.model.AuthResponse
import mx.edu.utez.fitlife.data.model.LoginRequest
import mx.edu.utez.fitlife.data.model.RegisterRequest
import okhttp3.ResponseBody

class AuthRepository {
    private val authApiService = ApiClient.authInstance
    private val gson = Gson()
    
    suspend fun login(email: String, password: String): Result<AuthResponse> {
        return try {
            val request = LoginRequest(email, password)
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
            Result.failure(e)
        }
    }
    
    suspend fun register(name: String, email: String, password: String): Result<AuthResponse> {
        return try {
            val request = RegisterRequest(name, email, password)
            val response = authApiService.register(request)
            
            android.util.Log.d("AuthRepository", "Register response code: ${response.code()}")
            android.util.Log.d("AuthRepository", "Register response isSuccessful: ${response.isSuccessful}")
            
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    android.util.Log.d("AuthRepository", "Register success: ${body.user.email}")
                    Result.success(body)
                } else {
                    android.util.Log.e("AuthRepository", "Register: Respuesta vacía del servidor")
                    Result.failure(Exception("Respuesta vacía del servidor"))
                }
            } else {
                val errorMessage = getErrorMessage(response.errorBody())
                android.util.Log.e("AuthRepository", "Register error: $errorMessage")
                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            android.util.Log.e("AuthRepository", "Register exception: ${e.message}", e)
            e.printStackTrace()
            Result.failure(Exception("Error de conexión: ${e.message ?: "Verifica que el servidor esté corriendo"}"))
        }
    }
    
    private fun getErrorMessage(errorBody: ResponseBody?): String {
        return try {
            errorBody?.string()?.let { bodyString ->
                // Intentar extraer el mensaje de error del JSON
                if (bodyString.contains("\"error\"")) {
                    try {
                        val errorJson = gson.fromJson(bodyString, Map::class.java)
                        (errorJson["error"] as? String) ?: "Error desconocido"
                    } catch (e: Exception) {
                        // Si falla el parsing, intentar extraer manualmente
                        val startIndex = bodyString.indexOf("\"error\"") + 9
                        val endIndex = bodyString.indexOf("\"", startIndex)
                        if (endIndex > startIndex) {
                            bodyString.substring(startIndex, endIndex)
                        } else {
                            "Error del servidor"
                        }
                    }
                } else {
                    "Error desconocido: ${bodyString.take(100)}"
                }
            } ?: "Error de conexión"
        } catch (e: Exception) {
            "Error al procesar respuesta: ${e.message ?: "Error desconocido"}"
        }
    }
}

