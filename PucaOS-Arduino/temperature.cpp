#include <OneWire.h>
#include <DallasTemperature.h>

#include "platform.h"
#include "temperature.h"

const int SENSOR_PIN = 5;

OneWire oneWire(SENSOR_PIN);

DallasTemperature sensors(&oneWire);

void init_temp()
{
    sensors.begin();
}

bool sensor_temp(int16_t *temp_buffer)
{
    sensors.requestTemperatures();

    float temperature = sensors.getTempCByIndex(0);
    if (temperature == -127.00)
    {
        printSerial("Error: Cannot get temperature data.");
        return false;
    }

    *temp_buffer = (int16_t)(temperature * 100);

    return true;
}