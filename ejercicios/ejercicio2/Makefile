# Shortcuts for compiling and running this project.
# Run `make`, `make run` or `make clean`.

JAVAC = javac
JAVA  = java
OUT   = out
CP    = out:lib/*
SRC   = $(shell find src -type f -name '*.java' ! -name '._*')

.PHONY: all build run clean

all: build

build: $(SRC)
	mkdir -p $(OUT)
	$(JAVAC) -d $(OUT) -cp "$(CP)" $(SRC)

run: build
	$(JAVA) -cp "$(CP)" Main

clean:
	rm -rf $(OUT)
