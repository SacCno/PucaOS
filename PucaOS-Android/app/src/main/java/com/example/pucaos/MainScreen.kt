package com.example.pucaos

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.pucaos.ui.theme.Aldo
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.transformLatest
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

@Composable
fun MainScreen(onNavigateToSchedule: () -> Unit) {

    val scope = rememberCoroutineScope()

    var isAutoMode by remember { mutableStateOf(false) }

    val staleAwareFlow = remember {
        MQTTHelper.observeTopic("/mc/temperature").transformLatest { newTemp ->
            emit(buildAnnotatedString {
                withStyle( style = SpanStyle(fontSize = 48.sp)) {
                    append(newTemp)
                    append("°C")
                }
            })
            delay(10000.milliseconds)
            emit(buildAnnotatedString {
                withStyle(style = SpanStyle(fontSize = 24.sp)) {
                    append("ERROR")
                }
            })
        }
    }
    val temperatureText by staleAwareFlow.collectAsStateWithLifecycle(initialValue = buildAnnotatedString { withStyle(style = SpanStyle(fontSize = 24.sp)) {append("ERROR")} })




    LaunchedEffect(Unit) {
        if(MQTTHelper.fetchMode() == "auto") isAutoMode = true
        else isAutoMode = false
    }


    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {
                Spacer(modifier = Modifier.height(128.dp))
                Image(
                    painter = painterResource(id = R.drawable.whitegecko),
                    contentDescription = "White Gecko Logo",
                    modifier = Modifier.size(325.dp),
                    colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary)
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = buildAnnotatedString {
                        withStyle(style = SpanStyle(fontSize = 50.sp)) {
                            append("PUCA")
                        }

                        append("OS")
                    },
                    fontFamily = Aldo,
                    fontSize = 64.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .align(Alignment.BottomCenter),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Bottom
            ) {
                if (!isAutoMode) {
                    Button(
                        onClick = {
                            scope.launch {
                                Plug.toggle()
                            }},
                        shape = RoundedCornerShape(5.dp)
                    ) {
                        Text(
                            text = "Toggle Lamp",
                            fontFamily = Aldo,
                            fontSize = 48.sp,
                            color = MaterialTheme.colorScheme.background,
                            modifier = Modifier.padding(5.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(64.dp))
                }
                else
                {
                    Spacer(modifier = Modifier.height(116.dp))
                }
                Button(
                    onClick = onNavigateToSchedule,
                    shape = RoundedCornerShape(5.dp)
                ) {
                    Text(
                        text = "Change Schedule",
                        fontFamily = Aldo,
                        fontSize =  24.sp,
                        color = MaterialTheme.colorScheme.background,
                        modifier = Modifier.padding(5.dp)
                    )
                }
                Spacer(modifier = Modifier.height(48.dp))
            }

            Row(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(horizontal = 20.dp, vertical = 0.dp)
                    .height(64.dp),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Switch(
                    checked = isAutoMode,
                    onCheckedChange = { newVal ->
                        scope.launch {
                            val modeStr = if (newVal) "auto" else "manual"
                            MQTTHelper.publish("/ph/command/mode", modeStr)
                            isAutoMode = newVal
                        }
                    },
                    colors = SwitchDefaults.colors(
                        uncheckedBorderColor = MaterialTheme.colorScheme.primary,
                        checkedBorderColor = MaterialTheme.colorScheme.primary,
                        uncheckedThumbColor = MaterialTheme.colorScheme.primary,
                        checkedThumbColor = MaterialTheme.colorScheme.background,
                        uncheckedTrackColor = MaterialTheme.colorScheme.background
                    )
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isAutoMode) "Auto" else "Manual",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                    fontFamily = Aldo,
                    fontSize = 16.sp
                )

            }

            Row(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(horizontal = 20.dp, vertical = 0.dp)
                    .height(64.dp),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = temperatureText,
                    fontWeight = FontWeight.Normal,
                    color = MaterialTheme.colorScheme.primary
                )
            }

        }

    }
}