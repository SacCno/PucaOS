#include <OneWire.h>
#include <DallasTemperature.h>

#include "platform.h"
#include "temperature.h"

const int SENSOR_PIN = 5;

OneWire oneWire(SENSOR_PIN);

DallasTemperature sensors(&oneWire);


static bool conversion_pending = false;

static void start_conversion()
{
    sensors.requestTemperatures();
    conversion_pending = true;
}

void init_temp()
{
    sensors.begin();
    sensors.setWaitForConversion(false);
    start_conversion();
}

TempStatus sensor_temp(int16_t *temp_buffer)
{
    if(temp_buffer == nullptr) return TEMP_ERROR;
    if(!conversion_pending) start_conversion();
    if(!sensors.isConversionComplete()) return TEMP_PENDING;

    float temperature = sensors.getTempCByIndex(0);
    start_conversion(); //We start getting the temperature reading for the next cycle ready

    if (temperature == DEVICE_DISCONNECTED_C) return TEMP_ERROR;

    *temp_buffer = (int16_t)(temperature * 100);
    return TEMP_READY;
}