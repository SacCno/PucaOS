#include <LittleFS.h>
#include "storage.h"

bool storage_write(const char *path,
                   const void *data,
                   size_t size)
{
  if (path == nullptr || data == nullptr)
  {
    return false;
  }
  
  
  // First write to temp file
  
  File file = LittleFS.open("/tempschedule.bin", "w");
  if (!file)
  {
    return false;
  }

  size_t written = file.write(
      static_cast<const uint8_t *>(data), size);

  file.flush();
  file.close();


  //Check temp file validity

  Schedule tempschedule = {0}

  if(!storage_read("/tempschedule.bin", &tempschedule, sizeof(tempschedule)))
  {
    return false;
  }

  if(!validate_schedule(&tempschedule))
  {
    return false;
  }
  
  // Write to desired path after confirming temp file
  
  File file = LittleFS.open(path, "w"); 
  if (!file)
  {
    return false;
  }

  size_t written = file.write(
      static_cast<const uint8_t *>(data), size);

  file.flush();
  file.close();

  return written == size;
}

bool storage_read(const char *path, void *data, size_t size)
{
  if (path == nullptr || data == nullptr)
  {
    return false;
  }

  File file = LittleFS.open(path, "r");
  if (!file)
  {
    return false;
  }

  if (file.size() != size)
  {
    file.close();
    return false;
  }

  size_t bytesRead = file.read(static_cast<uint8_t *>(data), size);
  file.close();

  return bytesRead == size;
}