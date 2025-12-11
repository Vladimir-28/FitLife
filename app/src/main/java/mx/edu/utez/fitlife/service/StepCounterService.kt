package mx.edu.utez.fitlife.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.location.Location
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.os.Looper
import androidx.core.app.NotificationCompat
import com.google.android.gms.location.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import mx.edu.utez.fitlife.MainActivity
import mx.edu.utez.fitlife.R

class StepCounterService : Service(), SensorEventListener {

    private val binder = LocalBinder()
    private var sensorManager: SensorManager? = null
    private var stepCounter: Sensor? = null
    private var accelerometer: Sensor? = null
    
    // Location para GPS
    private var fusedLocationClient: FusedLocationProviderClient? = null
    private var locationCallback: LocationCallback? = null
    private var lastLocation: Location? = null
    
    // Estados
    private var initialStepCount = 0f
    private var isInitialized = false
    private var startTime: Long = 0
    
    private val _stepCount = MutableStateFlow(0)
    val stepCount: StateFlow<Int> = _stepCount
    
    private val _distance = MutableStateFlow(0.0)
    val distance: StateFlow<Double> = _distance
    
    private val _elapsedTime = MutableStateFlow(0L)
    val elapsedTime: StateFlow<Long> = _elapsedTime
    
    private val _isRunning = MutableStateFlow(false)
    val isRunning: StateFlow<Boolean> = _isRunning
    
    private val _isSensorAvailable = MutableStateFlow(false)
    val isSensorAvailable: StateFlow<Boolean> = _isSensorAvailable

    companion object {
        const val CHANNEL_ID = "FitLifeStepCounter"
        const val NOTIFICATION_ID = 1
        const val ACTION_START = "ACTION_START"
        const val ACTION_STOP = "ACTION_STOP"
        const val ACTION_RESET = "ACTION_RESET"
        
        // Constante para cálculo de distancia por paso (metros)
        const val STEP_LENGTH_METERS = 0.75
    }

    inner class LocalBinder : Binder() {
        fun getService(): StepCounterService = this@StepCounterService
    }

    override fun onBind(intent: Intent?): IBinder = binder

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        initializeSensors()
        initializeLocationServices()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Contador de Pasos",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Notificación del contador de pasos en segundo plano"
                setShowBadge(false)
            }
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }

    private fun initializeSensors() {
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as? SensorManager
        sensorManager?.let { manager ->
            stepCounter = manager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
            if (stepCounter == null) {
                accelerometer = manager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
            }
            _isSensorAvailable.value = (stepCounter != null || accelerometer != null)
        }
    }

    private fun initializeLocationServices() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                locationResult.lastLocation?.let { newLocation ->
                    lastLocation?.let { previous ->
                        val distanceIncrement = previous.distanceTo(newLocation)
                        // Solo agregar si el movimiento es significativo (más de 2 metros)
                        if (distanceIncrement > 2 && distanceIncrement < 100) {
                            _distance.value += distanceIncrement / 1000.0 // Convertir a km
                        }
                    }
                    lastLocation = newLocation
                }
            }
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START -> startTracking()
            ACTION_STOP -> stopTracking()
            ACTION_RESET -> resetTracking()
        }
        return START_STICKY
    }

    fun startTracking() {
        if (_isRunning.value) return
        
        _isRunning.value = true
        startTime = System.currentTimeMillis() - _elapsedTime.value
        
        // Iniciar sensor de pasos
        sensorManager?.let { manager ->
            stepCounter?.let { sensor ->
                manager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL)
            } ?: accelerometer?.let { sensor ->
                manager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL)
            }
        }
        
        // Iniciar tracking de ubicación
        startLocationUpdates()
        
        // Iniciar como foreground service
        startForeground(NOTIFICATION_ID, createNotification())
        
        // Timer para actualizar tiempo
        Thread {
            while (_isRunning.value) {
                _elapsedTime.value = System.currentTimeMillis() - startTime
                updateNotification()
                Thread.sleep(1000)
            }
        }.start()
    }

    fun stopTracking() {
        _isRunning.value = false
        sensorManager?.unregisterListener(this)
        stopLocationUpdates()
        stopForeground(STOP_FOREGROUND_REMOVE)
    }

    fun resetTracking() {
        stopTracking()
        _stepCount.value = 0
        _distance.value = 0.0
        _elapsedTime.value = 0
        isInitialized = false
        initialStepCount = 0f
        lastLocation = null
    }

    private fun startLocationUpdates() {
        val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 5000)
            .setMinUpdateIntervalMillis(3000)
            .setMinUpdateDistanceMeters(5f)
            .build()

        try {
            fusedLocationClient?.requestLocationUpdates(
                locationRequest,
                locationCallback!!,
                Looper.getMainLooper()
            )
        } catch (e: SecurityException) {
            // Sin permiso de ubicación
        }
    }

    private fun stopLocationUpdates() {
        locationCallback?.let { callback ->
            fusedLocationClient?.removeLocationUpdates(callback)
        }
    }

    override fun onSensorChanged(event: SensorEvent?) {
        event?.let {
            when (it.sensor.type) {
                Sensor.TYPE_STEP_COUNTER -> {
                    if (!isInitialized) {
                        initialStepCount = it.values[0]
                        isInitialized = true
                    } else {
                        val currentSteps = (it.values[0] - initialStepCount).toInt()
                        if (currentSteps > _stepCount.value) {
                            _stepCount.value = currentSteps
                            // Calcular distancia aproximada basada en pasos si no hay GPS
                            if (_distance.value == 0.0) {
                                _distance.value = currentSteps * STEP_LENGTH_METERS / 1000.0
                            }
                        }
                    }
                }
                Sensor.TYPE_ACCELEROMETER -> {
                    val x = it.values[0]
                    val y = it.values[1]
                    val z = it.values[2]
                    val magnitude = Math.sqrt((x * x + y * y + z * z).toDouble()).toFloat()
                    if (magnitude > 12f) {
                        _stepCount.value = _stepCount.value + 1
                        _distance.value = _stepCount.value * STEP_LENGTH_METERS / 1000.0
                    }
                }
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    private fun createNotification(): Notification {
        val intent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val stopIntent = Intent(this, StepCounterService::class.java).apply {
            action = ACTION_STOP
        }
        val stopPendingIntent = PendingIntent.getService(
            this, 1, stopIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("FitLife - Rastreando actividad")
            .setContentText("${_stepCount.value} pasos | ${formatDistance(_distance.value)} | ${formatTime(_elapsedTime.value)}")
            .setSmallIcon(R.drawable.ic_steps_notification)
            .setContentIntent(pendingIntent)
            .addAction(android.R.drawable.ic_media_pause, "Detener", stopPendingIntent)
            .setOngoing(true)
            .setSilent(true)
            .build()
    }

    private fun updateNotification() {
        val notification = createNotification()
        val manager = getSystemService(NotificationManager::class.java)
        manager.notify(NOTIFICATION_ID, notification)
    }

    private fun formatDistance(km: Double): String {
        return String.format("%.2f km", km)
    }

    private fun formatTime(millis: Long): String {
        val seconds = (millis / 1000) % 60
        val minutes = (millis / (1000 * 60)) % 60
        val hours = (millis / (1000 * 60 * 60))
        return if (hours > 0) {
            String.format("%dh %02dm", hours, minutes)
        } else {
            String.format("%dm %02ds", minutes, seconds)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        stopTracking()
    }

    // Getters para valores actuales
    fun getCurrentSteps(): Int = _stepCount.value
    fun getCurrentDistance(): Double = _distance.value
    fun getCurrentElapsedTime(): Long = _elapsedTime.value
}

