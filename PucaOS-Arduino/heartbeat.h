#ifndef HEARTBEAT_H
#define HEARTBEAT_H

#include "mongoose.h"
#include "plug.h"

  /**
   * Emits a heartbeat to Tasmota Heartbeat event
   */
void emit_heartbeat(struct mg_mgr *manager);

#endif