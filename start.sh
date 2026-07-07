#!/usr/bin/env bash

set -euo pipefail

./gradlew installDist

CLASS="bftsmart.demo.counter.CounterServer"
ARGS=""
# CLASS="bftsmart.demo.microbenchmarks.ThroughputLatencyServer"
# ARGS="10 100 1000 null default rw"

FRAMEWORK="kryo"

WORKDIR="./build/install/library/"
RUNDIR="$(pwd)"
LOGDIR="$RUNDIR/logs"

COMMANDS=(
  "./smartrun.sh -Dserialization.framework=$FRAMEWORK $CLASS 0 $ARGS"
  "./smartrun.sh -Dserialization.framework=$FRAMEWORK $CLASS 1 $ARGS"
  "./smartrun.sh -Dserialization.framework=$FRAMEWORK $CLASS 2 $ARGS"
  "./smartrun.sh -Dserialization.framework=$FRAMEWORK $CLASS 3 $ARGS"
)

mkdir -p "$LOGDIR"
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
  LOGFILE="$LOGDIR/p$((i)).log"
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
