package mx.edu.utez.fitlife.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import mx.edu.utez.fitlife.data.model.ActivityDay
import mx.edu.utez.fitlife.data.repository.ActivityRepository

class ActivityViewModel : ViewModel() {

    private val repository = ActivityRepository()

    private val _activities = MutableStateFlow<List<ActivityDay>>(emptyList())
    val activities: StateFlow<List<ActivityDay>> = _activities

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    // Mantener compatibilidad con el código existente que usa weeklyActivity
    val weeklyActivity: StateFlow<List<ActivityDay>> = _activities

    init {
        loadActivities()
    }

    /**
     * Cargar todas las actividades desde la API
     */
    fun loadActivities() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val result = repository.getAllActivities()
                _activities.value = result
                // Si no hay datos, usar datos de ejemplo como fallback
                if (result.isEmpty()) {
                    _activities.value = getDefaultActivities()
                }
            } catch (e: Exception) {
                _error.value = "Error al cargar actividades: ${e.message}"
                // En caso de error, usar datos por defecto
                _activities.value = getDefaultActivities()
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Crear una nueva actividad
     */
    fun createActivity(activity: ActivityDay) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val result = repository.createActivity(activity)
                if (result != null) {
                    loadActivities() // Recargar lista después de crear
                } else {
                    _error.value = "Error al crear la actividad"
                }
            } catch (e: Exception) {
                _error.value = "Error al crear actividad: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Actualizar una actividad existente
     */
    fun updateActivity(id: Int, activity: ActivityDay) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val result = repository.updateActivity(id, activity)
                if (result != null) {
                    loadActivities() // Recargar lista después de actualizar
                } else {
                    _error.value = "Error al actualizar la actividad"
                }
            } catch (e: Exception) {
                _error.value = "Error al actualizar actividad: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Eliminar una actividad
     */
    fun deleteActivity(id: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val success = repository.deleteActivity(id)
                if (success) {
                    loadActivities() // Recargar lista después de eliminar
                } else {
                    _error.value = "Error al eliminar la actividad"
                }
            } catch (e: Exception) {
                _error.value = "Error al eliminar actividad: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Datos por defecto para usar cuando no hay conexión o la API falla
     */
    private fun getDefaultActivities(): List<ActivityDay> {
        return listOf(
            ActivityDay(day = "Lun", steps = 5200, distanceKm = 3.4f, activeTime = "45m"),
            ActivityDay(day = "Mar", steps = 7600, distanceKm = 5.1f, activeTime = "1h 10m"),
            ActivityDay(day = "Mié", steps = 3200, distanceKm = 2.1f, activeTime = "30m"),
            ActivityDay(day = "Jue", steps = 8900, distanceKm = 6.4f, activeTime = "1h 25m"),
            ActivityDay(day = "Vie", steps = 10400, distanceKm = 8.0f, activeTime = "1h 55m"),
            ActivityDay(day = "Sáb", steps = 6500, distanceKm = 4.8f, activeTime = "50m"),
            ActivityDay(day = "Dom", steps = 4000, distanceKm = 2.9f, activeTime = "35m")
        )
    }
}
