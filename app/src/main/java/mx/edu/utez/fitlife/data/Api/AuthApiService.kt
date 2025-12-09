package mx.edu.utez.fitlife.data.Api

import mx.edu.utez.fitlife.data.model.AuthResponse
import mx.edu.utez.fitlife.data.model.LoginRequest
import mx.edu.utez.fitlife.data.model.RegisterRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApiService {
    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): Response<AuthResponse>
    
    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<AuthResponse>
}

