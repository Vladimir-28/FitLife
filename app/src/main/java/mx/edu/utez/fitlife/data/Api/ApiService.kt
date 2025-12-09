package mx.edu.utez.fitlife.data.Api

import mx.edu.utez.fitlife.data.model.ActivityDay
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

interface ApiService {

    @GET("activities")
    suspend fun getActivities(): Response<List<ActivityDay>>

    @POST("activities")
    suspend fun createActivity(@Body activity: ActivityDay): Response<ActivityDay>

    @PUT("activities/{id}")
    suspend fun updateActivity(
        @Path("id") id: Int,
        @Body activity: ActivityDay
    ): Response<ActivityDay>

    @DELETE("activities/{id}")
    suspend fun deleteActivity(@Path("id") id: Int): Response<Unit>
}

object ApiClient {
    // IMPORTANTE: Cambiar esta IP por la IP de tu computadora donde corre el servidor Flask
    // Para obtener tu IP:
    // - Windows: ipconfig (buscar "IPv4 Address")
    // - Mac/Linux: ifconfig o ip addr
    // - Emulador Android: usar "10.0.2.2" en lugar de localhost
    // - Dispositivo físico: usar la IP de tu red local (ej: 192.168.1.100)
    private const val BASE_URL = "http://192.168.0.123:5000/"

    val instance: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
    
    val authInstance: AuthApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(AuthApiService::class.java)
    }
}
