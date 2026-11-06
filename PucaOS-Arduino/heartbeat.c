#include "heartbeat.h"

void emit_heartbeat(struct mg_mgr *manager)
{
  if(manager == NULL)
  {
    return;
  }

  char url[256];              // We format the event URL
  int n = snprintf(url,
                  sizeof(url),
                  "http:/%s/cm?cmnd=Event%%20Heartbeat",
                  PLUG_IP);

  if(n < 0 || (size_t)n > sizeof(url))
  {
    return; // Return if event URL doesn't format properly
  }

  // Connect via HTTP with no callback function or message.
  struct mg_connection *connection = mg_http_connect(manager,
                                                    url,
                                                    NULL,
                                                    NULL); 
}