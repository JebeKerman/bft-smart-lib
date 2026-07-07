#!/usr/bin/env bash

set -euo pipefail

./gradlew installDist

FRAMEWORK="$1"

case "$FRAMEWORK" in
  java|proto|kryo)
    ;;
  *)
    echo "Error: Invalid argument"
    echo "Usage: $0 <java|proto|kryo>"
    exit 1
    ;;
esac

CLASS="bftsmart.demo.microbenchmarks.AsyncLatencyClient"

INITIAL_CLIENT_ID=1000
NUM_CLIENTS=10
NUM_OPERATIONS=100000
REQUEST_SIZE=1024
MAX_INTERVAL=10
READ_ONLY=false
VERBOSE=false
MODE=default # nosig|default|ecdsa

ARGS="$INITIAL_CLIENT_ID $NUM_CLIENTS $NUM_OPERATIONS $REQUEST_SIZE $MAX_INTERVAL $READ_ONLY $VERBOSE $MODE"

WORKDIR="./build/install/library/"
RUNDIR="$(pwd)"
LOGDIR="$RUNDIR/logs"

mkdir -p "$LOGDIR/$FRAMEWORK"

cd "$WORKDIR"

./smartrun.sh -Dserialization.framework=$FRAMEWORK $CLASS $ARGS >"$LOGDIR/$FRAMEWORK/client.log" 2>&1
