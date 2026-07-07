#!/usr/bin/env bash

set -euo pipefail

./gradlew installDist

FRAMEWORK="$1"
RUN_ID="$2"

case "$FRAMEWORK" in
  java|proto|kryo)
    ;;
  *)
    echo "Error: Invalid argument"
    echo "Usage: $0 <java|proto|kryo>"
    exit 1
    ;;
esac

CLASS="bftsmart.demo.microbenchmarks.ThroughputLatencyServer"

#MEASUREMENT_INTERVAL=10000
MEASUREMENT_INTERVAL=1000
REPLY_SIZE=0
STATE_SIZE=64 
CONTEXT=false # true|false
MODE=default # nosig|default|ecdsa
OPERATION=rwd # rwd|rw

ARGS="$MEASUREMENT_INTERVAL $REPLY_SIZE $STATE_SIZE $CONTEXT $MODE"
# ARGS="$MEASUREMENT_INTERVAL $REPLY_SIZE $STATE_SIZE $CONTEXT $MODE $OPERATION"

WORKDIR="./build/install/library/"
RUNDIR="$(pwd)"
LOGDIR="$RUNDIR/logs/$RUN_ID"

COMMANDS=(
  "./smartrun.sh -Xms512m -Xmx4g -Dmetrics.file=$LOGDIR/$FRAMEWORK/s01.json -Dserialization.measure.bytes=true -Dserialization.framework=$FRAMEWORK $CLASS 0 $ARGS"
  "./smartrun.sh -Xms512m -Xmx4g -Dmetrics.file=$LOGDIR/$FRAMEWORK/s02.json -Dserialization.measure.bytes=true -Dserialization.framework=$FRAMEWORK $CLASS 1 $ARGS"
  "./smartrun.sh -Xms512m -Xmx4g -Dmetrics.file=$LOGDIR/$FRAMEWORK/s03.json -Dserialization.measure.bytes=true -Dserialization.framework=$FRAMEWORK $CLASS 2 $ARGS"
  "./smartrun.sh -Xms512m -Xmx4g -Dmetrics.file=$LOGDIR/$FRAMEWORK/s04.json -Dserialization.measure.bytes=true -Dserialization.framework=$FRAMEWORK $CLASS 3 $ARGS"
)

mkdir -p "$LOGDIR/$FRAMEWORK"
PIDS=()

cleanup() {
  echo "Shutting down all processes..."
  for pid in "${PIDS[@]}"; do
    if kill -0 "$pid" 2>/dev/null; then
      kill "$pid"
    fi
  done
  wait
  echo "All processes stopped."
  exit 0
}

trap cleanup INT TERM

cd "$WORKDIR"
for i in "${!COMMANDS[@]}"; do
  LOGFILE="$LOGDIR/$FRAMEWORK/p$((i)).log"
  echo "Starting ${COMMANDS[$i]} -> $LOGFILE"

  ${COMMANDS[$i]} >"$LOGFILE" 2>&1 &
  PIDS+=($!)
done

cd - >/dev/null

echo "All processes started."
echo "Press ENTER or Ctrl+C to stop."

# ---- wait for user input ----
read -r
cleanup
