CC :=gcc
CFLAGS := -c -D_REENTRANT -pthread
#-Wall
LDFLAGS := -shared

NAMEJNA := icmp4jJNA
NAMEJNI := icmp4jJNI

OS := $(shell uname)
ifeq ($(OS),Darwin)
    EXT := dylib
    JNI := -I$(JAVA_HOME)/include  -I$(JAVA_HOME)/include/darwin
else
    EXT := so
    JNI := -I$(JAVA_HOME)/../include -I$(JAVA_HOME)/../include/linux
endif

all: libJNA libJNI test

libJNA: lib$(NAMEJNA).$(EXT)

libJNI: lib$(NAMEJNI).$(EXT)

lib$(NAMEJNA).$(EXT): icmp4j.o
	$(CC) $(LDFLAGS) -o lib$(NAMEJNA).$(EXT) $^ -o $@

lib$(NAMEJNI).$(EXT): icmp4j.o icmp4jJNI.o
	$(CC) $(LDFLAGS) -o lib$(NAMEJNI).$(EXT) $^ -o $@

icmp4j.o: icmp4j.c
	$(CC) $(CFLAGS) -fpic icmp4j.c

icmp4jJNI.o: icmp4jJNI.c
	$(CC) $(JNI) $(CFLAGS) -fpic icmp4jJNI.c
    
test: main.o
	$(CC) main.o -o test -L. -licmp4jJNA

main.o: main.c
	$(CC) $(CFLAGS) main.c

clean:
	rm *.o test lib$(NAMEJNA).$(EXT) lib$(NAMEJNI).$(EXT)
