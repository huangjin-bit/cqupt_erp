param(
    [string]$BaseUrl = "http://39.98.115.237/",
    [string]$Teacher = "wangyong",
    [string]$TeacherPassword = "123456",
    [string]$RunId = ("T" + (Get-Date -Format "yyyyMMddHHmmss")),
    [string]$GroupName = "",
    [int]$MaxCycles = 80,
    [string]$BrowserChannel = "msedge",
    [ValidateSet("expanded", "basic-p1")]
    [string]$Strategy = "expanded",
    [switch]$Headed,
    [switch]$CheckEnv
)

Set-StrictMode -Version Latest
$ErrorActionPreference = "Stop"

$ProjectRoot = Split-Path -Parent $PSScriptRoot
Set-Location $ProjectRoot

$VenvPython = Join-Path $ProjectRoot ".venv\Scripts\python.exe"
if (Test-Path $VenvPython) {
    $Python = $VenvPython
} else {
    $Python = "python"
}

$RunnerArgs = @(
    "scripts\erp_competition_runner.py",
    "--base-url", $BaseUrl,
    "--teacher", $Teacher,
    "--teacher-password", $TeacherPassword,
    "--run-id", $RunId,
    "--browser-channel", $BrowserChannel,
    "--strategy", $Strategy,
    "--max-cycles", [string]$MaxCycles
)

if ($GroupName.Trim().Length -gt 0) {
    $RunnerArgs += @("--group-name", $GroupName)
}

if (-not $Headed) {
    $RunnerArgs += "--headless"
}

if ($CheckEnv) {
    $RunnerArgs += "--check-env"
}

& $Python @RunnerArgs
exit $LASTEXITCODE
