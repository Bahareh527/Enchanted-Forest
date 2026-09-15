$ErrorActionPreference = "Stop"

$repoRoot = Split-Path -Parent $PSScriptRoot
$mainClasses = Join-Path $repoRoot "build\classes"

if (Test-Path -LiteralPath $mainClasses) {
    Remove-Item -LiteralPath $mainClasses -Recurse -Force
}
New-Item -ItemType Directory -Force -Path $mainClasses | Out-Null

$mainSources = Get-ChildItem -LiteralPath (Join-Path $repoRoot "src\main\java") -Filter "*.java" -Recurse
javac --release 17 -d $mainClasses $mainSources.FullName
if ($LASTEXITCODE -ne 0) { exit $LASTEXITCODE }

java -cp $mainClasses io.github.bahareh527.enchantedforest.Main
exit $LASTEXITCODE
