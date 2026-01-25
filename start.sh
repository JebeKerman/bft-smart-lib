#!/usr/bin/env bash

set -euo pipefail

./gradlew installDist

WORKDIR="./build/install/library/"
RUNDIR="$(pwd)"
LOGDIR="$RUNDIR/logs"
COMMANDS=(
  "./smartrun.sh bftsmart.demo.counter.CounterServer 0"
  "./smartrun.sh bftsmart.demo.counter.CounterServer 1"
  "./smartrun.sh bftsmart.demo.counter.CounterServer 2"
  "./smartrun.sh bftsmart.demo.counter.CounterServer 3"
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
