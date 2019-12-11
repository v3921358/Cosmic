#!/bin/bash

java -Xmx2048m -cp "dist/*:cores/*" -Dwzpath="wz/" net.server.Server
