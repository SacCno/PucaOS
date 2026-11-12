#ifndef SETTINGS_H
#define SETTINGS_H

#include "storage.h"
#include "logic.h"

#ifdef __cplusplus
extern "C"
{
#endif

struct settings_type
{
  ControlMode mode;
};

bool settings_init();
bool update_settings_mode(ControlMode cm);
bool verify_settings(struct settings_type * s);

#ifdef __cplusplus
}
#endif

#endif