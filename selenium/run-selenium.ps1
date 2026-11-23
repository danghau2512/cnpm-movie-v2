param(
    [string]$BaseUrl = '',
    [string]$Test = '',
    [string]$ConfigFile = '',
    [ValidateRange(0,10000)]
    [int]$PauseMs = 2500,
    [switch]$Headless
)
$ErrorActionPreference = 'Stop'
$projectRoot = Split-Path $PSScriptRoot -Parent
$wrapper = Join-Path $projectRoot 'mvnw.cmd'
if (!(Test-Path -LiteralPath $wrapper)) { throw "Maven Wrapper not found: $wrapper" }
$resolvedConfigFile = if ($ConfigFile) { (Resolve-Path -LiteralPath $ConfigFile).Path } else { '' }
Push-Location $projectRoot
try {
    $runArgs = @('-f', (Join-Path $PSScriptRoot 'pom.xml'), 'test')
    if ($BaseUrl) { $runArgs += "-Dcinebook.baseUrl=$BaseUrl" }
    if ($PSBoundParameters.ContainsKey('PauseMs')) { $runArgs += "-Dcinebook.technique.pauseMs=$PauseMs" }
    if ($PSBoundParameters.ContainsKey('Headless')) { $runArgs += "-Dcinebook.headless=$($Headless.IsPresent.ToString().ToLowerInvariant())" }
    if ($resolvedConfigFile) { $runArgs += "-Dcinebook.configFile=$resolvedConfigFile" }
    elseif (Test-Path -LiteralPath (Join-Path $PSScriptRoot 'mainflows.local.properties')) {
        $runArgs += "-Dcinebook.configFile=$(Join-Path $PSScriptRoot 'mainflows.local.properties')"
    }
    if ($Test) { $runArgs += "-Dtest=$Test" }
    & $wrapper @runArgs
    $runExitCode = $LASTEXITCODE
} finally { Pop-Location }
exit $runExitCode
