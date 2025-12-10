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
                // Si no hay datos, mostrar lista vacía (sin datos hardcodeados)
            } catch (e: Exception) {
                _error.value = "Error al cargar actividades: ${e.message}"
                // En caso de error, mostrar lista vacía
                _activities.value = emptyList()
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

}
