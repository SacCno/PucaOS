# PucaOS

PucaOS is an embedded system written for the Arduino Nano ESP32. It includes an accompanying Android app, which allows for remote interfacing with the Arduino.
The Ardduino takes in measurements from DS18B20 temperature probes and compares them against a saved schedule. It uses that information to control a Tasmota smart plug
through LAN HTTP commands, to temperature control a terrarium.

**This project is in development. There are safeguards and failsafes that are yet to be implemented**

## Features

* Temperature reading.
* Create and set custom schedules with:
  1. Custom starting times.
  2. Custom set temperatures.
* Manually control the lamp with manual control mode.

## Dependencies

* Mongoose
* DallasTemperature
* OneWire
* Wire
* Adafruit

## Technologies
* WiFi
* MQTT
* HTTP
* C/C++
* Kotlin/Java

## Included

1. The source code for the Arduino program.
2. The source code for the Android app.
3. The gerbers and KiCad project files for the PCB.
4. The .STEP files for the housing and Stevenson Screen.

## Implementation

Clone the repository

```bash
git clone https://github.com/SacCno/PucaOS
```

### Arduino

Before beginning the steps below, first make sure to copy 'config.example.h' as 'config.h' and fill in the values with your own.

```bash
cp config.example.h config.h
```

1. Ensure all files are in the same directory as *'PucaOSArduino.ino'*
2. Open Arduino IDE
3. Click 'file -> open...'
4. Select *'PucaOSArduino.ino'*
5. Ensure Arduino Nano ESP32 is connected to the computer and that the appropriate board and port are selected in 'tools'
6. Click 'Upload Sketch'
7. On first start-up, open serial monitor in Arduino IDE where you will be prompted to enter your MQTT password.


### Android

In Android Studio, open the *PucaOS-Android* folder as a project, and let the gradle build.


## Licence
MIT Licence

See LICENCE.md