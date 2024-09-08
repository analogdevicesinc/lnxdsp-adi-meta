#!/bin/sh

case "$1" in
 start)
	echo "Starting watchdog..."
	/sbin/watchdog -t 1 /dev/watchdog
	;;
  stop)
	echo "Stopping watchdog..."    
  	killall -9 watchdog
	;;
  restart|reload)
	echo "Restarting watchdog..."  
  	killall -9 watchdog
	/sbin/watchdog -t 1 /dev/watchdog
	;;
  *)
	echo "Usage: $0 {start|stop|restart}"
	exit 1
esac

exit $?
