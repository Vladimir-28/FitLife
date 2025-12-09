package mx.edu.utez.fitlife.data.repository

import mx.edu.utez.fitlife.data.Api.ApiClient
import mx.edu.utez.fitlife.data.model.ActivityDay

class ActivityRepository {
    private val apiService = ApiClient.instance

    // ========== CRUD OPERATIONS ==========

    /**
     * GET: Obtener todas las actividades desde la API
     */
    suspend fun getAllActivities(): List<ActivityDay> {
        return try {
            val response = apiService.getActivities()
            if (response.isSuccessful) {
                response.body() ?: emptyList()
            } else {
                emptyList()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    /**
     * POST: Crear una nueva actividad en la API
     */
    suspend fun createActivity(activity: ActivityDay): ActivityDay? {
        return try {
            val response = apiService.createActivity(activity)
            if (response.isSuccessful) {
                response.body()
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * PUT: Actualizar una actividad existente en la API
     */
    suspend fun updateActivity(id: Int, activity: ActivityDay): ActivityDay? {
        return try {
            val response = apiService.updateActivity(id, activity)
            if (response.isSuccessful) {
                response.body()
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * DELETE: Eliminar una actividad de la API
     */
    suspend fun deleteActivity(id: Int): Boolean {
        return try {
            val response = apiService.deleteActivity(id)
            response.isSuccessful
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}

