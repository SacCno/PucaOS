#ifndef PLATFORM_H
#define PLATFORM_H

#include <stdbool.h>
#include <stddef.h>

#ifdef __cplusplus
extern "C"
{
#endif

  /**
   * Initiates the hardware, including WiFi and time sync.
   */
  bool platform_init(void);
  /**
   * Prints out to the hardware's serial output (if connected).
   *
   * @param fmt the args to print.
   */
  void printSerial(const char *fmt, ...);

#ifdef __cplusplus
}
#endif

#endif