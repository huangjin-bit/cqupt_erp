#!/usr/bin/env sh
set -eu

SCRIPT_DIR=$(CDPATH= cd -- "$(dirname -- "$0")" && pwd)
PROJECT_ROOT=$(CDPATH= cd -- "$SCRIPT_DIR/.." && pwd)
cd "$PROJECT_ROOT"

if [ -x "$PROJECT_ROOT/.venv/Scripts/python.exe" ]; then
  PYTHON_EXE="$PROJECT_ROOT/.venv/Scripts/python.exe"
elif [ -x "$PROJECT_ROOT/.venv/bin/python" ]; then
  PYTHON_EXE="$PROJECT_ROOT/.venv/bin/python"
else
  PYTHON_EXE="python"
fi

printf '[INFO] Project root: %s\n' "$PROJECT_ROOT"
printf '[INFO] Python: %s\n' "$PYTHON_EXE"

exec "$PYTHON_EXE" "scripts/erp_competition_runner.py" "$@"
