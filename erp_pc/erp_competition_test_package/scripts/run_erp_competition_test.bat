@echo off
setlocal

rem Run from this package root no matter where the command is launched.
set "SCRIPT_DIR=%~dp0"
for %%I in ("%SCRIPT_DIR%..") do set "PROJECT_ROOT=%%~fI"
cd /d "%PROJECT_ROOT%"

set "PYTHON_EXE=%PROJECT_ROOT%\.venv\Scripts\python.exe"
if not exist "%PYTHON_EXE%" set "PYTHON_EXE=python"

echo [INFO] Project root: %PROJECT_ROOT%
echo [INFO] Python: %PYTHON_EXE%

"%PYTHON_EXE%" "scripts\erp_competition_runner.py" %*
exit /b %ERRORLEVEL%
