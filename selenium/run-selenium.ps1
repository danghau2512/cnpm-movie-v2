param(
    [string]$BaseUrl = 'http://localhost:8080/CineBook_war_exploded',
    [string]$Test = '',
    [switch]$Headless
)
$ErrorActionPreference = 'Stop'
$projectRoot = Split-Path $PSScriptRoot -Parent
$wrapper = Join-Path $projectRoot 'mvnw.cmd'
if (!(Test-Path -LiteralPath $wrapper)) { throw "Maven Wrapper not found: $wrapper" }
Push-Location $projectRoot
try {
    $runArgs = @('-f', (Join-Path $PSScriptRoot 'pom.xml'), 'test', "-Dcinebook.baseUrl=$BaseUrl", "-Dcinebook.headless=$($Headless.IsPresent.ToString().ToLowerInvariant())")
    if ($Test) { $runArgs += "-Dtest=$Test" }
    & $wrapper @runArgs
    $runExitCode = $LASTEXITCODE
} finally { Pop-Location }
exit $runExitCode
