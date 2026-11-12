#include <LittleFS.h>
#include "storage.h"
#include "settings.h"

bool storage_write(const char *path,
                   const void *data,
                   size_t size)
{
  if (path == nullptr || data == nullptr)
  {
    return false;
  }
  
  
  // First write to temp file
  
  File file = LittleFS.open("/temp.bin", "w");
  if (!file)
  {
    return false;
  }

  file.write(static_cast<const uint8_t *>(data), size);

  file.flush();
  file.close();


  //Check temp file validity for schedule and settings files

  switch (size)
  {
  case sizeof(Schedule): //Schedule file
    {
      Schedule tempschedule = {0};
      if(!storage_read("/temp.bin", &tempschedule, sizeof(tempschedule))) return false;
      if(!validate_schedule(&tempschedule)) return false;
      break;
    }
  case sizeof(settings_type): //Settings file
    {
      struct settings_type tempsettings = {CONTROL_MODE_AUTO};
      if(!storage_read("/temp.bin", &tempsettings, sizeof(tempsettings))) return false;
      if(!verify_settings(&tempsettings)) return false;
      break;
    }
  default:
    break;
  }
  
  // Write to desired path after confirming temp file
  
  file = LittleFS.open(path, "w"); 
  if (!file)
  {
    return false;
  }

  size_t written = file.write(static_cast<const uint8_t *>(data), size);

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
  if (!file) return false;

  if (file.size() != size)
  {
    file.close();
    return false;
  }

  size_t bytesRead = file.read(static_cast<uint8_t *>(data), size);
  file.close();

  return bytesRead == size;
}