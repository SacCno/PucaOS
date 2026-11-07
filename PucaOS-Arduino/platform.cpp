#include <Arduino.h>
#include <LittleFS.h>
#include <WiFi.h>
#include <stdarg.h>
#include <time.h>
#include <stdint.h>

#include "config.h"

extern "C" void printSerial(const char *fmt, ...)
{
    if (fmt == NULL)
    {
        return;
    }
    if (!Serial)
    {
        return;
    }

    char buffer[256];
    va_list args;
    va_start(args, fmt);
    vsnprintf(buffer, sizeof(buffer), fmt, args);
    va_end(args);

    Serial.println(buffer);
}


extern "C" bool platform_init(void)
{
    printSerial("Entered platform init");

    printSerial("Mounting LittleFS (may format on first boot)...");
    if (!LittleFS.begin(false)) // false means a failed boot doesn't reformat the whole device
    {
        printSerial("LittleFS mount failed\nWould you like to (re)format the device? (Y/N)\nWARNING THIS MAY WIPE ALL DATA");
        
        const unsigned long start = millis();
        int input = -1;
        while(millis() - start < 30000UL && input < 0)
        {
            if(Serial.available > 0)
            {
                input = Serial.read();
            }
            delay(10);
        }

        if (input != 'y' && input != 'Y')
        {
            printSerial("Formatting declined or timed out");
            return false;
        }

        printSerial("Formatting LittleFS...");
        if(!LittleFS.begin(true))
        {
            printSerial("LittleFS still not mounting.");
            return false;
        }
    }

    printSerial("LittleFS mounted");

    File root = LittleFS.open("/");

    if (!root)
    {
        printSerial("Could not open LittleFS root");
    }
    else if (!root.isDirectory())
    {
        printSerial("LittleFS root is not a directory");
    }
    else
    {
        //Loops through and lists all files saved to flash storage
        bool found_file = false;
        File file = root.openNextFile();

        while (file)
        {
            found_file = true;
            printSerial("Found: %s (%u bytes)",
                        file.name(),
                        (unsigned)file.size());

            file = root.openNextFile();
        }

        if (!found_file)
        {
            printSerial("LittleFS is empty");
        }
    }

    WiFi.mode(WIFI_STA);
    WiFi.setAutoReconnect(true);
    WiFi.disconnect();
    delay(100);
    printSerial("Connecting to WiFi SSID '%s'", WIFI_SSID);
    WiFi.begin(WIFI_SSID, WIFI_PASS);

    delay(5000);

    const unsigned long start = millis();

    while (WiFi.status() != WL_CONNECTED && millis() - start < 20000UL)
    {
        delay(250);
    }

    if (WiFi.status() != WL_CONNECTED) //If WiFi doesn't connect we list the found networks
    {
        printSerial("WiFi connection failed, status=%d", (int)WiFi.status());

        int network_count = WiFi.scanNetworks();
        printSerial("WiFi scan found %d networks", network_count);
        for (int network_index = 0; network_index < network_count; ++network_index)
        {
            if (WiFi.SSID(network_index) == WIFI_SSID)
            {
                printSerial("Target SSID visible, RSSI=%d dBm, encryption=%d",
                            WiFi.RSSI(network_index),
                            (int)WiFi.encryptionType(network_index));
            }
        }

        return false;
    }

    // We start and sync the time on the board
    configTzTime("GMT0IST,M3.5.0/1,M10.5.0/2", "pool.ntp.org", "time.nist.gov"); 

    struct tm timeinfo = {};
    if(!getLocalTime(&timeinfo, 15000)) {
        printSerial("Time sync failed");
        return false;
    }

    char timeText[32];
    strftime(timeText, sizeof(timeText), "%Y-%m-%d %H:%M:%S", &timeinfo);
    printSerial("Time synchronised: %s", timeText);

    printSerial("Setup Complete");

    return true;
}