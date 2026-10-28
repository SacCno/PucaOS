#ifndef LOGIC_H
#define LOGIC_H

#include <stdint.h>
#include "schedule.h"
#include "mongoose.h"

typedef enum
{
  CONTROL_MODE_AUTO,
  CONTROL_MODE_MANUAL
} ControlMode;

/**
 * Initialises the parameters needed to apply logic.
 *
 * @param schedule Pointer to the schedule we are applying.
 * @param manager Pointer to the manager object controlling connectivity.
 */
void logic_init(Schedule *schedule, struct mg_mgr *manager);

/**
 * Checks parameters and error messages and applies logic if conditions are met.
 *
 * @note Will automatically skip if control mode is set to manual.
 */
void logic_update();

/**
 * Callback function from plug call, applies logic after confirming plug power status.
 *
 * @param event_data The received message from the plug, containing power information.
 */
void plug_state_received(struct mg_http_message event_data);

/**
 * Reads temperature from sensor. Checks for erroneous readings. Sends sensor data to MQTT broker.
 */
void update_logic_temp();

/**
 * Reads the current minute since midnight and saves it to a local variable for logic initialisation.
 */
void update_logic_time();

/**
 * Updates the local variable for the current control mode.
 *
 * @param mode The new control mode.
 *
 * @note Also updates the control mode variable in the settings module.
 */
void update_logic_mode(ControlMode mode);

/**
 * Get function for retrieving the current control mode.
 *
 * @returns the current control mode.
 */
ControlMode get_control_mode();

/**
 * Get function for the current lamp state.
 *
 * @return the lamp state as a boolean.
 *
 * @note Lamp state is updated every logic tick, not realtime.
 */
bool get_lamp_status();

#endif