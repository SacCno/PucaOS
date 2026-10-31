#include "heartbeat.h"

static void heartbeat_http_handler(struct mg_connection *connection, int event, void *event_data)
{
    switch (event)
  {
  case MG_EV_CONNECT:
  {
    struct plug_request *request = (struct plug_request *)connection->fn_data;

    mg_printf(connection,
              "GET %s HTTP/1.1\r\n"
              "Host: %s\r\n"
              "Connection: close\r\n\r\n",
              request->path,
              request->host);
    break;
  }
  case MG_EV_HTTP_MSG:
  {
    struct mg_http_message *message =
        (struct mg_http_message *)event_data;
    
    printSerial("Plug HTTP response: %.*s",
               (int)message->body.len,
               message->body.buf);

    connection->is_draining = 1;
    break;
  }
  case MG_EV_ERROR:
    printSerial("Plug HTTP error: %s",
                event_data != NULL
                    ? (char *)event_data
                    : "unknown error");
    break;
  case MG_EV_CLOSE:
    free(connection->fn_data);
    printSerial("Plug HTTP connection closed");
    break;
  }
}

void emit_heartbeat(struct mg_mgr *manager)
{

}