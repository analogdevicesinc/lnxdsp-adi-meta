/*  Copyright (C) Analog Devices Inc. 2020.
**  All Rights Reserved.
*/

#ifndef _FASTBOOT_H
#define _FASTBOOT_H
#define SERVER_PORT 7788
#define SERVER_IP "10.37.33.113"
/* TRIGGER_WORD is the string sent from client to host to request wakeup */
#define TRIGGER_WORD "gong"
/* SLEEP_TIME amount of time in uSeconds to sleep before attempting to connect to host again. */
#define SLEEP_TIME 10000
#endif