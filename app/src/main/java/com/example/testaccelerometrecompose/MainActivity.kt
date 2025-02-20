package com.example.testaccelerometrecompose

import com.example.testaccelerometrecompose.ui.theme.TestAccelerometreComposeTheme
import androidx.compose.ui.unit.dp
import android.annotation.SuppressLint
import androidx.compose.ui.graphics.Color
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll

class MainActivity : ComponentActivity(), SensorEventListener {

    private lateinit var sensorManager: SensorManager
    private var lastUpdate: Long = 0
    private var lastLightUpdate: Long = 0

    // Mutable state variables to store sensor data
    private lateinit var lightValue: MutableState<Float>
    private lateinit var intensityLevel: MutableState<String>

    private var color: MutableState<Boolean> = mutableStateOf(false)

    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager

        val accelerometer: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        val lightSensor: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT)

        // Initializing mutable states for light sensor
        lightValue = mutableStateOf(0f)
        intensityLevel = mutableStateOf("UNKNOWN")

        if (accelerometer != null) {
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL)
            lastUpdate = System.currentTimeMillis()
        }

        if (lightSensor != null) {
            sensorManager.registerListener(this, lightSensor, SensorManager.SENSOR_DELAY_NORMAL)
        }

        enableEdgeToEdge()
        setContent {
            TestAccelerometreComposeTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) {
                    SensorsInfo(color, accelerometer, lightSensor, lightValue, intensityLevel)
                }
            }
        }
    }

    override fun onSensorChanged(event: SensorEvent) {
        when (event.sensor.type) {
            Sensor.TYPE_ACCELEROMETER -> getAccelerometer(event)
            Sensor.TYPE_LIGHT -> handleLightSensor(event, lightValue, intensityLevel)
        }
    }

    // Processes light sensor data and updates the UI state
    private fun handleLightSensor(event: SensorEvent, lightValueState: MutableState<Float>, intensityState: MutableState<String>) {
        val lightValue = event.values[0]
        val currentTime = System.currentTimeMillis()

        val maxRange = event.sensor.maximumRange
        val limitLow = maxRange / 3
        val limitHigh = (2 * maxRange) / 3

        val intensityLevel = when {
            lightValue < limitLow -> "LOW Intensity"
            lightValue < limitHigh -> "MEDIUM Intensity"
            else -> "HIGH Intensity"
        }

        // Updates UI only if the light intensity change exceeds 200 lx and at least 1 second has passed
        if ((Math.abs(lightValue - lightValueState.value) >= 200) && ((currentTime - lastLightUpdate) >= 1000)) {
            lastLightUpdate = currentTime

            lightValueState.value = lightValue
            intensityState.value = intensityLevel
        }
    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
        if (p0?.type == Sensor.TYPE_ACCELEROMETER) {
            Toast.makeText(this, getString(R.string.changAcc, p1), Toast.LENGTH_SHORT).show()
        }
    }

    // Processes accelerometer data and updates UI accordingly
    private fun getAccelerometer(event: SensorEvent) {
        val accelerationSquareRootThreshold = 200
        val timeThreshold = 1000
        val values = event.values

        val x = values[0]
        val y = values[1]
        val z = values[2]
        val accelerationSquareRoot = (x * x + y * y + z * z
                / (SensorManager.GRAVITY_EARTH * SensorManager.GRAVITY_EARTH))
        val actualTime = System.currentTimeMillis()

        if (accelerationSquareRoot >= accelerationSquareRootThreshold) {
            if (actualTime - lastUpdate < timeThreshold) {
                return
            }
            lastUpdate = actualTime
            Toast.makeText(this, R.string.shuffed, Toast.LENGTH_SHORT).show()
            color.value = !(color.value)
        }
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }
}

@Composable
fun SensorsInfo(
    color: MutableState<Boolean>,
    accelerometer: Sensor?,
    lightSensor: Sensor?,
    lightValue: MutableState<Float>,
    intensityLevel: MutableState<String>
) {
    // 🔹 List to store previous light sensor values
    val lightHistory = remember { mutableStateListOf<String>() }

    Column(modifier = Modifier.fillMaxSize()) {

        // Upper section: changes color based on accelerometer input
        Card(
            modifier = Modifier
                .fillMaxSize()
                .weight(3f),
            colors = CardDefaults.cardColors(if (color.value) Color.Red else Color.Green),
            shape = CardDefaults.shape,
            elevation = CardDefaults.cardElevation(),
            border = BorderStroke(5.dp, if (color.value) Color.Black else Color.LightGray)
        ) {}

        // Middle section: accelerometer information
        Card(
            modifier = Modifier
                .fillMaxSize()
                .weight(2f),
            colors = CardDefaults.cardColors(Color.White),
            elevation = CardDefaults.cardElevation(),
            border = BorderStroke(2.dp, Color.Gray)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                if (accelerometer != null) {
                    Text(text = "Shake to get a toast and to switch color")
                    Text(text = "Sensor Info:")
                    Text(text = "Max Range: ${accelerometer.maximumRange}")
                    Text(text = "Resolution: ${accelerometer.resolution}")
                } else {
                    Text(text = "Sorry, there is no accelerometer")
                }
            }
        }

        // Lower section: light sensor information (Scrollable history)
        Card(
            modifier = Modifier
                .fillMaxSize()
                .weight(2f),
            colors = CardDefaults.cardColors(Color.Yellow),
            elevation = CardDefaults.cardElevation(),
            border = BorderStroke(2.dp, Color.Gray)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()), // Enables scrolling if needed
                horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally
            ) {
                if (lightSensor != null) {
                    Text(text = "Light Sensor Available")
                    Text(text = "Max Range: ${lightSensor.maximumRange}")

                    // 🔹 Adding new values to history when the sensor updates
                    LaunchedEffect(lightValue.value) {
                        lightHistory.add("New value: ${lightValue.value} lx - ${intensityLevel.value}")
                    }

                    // 🔹 Display all historical light sensor values
                    lightHistory.forEach { value ->
                        Text(text = value)
                    }
                } else {
                    Text(text = "Sorry, there is no light sensor")
                }
            }
        }
    }
}
