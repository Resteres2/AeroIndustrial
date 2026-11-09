$ErrorActionPreference = 'Stop'
& "$PSScriptRoot\gradlew.bat" build
if ($LASTEXITCODE -ne 0) { exit $LASTEXITCODE }
