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
    // 10.0.2.2 permite acceder al host desde el emulador Android.
    // Cambia esta URL si pruebas en dispositivo físico (usa tu IP local).
    private const val BASE_URL = "http://192.168.0.22:5000/"

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
