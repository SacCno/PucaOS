#ifndef PLUG_H
#define PLUG_H

#include <stdbool.h>
#include <stdio.h>
#include <stdlib.h>

#include "mongoose.h"
#include "config.h"
#include "logic.h"
#include "platform.h"

struct plug_request
{
  char host[64];
  char path[128];
  bool query_state;
};

typedef void (*plug_state_callback)(
    bool is_on,
    void *arg);

struct mg_mgr;

bool get_plug_state(struct mg_mgr *manager);
bool set_plug(struct mg_mgr *manager, char *ip, bool turn_on);

#endif