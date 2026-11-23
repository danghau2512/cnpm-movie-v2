param(
    [string]$TomcatHome = 'C:\Tomcat',
    [string]$JavaHome = 'C:\Program Files\Java\jdk-17',
    [int]$Port = 8080,
    [switch]$SkipBuild
)
$ErrorActionPreference = 'Stop'
$projectRoot = Split-Path $PSScriptRoot -Parent
$tomcatBase = Join-Path $projectRoot 'build\local-tomcat'
$javaExe = Join-Path $JavaHome 'bin\java.exe'
if (!(Test-Path -LiteralPath $javaExe)) { throw "JDK not found: $JavaHome" }
if (!(Test-Path -LiteralPath (Join-Path $TomcatHome 'bin\bootstrap.jar'))) {
    throw "Tomcat 10.1 not found: $TomcatHome"
}
if (Get-NetTCPConnection -State Listen -LocalPort $Port -ErrorAction SilentlyContinue) {
    throw "Port $Port is occupied. Stop that server first or use -Port 8081."
}
if (Get-NetTCPConnection -State Listen -LocalPort 8006 -ErrorAction SilentlyContinue) {
    throw 'Tomcat shutdown port 8006 is occupied. Stop the existing local instance first.'
}
if (!$SkipBuild) {
    Push-Location $projectRoot
    try {
        & .\gradlew.bat war --console=plain
        if ($LASTEXITCODE -ne 0) { throw 'WAR build failed.' }
    } finally { Pop-Location }
}
$warFile = Join-Path $projectRoot 'build\libs\ROOT.war'
if (!(Test-Path -LiteralPath $warFile)) { throw 'Build the WAR before starting Tomcat.' }
foreach ($folder in @('conf', 'logs', 'temp', 'work', 'webapps')) {
    New-Item -ItemType Directory -Force -Path (Join-Path $tomcatBase $folder) | Out-Null
}
Copy-Item -Path (Join-Path $TomcatHome 'conf\*') -Destination (Join-Path $tomcatBase 'conf') -Recurse -Force
$serverFile = Join-Path $tomcatBase 'conf\server.xml'
[xml]$serverConfig = Get-Content -LiteralPath $serverFile
$serverConfig.Server.SetAttribute('port', '8006')
$serverConfig.Server.Service.Connector | Where-Object { $_.protocol -eq 'HTTP/1.1' } | ForEach-Object {
    $_.SetAttribute('port', [string]$Port)
}
$serverConfig.Save($serverFile)
Copy-Item -LiteralPath $warFile -Destination (Join-Path $tomcatBase 'webapps\CineBook_war_exploded.war') -Force
$javaArgs = @(
    "`"-Dcatalina.home=$TomcatHome`"", "`"-Dcatalina.base=$tomcatBase`"",
    "`"-Djava.io.tmpdir=$tomcatBase\temp`"", '-Dfile.encoding=UTF-8',
    "`"-Djava.util.logging.config.file=$tomcatBase\conf\logging.properties`"",
    '-Djava.util.logging.manager=org.apache.juli.ClassLoaderLogManager',
    '-cp', "`"$TomcatHome\bin\bootstrap.jar;$TomcatHome\bin\tomcat-juli.jar`"",
    'org.apache.catalina.startup.Bootstrap', 'start'
)
$process = Start-Process -FilePath $javaExe -ArgumentList $javaArgs -WindowStyle Hidden -PassThru `
    -RedirectStandardOutput (Join-Path $tomcatBase 'console.log') `
    -RedirectStandardError (Join-Path $tomcatBase 'console-error.log')
$process.Id | Set-Content -LiteralPath (Join-Path $tomcatBase 'tomcat.pid')
$url = "http://localhost:$Port/CineBook_war_exploded/home"
for ($attempt = 0; $attempt -lt 45; $attempt++) {
    Start-Sleep -Seconds 1
    $process.Refresh()
    if ($process.HasExited) { throw "Tomcat exited. See $tomcatBase\console-error.log" }
    try {
        $response = Invoke-WebRequest -Uri $url -UseBasicParsing -TimeoutSec 2
        if ($response.StatusCode -eq 200) { Write-Output "CineBook ready: $url (PID $($process.Id))"; return }
    } catch { }
}
throw "Tomcat did not become ready. See $tomcatBase\logs and console-error.log."
