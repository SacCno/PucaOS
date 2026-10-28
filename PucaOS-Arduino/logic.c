#include "logic.h"
#include "plug.h"
#include "config.h"
#include "utils.h"
#include "temperature.h"
#include "platform.h"
#include "display.h"
#include "server.h"
#include "settings.h"

ControlMode control_mode;

Schedule *current_schedule;
uint16_t current_temp;
uint16_t current_time;
bool lamp_state = false;

bool isError = false;

static struct mg_mgr *mgr = NULL;

static void apply_logic();

bool get_lamp_status()
{
  return lamp_state;
}

void update_logic_mode(ControlMode mode)
{
  if (mode != CONTROL_MODE_AUTO && mode != CONTROL_MODE_MANUAL)
  {
    return;
  }

  control_mode = mode;
  update_settings_mode(mode); // Updates the control mode in the settings module as well
}

void plug_state_received(struct mg_http_message event_data)
{
  // Isolates the power attribute in the return call from the plug
  char *output = mg_json_get_str(event_data.body, "$.POWER");

  if (output == NULL)
  {
    printSerial("Plug state not received.");
    return;
  }

  if (strcmp(output, "ON") == 0)
  {
    lamp_state = true;
  }
  else if (strcmp(output, "OFF") == 0)
  {
    lamp_state = false;
  }
  else
  {
    printSerial("Invalid lamp response.");
    mg_free(output);
    return;
  }

  mg_free(output);
  apply_logic(); // Only after the plug state is received we apply the logic
}

void logic_init(Schedule *schedule, struct mg_mgr *manager)
{
  current_schedule = schedule;
  mgr = manager;
  update_logic_temp();
  update_logic_time();
  get_plug_state(mgr);
}

void update_logic_time()
{
  current_time = current_minute_of_day();
}

void update_logic_temp()
{
  uint16_t temp = sensor_temp();
  if (temp == 0)
  {
    isError = true;
    return;
  }
  else if (isError)
  {
    isError = false;
  }

  current_temp = temp;
  if (!isError)
  {
    const char temp_msg[8];
    int ref = snprintf(temp_msg, sizeof(temp_msg), "%.1f",
                       (float)temp / 100.00f);

    if (ref < 0)
    {
      return;
    }

    mqtt_publish("/mc/temperature", temp_msg, false); // We publish the temperature to MQTT for the android app to read
  }
}

void logic_update()
{
  get_plug_state(mgr);
}

static void apply_logic()
{
  updateDisplay((float)(current_temp / 100.0f)); // Temp is stored times 100 (30 = 3000) so it is divided by 100 when needs displaying

  if (control_mode != CONTROL_MODE_AUTO || isError) // When there is an error or the Arduino is in manual mode, we skip applying logic
  {
    return;
  }

  uint16_t tf_index = get_current_timeframe_index(*current_schedule, current_time); // Check which timeframe applies to right now
  Timeframe current_t = current_schedule->timeframes[tf_index];

  // If we're well above temp or schedule says turn off, and the lamp is already on
  if ((current_t.temperature + 200 < current_temp || !current_t.lamp_state) && lamp_state) // 2 degree range
  {
    set_plug(mgr, PLUG_IP, false); // Turn off
    printSerial("Turning off lamp.");
  }
  // If the lamp's off, temps below target, and lamp should be on
  else if (!lamp_state && current_t.temperature - 300 > current_temp && current_t.lamp_state) // 3 degree range
  {
    set_plug(mgr, PLUG_IP, true); // Turn on
    printSerial("Turning on lamp.");
  }

  printSerial("Target Temp: %f | Current Temp: %f | Lamp Status: %s",
              (float)current_schedule->timeframes[tf_index].temperature / 100.0f,
              (float)current_temp / 100.0f,
              lamp_state ? "On" : "Off");
}

ControlMode get_control_mode()
{
  return control_mode;
}