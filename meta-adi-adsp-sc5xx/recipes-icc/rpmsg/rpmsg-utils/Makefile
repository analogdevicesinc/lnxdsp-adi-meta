all: rpmsg-bind-chardev rpmsg-xmit

rpmsg-bind-chardev: rpmsg-bind-chardev.o
	$(CC) $(LDFLAGS) -o $@ rpmsg-bind-chardev.o $(LDLIBS)

rpmsg-xmit: rpmsg-xmit.o
	$(CC) $(LDFLAGS) -o $@ rpmsg-xmit.o $(LDLIBS)

clean:
	rm -rf rpmsg-bind-chardev rpmsg-xmit *.o

%.o: %.c
	$(CC) -c $(CFLAGS) -o $@ $<

