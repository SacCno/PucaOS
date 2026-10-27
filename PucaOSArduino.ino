#include "mongoose.h"

extern "C" void pucaos_setup(void);
extern "C" void pucaos_loop(void);

void setup()
{
    delay(500);
    Serial.begin(115200);
    const unsigned long serial_start = millis();
    while (!Serial && millis() - serial_start < 5000UL)
    {
        delay(10);
    }
    if (Serial)
    {
        Serial.println("Setup start.");
    }

    pucaos_setup();
}

void loop()
{
    pucaos_loop();
}
