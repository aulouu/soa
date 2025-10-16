#!/usr/bin/env bash
set -euo pipefail

# Usage:
#   ./kill-ports.sh            # kills defaults: 8080 2468 8449 8448 5173
#   ./kill-ports.sh 3000 8080  # kills specific ports

DEFAULT_PORTS=(8080 2468 8449 8448 5173)

if [ "$#" -gt 0 ]; then
  PORTS=("$@")
else
  PORTS=("${DEFAULT_PORTS[@]}")
fi

any_killed=false

for port in "${PORTS[@]}"; do
  # Find PIDs listening on the port (macOS & Linux compatible with lsof)
  if command -v lsof >/dev/null 2>&1; then
    PIDS=$(lsof -tiTCP:${port} -sTCP:LISTEN || true)
  else
    echo "lsof not found; please install lsof" >&2
    exit 1
  fi

  if [ -z "$PIDS" ]; then
    echo "No listeners on :${port}"
    continue
  fi

  echo "Killing processes on :${port} → $PIDS"
  # shellcheck disable=SC2086
  kill $PIDS 2>/dev/null || true
  sleep 0.5
  # shellcheck disable=SC2086
  kill -9 $PIDS 2>/dev/null || true
  any_killed=true
done

if [ "$any_killed" = false ]; then
  echo "Nothing to kill"
fi


