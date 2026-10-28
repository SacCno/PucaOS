#ifndef DISPLAY_H
#define DISPLAY_H

#ifdef __cplusplus
extern "C"
{
#endif

  /**
   * Initialise the display module.
   */
  void displayInit();

  /**
   * Updates the screen's display with the given temperature.
   *
   * @param temp The temperature to display in degrees celcius.
   *
   * @note The temperature is checked for errors within the function.
   */
  void updateDisplay(float temp);

#ifdef __cplusplus
}
#endif

#endif