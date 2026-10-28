#include <SPI.h>
#include <Wire.h>
#include <Adafruit_GFX.h>
#include <Adafruit_SSD1306.h>
#include <stdio.h>

#include "display.h"

#define SCREEN_WIDTH 128
#define SCREEN_HEIGHT 64
#define OLED_RESET 4

Adafruit_SSD1306 display(SCREEN_WIDTH, SCREEN_HEIGHT, &Wire, OLED_RESET);

void displayInit()
{
  // Assigning Arduino data pins and clock speed.
  Wire.begin(A4, A5);
  Wire.setClock(100000);

  if (Serial) // To prevent memory leaks, we only print to the serial (USB) output if it is connected
  {
    Serial.println("Beginning Display...");

    // Try beginning display at various addresses and log accordingly
    if (!display.begin(SSD1306_SWITCHCAPVCC, 0x3C))
    {
      Serial.println(F("Screen not seen at 0x3C, trying 0x3D..."));

      if (!display.begin(SSD1306_SWITCHCAPVCC, 0x3D))
      {
        if (Serial)
        {
          Serial.println(F("OLED hardware communication failed. Check SDA/SCL joints."));
        }
        for (;;) //
      }
    }
  }

  display.clearDisplay();
  display.setTextColor(SSD1306_WHITE);
  display.setTextSize(1);
}

// We store the char arrays for reuse each frame
char headerText[] = "Temperature";
char errorLine1[] = "Sensor";
char errorLine2[] = "Error";
char tempText[8];

void updateDisplay(float temp)
{
  display.clearDisplay();
  display.setTextSize(1);

  int16_t hx, hy;
  uint16_t hw, hh;

  // To centre the text, we get the width of the text, take it away from
  // the screen width and halve it to get the start point of the centred text.
  display.getTextBounds(headerText, 0, 0, &hx, &hy, &hw, &hh);
  int headerX = (SCREEN_WIDTH - hw) / 2;

  display.setCursor(headerX, 0);

  display.println(headerText);

  if (temp != -127.00) // An erroneous reading will read -127 degrees
  {
    display.setTextSize(4);

    int n = snprintf(tempText, sizeof(tempText), "%.1fC",
                     temp);

    if (n < 0 || (size_t)n >= sizeof(tempText))
    {
      return;
    }

    int16_t tx, ty;
    uint16_t tw, th;

    // Again, we centre the text using the same method
    display.getTextBounds(tempText, 0, 0, &tx, &ty, &tw, &th);

    int tempX = (SCREEN_WIDTH - tw) / 2;
    int tempY = 24 + ((40 - th) / 2);

    display.setCursor(tempX, tempY);
    display.print(tempText);

    if (Serial)
    {
      Serial.print("Temperature: ");
      Serial.println(tempText);
    }
  }
  else
  {
    display.setTextSize(2);

    int16_t x1, y1, x2, y2;
    uint16_t w1, h1, w2, h2;

    // If the temperature reading is erroneous, we display "Sensor Error" instead.
    // As this takes up two lines we have to centre both lines seperately.
    display.getTextBounds(errorLine1, 0, 0, &x1, &y1, &w1, &h1);
    display.getTextBounds(errorLine2, 0, 0, &x2, &y2, &w2, &h2);

    int line1X = (SCREEN_WIDTH - w1) / 2;
    int line2X = (SCREEN_WIDTH - w2) / 2;

    int totalBlockHeight = h1 + 6 + h2;
    int startY = 24 + ((40 - totalBlockHeight) / 2);

    display.setCursor(line1X, startY);
    display.print(errorLine1);

    display.setCursor(line2X, startY + h1 + 6); // Y is set to the beginning plus the height of the above text plus a padding of 6px
    display.print(errorLine2);

    if (Serial)
    {
      Serial.println("Sensor Read Error");
    }
  }

  display.display();
}
