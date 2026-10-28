package com.example.pucaos

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.exclude
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.TopAppBarColors
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.font.FontWeight
import kotlinx.serialization.json.Json
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerDefaults
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLocale
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.window.Dialog
import com.example.pucaos.ui.theme.Aldo
import com.example.pucaos.ui.theme.ErrorRed
import kotlinx.coroutines.launch
import java.sql.Time

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleScreen(onBack: () -> Unit) {
    val scope = rememberCoroutineScope()

    var showTimePicker by remember { mutableStateOf(false) }
    val timePickerState = rememberTimePickerState(initialHour = 12, initialMinute = 0)

    var power by remember { mutableStateOf(true) }
    var timeframes by remember { mutableStateOf(listOf<Timeframe>()) }

    var enteredTemp by remember { mutableStateOf("") }

    var validationError by remember { mutableStateOf("") }

    suspend fun fetchTimeframes() {
        try {
            val response = MQTTHelper.request("/ph/schedule/check", "/mc/schedule/check") ?: return
            Log.d("JSON", response)
            response.trimIndent()
            val sch = Json.decodeFromString<Schedule>(response)
            timeframes = sch.timeframes
        } catch (e: Exception) {
            Log.e("MQTT", "Failed to fetch schedule. (${e.message})")
        }
    }

    LaunchedEffect(Unit) {
        fetchTimeframes()
    }

    if(showTimePicker) {
        Dialog(onDismissRequest = {showTimePicker = false}) {
            Surface(
                shape = MaterialTheme.shapes.extraLarge,
                color = MaterialTheme.colorScheme.background,
                tonalElevation = 6.dp,
                modifier = Modifier.padding(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Select Time",
                        modifier = Modifier.fillMaxWidth().padding(bottom = 20.dp),
                        color = MaterialTheme.colorScheme.primary
                    )
                    TimePicker(state = timePickerState,
                        colors = TimePickerDefaults.colors(
                            clockDialColor = MaterialTheme.colorScheme.onSurfaceVariant
                        ))
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(top = 24.dp),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(onClick = { showTimePicker = false }) {
                            Text("Ok", fontFamily = Aldo, fontSize = 24.sp)
                        }
                    }
                }
            }
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Schedule",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = Aldo)},
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                    navigationIconContentColor = MaterialTheme.colorScheme.primary,
                    scrolledContainerColor = MaterialTheme.colorScheme.primary,
                    actionIconContentColor = MaterialTheme.colorScheme.primary,
                    subtitleContentColor = MaterialTheme.colorScheme.primary
                ),
                windowInsets = WindowInsets.systemBars.exclude(WindowInsets.navigationBars)
            )
        }
    ) { innerPadding ->
        Box(modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding))
        {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally)

            {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.onSurfaceVariant),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                        contentColor = MaterialTheme.colorScheme.primary
                    ))
                {
                    Column(modifier = Modifier.padding(16.dp)) {
                        OutlinedButton(
                            modifier = Modifier.fillMaxWidth(),
                            onClick = { showTimePicker = true },
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurfaceVariant),
                            colors = ButtonDefaults.buttonColors(
                                contentColor = MaterialTheme.colorScheme.primary,
                                containerColor = MaterialTheme.colorScheme.surface,
                            ),
                            shape = RoundedCornerShape(5.dp)
                        )
                        {
                            val formattedTime = String.format(
                                LocalLocale.current.platformLocale,
                                "%02d:%02d",
                                timePickerState.hour,
                                timePickerState.minute
                            )

                            Text(
                                text = "Start Time: $formattedTime",
                                color = MaterialTheme.colorScheme.primary,
                                fontFamily = Aldo
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            modifier = Modifier.fillMaxWidth(),
                            value = enteredTemp,
                            onValueChange = {
                                if(it != "") {
                                    val result = it.toFloatOrNull()
                                    if(result != null) {
                                        if(result <= 60.0) enteredTemp = it
                                    }
                                } else enteredTemp = ""

                                validationError = ""
                            },
                            shape = RoundedCornerShape(5.dp),
                            label = { Text("Target Temperature (°C)", fontFamily = Aldo) },
                            colors = if (validationError.isNotEmpty()) {
                                OutlinedTextFieldDefaults.colors(
                                    unfocusedBorderColor = ErrorRed,
                                    focusedBorderColor = ErrorRed,

                                    focusedLabelColor = ErrorRed,
                                    unfocusedLabelColor = ErrorRed,
                                )} else {
                                    OutlinedTextFieldDefaults.colors(
                                        unfocusedBorderColor = MaterialTheme.colorScheme.primary,
                                        focusedBorderColor = MaterialTheme.colorScheme.primary,

                                        focusedLabelColor = MaterialTheme.colorScheme.primary,
                                        unfocusedLabelColor = MaterialTheme.colorScheme.primary,
                                    )
                                }

                        )
                        if (validationError.isNotEmpty())
                        {
                            Text(
                                text = validationError,
                                fontSize = 12.sp,
                                color = ErrorRed,
                                modifier = Modifier.padding(start = 8.dp, top = 8.dp)
                            )
                        }
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(vertical = 8.dp)
                        ) {
                            Checkbox(checked = power, onCheckedChange = { power = it },
                                colors = CheckboxDefaults.colors(
                                    uncheckedColor = MaterialTheme.colorScheme.primary
                                ))
                            Text("Powered",
                                fontFamily = Aldo)
                        }
                        Button(
                            onClick = {
                                val minuteofday = (timePickerState.hour * 60) + timePickerState.minute
                                val temp = enteredTemp.toFloatOrNull()

                                when {
                                    temp == null || temp < 10 || temp > 60 -> {
                                        validationError = "Invalid Temperature: Temp must be a number between 10 and 60"
                                    }
                                    timeframes.any {it.minute_of_day == minuteofday} -> {
                                        validationError = "Invalid Time: This time is already assigned"
                                    }
                                    timeframes.size >= 12 -> {
                                        validationError = "Maximum 12 allowed time slots"
                                    }
                                    else -> {
                                        var intTemp = (temp * 100).toInt()
                                        val enteredPower = if(power) 1 else 0
                                        val newList = (timeframes + Timeframe(minuteofday, intTemp, enteredPower))
                                            .sortedBy { it.minute_of_day }

                                        scope.launch {
                                            val json = Json.encodeToString(Schedule(newList.size, newList))
                                            val result = MQTTHelper.request("/ph/schedule/update", "/mc/schedule/update", json)

                                            when (result) {
                                                "valid" -> {
                                                    timeframes = newList
                                                    Log.d("MQTT", "Schedule updated on server successfully.")

                                                    enteredTemp = ""
                                                    power = true
                                                    timePickerState.hour = 12
                                                    timePickerState.minute = 0
                                                }
                                                "invalid" -> {
                                                    Log.e("MQTT", "Server claims new schedule is invalid.")
                                                }
                                                else -> {
                                                    Log.e("MQTT", "Error syncing schedule with server.")
                                                }
                                            }
                                        }
                                    }
                                }
                            },
                            modifier = Modifier.align(Alignment.End),
                            shape = RoundedCornerShape(5.dp),
                            enabled = (timeframes.size < 12)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, tint = MaterialTheme.colorScheme.surface)
                            Text(
                                text = "Add",
                                fontSize = 18.sp,
                                fontFamily = Aldo,
                                color = MaterialTheme.colorScheme.surface
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                LazyColumn(modifier = Modifier.weight(1f)) {
                    items(timeframes) { tf ->
                        val hour = tf.minute_of_day / 60
                        val minute = tf.minute_of_day % 60
                        val timeString = String.format(LocalLocale.current.platformLocale, "%02d:%02d", hour, minute)

                        ListItem(
                            colors = ListItemDefaults.colors(
                                headlineColor = MaterialTheme.colorScheme.primary,
                                supportingColor = MaterialTheme.colorScheme.primary,
                                containerColor = MaterialTheme.colorScheme.background,
                                trailingIconColor = MaterialTheme.colorScheme.primary
                            ),
                            headlineContent = { Text("Start Time: $timeString") },
                            supportingContent = { Text("Temp: ${tf.temperature / 100.0}°C | Power: ${if(tf.lamp_state == 1) "ON" else "OFF"}", fontFamily = Aldo) },
                            trailingContent = {
                                IconButton(onClick = {
                                    val newList = timeframes.filter { it != tf }
                                    scope.launch {
                                        val json = Json.encodeToString(Schedule(newList.size, newList))
                                        val result = MQTTHelper.request("/ph/schedule/update", "/mc/schedule/update", json)

                                        when (result) {
                                            "valid" -> {
                                                timeframes = newList
                                                Log.d("MQTT", "Schedule updated on server successfully.")
                                            }
                                            "invalid" -> {
                                                Log.e("MQTT", "Server claims new schedule is invalid.")
                                            }
                                            else -> {
                                                Log.e("MQTT", "Error syncing schedule with server.")
                                            }
                                        }
                                    }
                                }) {
                                    Icon(Icons.Default.Delete, contentDescription = "Delete")
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}