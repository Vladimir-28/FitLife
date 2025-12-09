package mx.edu.utez.fitlife.data.sensor

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class StepSensorManager(private val context: Context) : SensorEventListener {
    
    private val sensorManager: SensorManager? = 
        context.getSystemService(Context.SENSOR_SERVICE) as? SensorManager
    
    private var stepCounter: Sensor? = null
    private var accelerometer: Sensor? = null
    
    // Valores iniciales para calcular diferencia
    private var initialStepCount = 0f
    private var lastStepCount = 0f
    private var isInitialized = false
    
    // Flows para observar los valores
    private val _stepCount = MutableStateFlow(0)
    val stepCount: StateFlow<Int> = _stepCount
    
    private val _isSensorAvailable = MutableStateFlow(false)
    val isSensorAvailable: StateFlow<Boolean> = _isSensorAvailable
    
    private val _acceleration = MutableStateFlow(Triple(0f, 0f, 0f))
    val acceleration: StateFlow<Triple<Float, Float, Float>> = _acceleration
    
    init {
        initializeSensors()
    }
    
    private fun initializeSensors() {
        sensorManager?.let { manager ->
            // Intentar obtener el sensor de contador de pasos (más preciso)
            stepCounter = manager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
            
            // Si no está disponible, usar acelerómetro como alternativa
            if (stepCounter == null) {
                accelerometer = manager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
            }
            
            _isSensorAvailable.value = (stepCounter != null || accelerometer != null)
        }
    }
    
    fun startListening() {
        sensorManager?.let { manager ->
            stepCounter?.let { sensor ->
                manager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL)
            } ?: accelerometer?.let { sensor ->
                manager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL)
            }
        }
    }
    
    fun stopListening() {
        sensorManager?.unregisterListener(this)
    }
    
    override fun onSensorChanged(event: SensorEvent?) {
        event?.let {
            when (it.sensor.type) {
                Sensor.TYPE_STEP_COUNTER -> {
                    if (!isInitialized) {
                        initialStepCount = it.values[0]
                        lastStepCount = it.values[0]
                        isInitialized = true
                    } else {
                        val currentSteps = it.values[0] - initialStepCount
                        val newSteps = (currentSteps - (lastStepCount - initialStepCount)).toInt()
                        
                        if (newSteps > 0) {
                            _stepCount.value = _stepCount.value + newSteps
                        }
                        lastStepCount = it.values[0]
                    }
                }
                Sensor.TYPE_ACCELEROMETER -> {
                    // Calcular magnitud de aceleración para detectar movimiento
                    val x = it.values[0]
                    val y = it.values[1]
                    val z = it.values[2]
                    _acceleration.value = Triple(x, y, z)
                    
                    // Detectar pasos basado en cambios de aceleración (método simple)
                    val magnitude = Math.sqrt((x * x + y * y + z * z).toDouble()).toFloat()
                    if (magnitude > 12f) { // Umbral para detectar paso
                        _stepCount.value = _stepCount.value + 1
                    }
                }
            }
        }
    }
    
    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // Manejar cambios en la precisión del sensor si es necesario
    }
    
    fun resetStepCount() {
        _stepCount.value = 0
        isInitialized = false
        initialStepCount = 0f
        lastStepCount = 0f
    }
    
    fun getCurrentStepCount(): Int = _stepCount.value
}

