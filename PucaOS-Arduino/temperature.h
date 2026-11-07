#ifndef TEMPERATURE_H
#define TEMPERATURE_H

#include <stdint.h>

#ifdef __cplusplus
extern "C" {
#endif

  /**
   * Begins the sensor object.
   */
void init_temp(void);
  /**
   * Returns the temperature reading from the sensor.
   *
   * @param temp_buffer The address of the int16_t variable to store the retrieved temperature.
   *
   * @note The temperature is checked for errors within the function, setting temp_buffer to -127 if an error is encountered.
   * 
   * @note Successful temperatures are formatted in celcius multiplied by 100 (i.e. 24.3C is returned as 2430)
   * 
   * @return a bool stating the success or failure to retrieve the current temperature.
   */
bool sensor_temp(int16_t *temp_buffer);

#ifdef __cplusplus
}
#endif

#endif