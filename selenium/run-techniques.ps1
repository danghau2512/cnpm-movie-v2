param(
    [ValidateSet('all','remaining','F01','F02','F03','F04','F05','F06','F07','F08')]
    [string]$Feature = 'remaining',
    [string]$ConfigFile = '',
    [ValidateRange(0,10000)]
    [int]$PauseMs = 2500,
    [switch]$Headless
)
$ErrorActionPreference = 'Stop'
$classes = [ordered]@{
    F01='F01EquivalencePartitionTest'
    F02='F02EquivalencePartitionTest'
    F03='F03DecisionTableTest'
    F04='F04DecisionTableTest'
    F05='F05PaymentStateTest'
    F06='F06DurationBoundaryTest'
    F07='F07ShowtimeDecisionTest'
    F08='F08BookingStateTest'
}
if ($Feature -eq 'all') { $test = $classes.Values -join ',' }
elseif ($Feature -eq 'remaining') { $test = ($classes.GetEnumerator() | Where-Object {$_.Key -notin @('F02','F03')} | ForEach-Object {$_.Value}) -join ',' }
else { $test = $classes[$Feature] }
$run = @{Test=$test;PauseMs=$PauseMs}
if ($ConfigFile) { $run.ConfigFile = $ConfigFile }
if ($PSBoundParameters.ContainsKey('Headless')) { $run.Headless = $Headless }
& (Join-Path $PSScriptRoot 'run-selenium.ps1') @run
exit $LASTEXITCODE
