$ErrorActionPreference = "Stop"

$repoRoot = Split-Path -Parent $PSScriptRoot
$mainClasses = Join-Path $repoRoot "build\classes"
$testClasses = Join-Path $repoRoot "build\test-classes"

foreach ($directory in @($mainClasses, $testClasses)) {
    if (Test-Path -LiteralPath $directory) {
        Remove-Item -LiteralPath $directory -Recurse -Force
    }
    New-Item -ItemType Directory -Force -Path $directory | Out-Null
}

$mainSources = Get-ChildItem -LiteralPath (Join-Path $repoRoot "src\main\java") -Filter "*.java" -Recurse
javac -Xlint:all -Werror --release 17 -d $mainClasses $mainSources.FullName
if ($LASTEXITCODE -ne 0) { exit $LASTEXITCODE }

$testSources = Get-ChildItem -LiteralPath (Join-Path $repoRoot "src\test\java") -Filter "*.java" -Recurse
javac -Xlint:all -Werror --release 17 -cp $mainClasses -d $testClasses $testSources.FullName
if ($LASTEXITCODE -ne 0) { exit $LASTEXITCODE }

java -ea -cp \"$mainClasses;$testClasses\" io.github.bahareh527.enchantedforest.AllTests
exit $LASTEXITCODE
