param(
    [string]$BaseUrl = '',
    [string]$Test = '',
    [string]$ConfigFile = (Join-Path $PSScriptRoot 'database/demo.properties'),
    [ValidateRange(0,10000)]
    [int]$PauseMs = 2500,
    [switch]$Headless
)
$ErrorActionPreference = 'Stop'
$projectRoot = Split-Path $PSScriptRoot -Parent
$wrapper = Join-Path $projectRoot 'mvnw.cmd'
if (!(Test-Path -LiteralPath $wrapper)) { throw "Maven Wrapper not found: $wrapper" }
$resolvedConfigFile = (Resolve-Path -LiteralPath $ConfigFile).Path
Push-Location $projectRoot
try {
    $runArgs = @('-f', (Join-Path $PSScriptRoot 'pom.xml'), 'test')
    if ($BaseUrl) { $runArgs += "-Dcinebook.baseUrl=$BaseUrl" }
    if ($PSBoundParameters.ContainsKey('PauseMs')) { $runArgs += "-Dcinebook.technique.pauseMs=$PauseMs" }
    if ($PSBoundParameters.ContainsKey('Headless')) { $runArgs += "-Dcinebook.headless=$($Headless.IsPresent.ToString().ToLowerInvariant())" }
    $runArgs += "-Dcinebook.configFile=$resolvedConfigFile"
    if ($Test) { $runArgs += "-Dtest=$Test" }
    & $wrapper @runArgs
    $runExitCode = $LASTEXITCODE
} finally { Pop-Location }
exit $runExitCode
