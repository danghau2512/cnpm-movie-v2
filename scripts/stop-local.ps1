$ErrorActionPreference = 'Stop'
$projectRoot = Split-Path $PSScriptRoot -Parent
$tomcatBase = Join-Path $projectRoot 'build\local-tomcat'
$pidFile = Join-Path $tomcatBase 'tomcat.pid'
if (!(Test-Path -LiteralPath $pidFile)) { Write-Output 'No local Tomcat PID file.'; return }
$tomcatProcessId = [int](Get-Content -LiteralPath $pidFile)
$process = Get-CimInstance Win32_Process -Filter "ProcessId = $tomcatProcessId"
if ($process) {
    if ($process.Name -ne 'java.exe' -or !$process.CommandLine.Contains("-Dcatalina.base=$tomcatBase")) {
        throw 'PID belongs to another process; refusing to stop it.'
    }
    Stop-Process -Id $tomcatProcessId
}
Remove-Item -LiteralPath $pidFile
Write-Output 'Local CineBook Tomcat stopped.'
