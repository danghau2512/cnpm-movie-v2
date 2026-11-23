param([int]$Port = 8080, [string]$JavaHome = 'C:\Program Files\Java\jdk-17', [switch]$SkipBuild)
$ErrorActionPreference = 'Stop'
$projectRoot = Split-Path $PSScriptRoot -Parent
$artifactPath = Join-Path $projectRoot 'out\artifacts\cnpm_movie_v2_main_war_exploded'
$javaExe = Join-Path $JavaHome 'bin\java.exe'
if (!(Test-Path -LiteralPath $javaExe)) { throw "JDK not found: $JavaHome" }
Push-Location $projectRoot
try {
    & .\gradlew.bat syncIntellijDeployment --console=plain
    if ($LASTEXITCODE -ne 0) { throw 'Build/deployment failed.' }
} finally { Pop-Location }
$listener = Get-NetTCPConnection -LocalPort $Port -State Listen -ErrorAction SilentlyContinue | Select-Object -First 1
if (!$listener) { Write-Output 'Artifact updated. Start the Tomcat configuration in IntelliJ.'; return }
$tomcatProcess = Get-CimInstance Win32_Process -Filter "ProcessId = $($listener.OwningProcess)"
if ($tomcatProcess.Name -ne 'java.exe' -or $tomcatProcess.CommandLine -notmatch 'org.apache.catalina.startup.Bootstrap') {
    throw "Port $Port is owned by another application. Artifact updated; reload Tomcat manually."
}
if ($tomcatProcess.CommandLine -notmatch '-Dcatalina.base="([^"]+)"') {
    throw 'This is not the expected IntelliJ Tomcat. Artifact updated; reload Tomcat manually.'
}
$tomcatBase = $Matches[1]
$descriptorFile = Join-Path $tomcatBase 'conf\Catalina\localhost\CineBook_war_exploded.xml'
if (!(Test-Path -LiteralPath $descriptorFile)) { throw 'CineBook deployment descriptor was not found.' }
[xml]$descriptor = Get-Content -LiteralPath $descriptorFile
if ([IO.Path]::GetFullPath($descriptor.Context.docBase) -ne [IO.Path]::GetFullPath($artifactPath)) {
    throw 'Tomcat belongs to another checkout; refusing to reload it.'
}
& $javaExe --add-modules jdk.attach (Join-Path $PSScriptRoot 'ReloadContext.java') ([string]$tomcatProcess.ProcessId) '/CineBook_war_exploded'
if ($LASTEXITCODE -ne 0) { throw 'Tomcat reload failed. Use Restart in IntelliJ.' }
Write-Output "Updated: http://localhost:$Port/CineBook_war_exploded/home"
