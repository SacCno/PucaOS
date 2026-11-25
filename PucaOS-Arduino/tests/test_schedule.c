#include <stdarg.h>
#include "unity.h"
#include "schedule.h"
#include "platform.h"
#include "storage.h"
#include "mongoose.h"

void setUp(void) {}
void tearDown(void) {}

void test_temperature_changes_at_timeframe_start(void)
{
    Schedule schedule = {0};
    schedule.count = 2;

    schedule.timeframes[0] = (Timeframe){
        .temperature = 3000,
        .minute_of_day = 360,
        .lamp_state = 1};
    schedule.timeframes[1] = (Timeframe){
        .temperature = 2500,
        .minute_of_day = 1260,
        .lamp_state = 0};

    TEST_ASSERT_EQUAL_UINT16(
        2500,
        get_temperature_from_time(&schedule, 359));

    TEST_ASSERT_EQUAL_UINT16(
        3000,
        get_temperature_from_time(&schedule, 360));
}

/* Stubs for dependencies schedule.c needs to link.
   This test does not check storage or CRC behavior. */
void printSerial(const char *fmt, ...)
{
    (void)fmt;
}

bool storage_write(const char *path, const void *data, size_t size)
{
    (void)path;
    (void)data;
    (void)size;
    return false;
}

bool storage_read(const char *path, void *data, size_t size)
{
    (void)path;
    (void)data;
    (void)size;
    return false;
}

uint32_t mg_crc32(uint32_t crc, const char *buf, size_t len)
{
    (void)crc;
    (void)buf;
    (void)len;
    return 0;
}

int main(void)
{
    UNITY_BEGIN();
    RUN_TEST(test_temperature_changes_at_timeframe_start);
    return UNITY_END();
}