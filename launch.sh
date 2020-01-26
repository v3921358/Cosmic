#!/bin/bash

java -Xmx2048m -cp "dist/*:cores/*:lib/*" -Dwzpath="wz/" net.server.Server
