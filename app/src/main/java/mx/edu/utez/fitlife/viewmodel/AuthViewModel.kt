package mx.edu.utez.fitlife.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import mx.edu.utez.fitlife.data.local.PreferencesManager
import mx.edu.utez.fitlife.data.model.User
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
            User(
                id = preferencesManager.getUserId(),
                name = preferencesManager.getUserName() ?: "",
                email = preferencesManager.getUserEmail() ?: ""
            )
        } else null
    )
    val currentUser: StateFlow<User?> = _currentUser
    
    // Estados para recuperación de contraseña
    private val _resetPasswordEmail = MutableStateFlow<String?>(null)
    val resetPasswordEmail: StateFlow<String?> = _resetPasswordEmail
    
    private val _resetToken = MutableStateFlow<String?>(null)
    val resetToken: StateFlow<String?> = _resetToken
    
    private val _resetSuccess = MutableStateFlow(false)
    val resetSuccess: StateFlow<Boolean> = _resetSuccess
    
    fun login(email: String, password: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            
            val deviceId = preferencesManager.getDeviceId()
            
            repository.login(email, password, deviceId)
                .onSuccess { response ->
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
            
            val deviceId = preferencesManager.getDeviceId()
            
            repository.register(name, email, password, deviceId)
                .onSuccess { response ->
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
    
    fun forgotPassword(email: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            
            repository.forgotPassword(email)
                .onSuccess { response ->
                    _resetPasswordEmail.value = email
                    _resetToken.value = response.resetToken // Para desarrollo
                    onSuccess()
                }
                .onFailure { exception ->
                    _error.value = exception.message ?: "Error al solicitar recuperación"
                }
            
            _isLoading.value = false
        }
    }
    
    fun verifyResetToken(token: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            
            val email = _resetPasswordEmail.value ?: return@launch
            
            repository.verifyResetToken(email, token)
                .onSuccess { response ->
                    if (response.valid) {
                        _resetToken.value = token
                        onSuccess()
                    } else {
                        _error.value = "Token inválido"
                    }
                }
                .onFailure { exception ->
                    _error.value = exception.message ?: "Error al verificar código"
                }
            
            _isLoading.value = false
        }
    }
    
    fun resetPassword(newPassword: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            
            val email = _resetPasswordEmail.value ?: return@launch
            val token = _resetToken.value ?: return@launch
            
            repository.resetPassword(email, token, newPassword)
                .onSuccess { response ->
                    if (response.success) {
                        _resetSuccess.value = true
                        clearResetState()
                        onSuccess()
                    } else {
                        _error.value = "Error al restablecer contraseña"
                    }
                }
                .onFailure { exception ->
                    _error.value = exception.message ?: "Error al restablecer contraseña"
                }
            
            _isLoading.value = false
        }
    }
    
    fun clearResetState() {
        _resetPasswordEmail.value = null
        _resetToken.value = null
        _resetSuccess.value = false
    }
    
    fun logout() {
        preferencesManager.clearSession()
        _isLoggedIn.value = false
        _currentUser.value = null
    }
    
    fun checkSession() {
        if (preferencesManager.isLoggedIn()) {
            _currentUser.value = User(
                id = preferencesManager.getUserId(),
                name = preferencesManager.getUserName() ?: "",
                email = preferencesManager.getUserEmail() ?: ""
            )
        }
    }
    
    fun clearError() {
        _error.value = null
    }
    
    fun getDeviceId(): String = preferencesManager.getDeviceId()
}
