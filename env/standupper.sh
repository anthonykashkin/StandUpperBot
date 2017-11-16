#!/bin/bash

BOT_DIR=`readlink -f "$(dirname "$0")"`

STANDUPPER=`find ${BOT_DIR} -name '*.jar'`

exec java -jar $STANDUPPER $@