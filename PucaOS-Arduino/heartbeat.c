#include "heartbeat.h"

static void heartbeat_http_handler(struct mg_connection *connection, int event, void *event_data)
{
  switch (event)
  {
  case MG_EV_CONNECT:
    printSerial("Connected, sending heartbeat");
    mg_printf(connection,
              "GET /cm?cmnd=Event%%20Heartbeat HTTP/1.1\r\n"
              "Host: %s\r\n"
              "Connection: close\r\n\r\n",
              PLUG_IP);
    break;
  
  default:
    break;
  }
}

void emit_heartbeat(struct mg_mgr *manager)
{
  if(manager == NULL)
  {
    return;
  }

  char url[256];              // We format the event URL
  int n = snprintf(url,
                  sizeof(url),
                  "http://%s",
                  PLUG_IP);

  if(n < 0 || (size_t)n > sizeof(url))
  {
    return; // Return if event URL doesn't format properly
  }

  // Connect via HTTP with no callback function or message.
  struct mg_connection *connection = mg_http_connect(manager,
                                                    url,
                                                    heartbeat_http_handler,
                                                    NULL);

  return;
}