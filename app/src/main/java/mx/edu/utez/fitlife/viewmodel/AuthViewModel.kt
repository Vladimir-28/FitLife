package mx.edu.utez.fitlife.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import mx.edu.utez.fitlife.data.local.PreferencesManager
import mx.edu.utez.fitlife.data.model.AuthResponse
import mx.edu.utez.fitlife.data.repository.AuthRepository

class AuthViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = AuthRepository()
    private val preferencesManager = PreferencesManager(application)
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading
    
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error
    
    private val _isLoggedIn = MutableStateFlow(preferencesManager.isLoggedIn())
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn
    
    private val _currentUser = MutableStateFlow(
        if (preferencesManager.isLoggedIn()) {
            mx.edu.utez.fitlife.data.model.User(
                id = preferencesManager.getUserId(),
                name = preferencesManager.getUserName() ?: "",
                email = preferencesManager.getUserEmail() ?: ""
            )
        } else null
    )
    val currentUser: StateFlow<mx.edu.utez.fitlife.data.model.User?> = _currentUser
    
    fun login(email: String, password: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            
            repository.login(email, password)
                .onSuccess { response ->
                    // Guardar sesión
                    response.user.id?.let { userId ->
                        preferencesManager.saveUser(
                            userId,
                            response.user.name,
                            response.user.email,
                            response.token
                        )
                    }
                    _currentUser.value = response.user
                    _isLoggedIn.value = true
                    onSuccess()
                }
                .onFailure { exception ->
                    _error.value = exception.message ?: "Error al iniciar sesión"
                }
            
            _isLoading.value = false
        }
    }
    
    fun register(name: String, email: String, password: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            
            repository.register(name, email, password)
                .onSuccess { response ->
                    // Guardar sesión
                    response.user.id?.let { userId ->
                        preferencesManager.saveUser(
                            userId,
                            response.user.name,
                            response.user.email,
                            response.token
                        )
                    }
                    _currentUser.value = response.user
                    _isLoggedIn.value = true
                    onSuccess()
                }
                .onFailure { exception ->
                    _error.value = exception.message ?: "Error al registrar usuario"
                }
            
            _isLoading.value = false
        }
    }
    
    fun logout() {
        preferencesManager.clearSession()
        _isLoggedIn.value = false
        _currentUser.value = null
    }
    
    fun checkSession() {
        if (preferencesManager.isLoggedIn()) {
            _currentUser.value = mx.edu.utez.fitlife.data.model.User(
                id = preferencesManager.getUserId(),
                name = preferencesManager.getUserName() ?: "",
                email = preferencesManager.getUserEmail() ?: ""
            )
        }
    }
}

