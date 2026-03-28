param(
    [ValidateSet("exe", "app-image")]
    [string]$PackageType = "exe",
    [string]$AppName = "Import Voitures Guinee",
    [string]$Vendor = "Import Voitures Guinee",
    [string]$MainClass = "com.importation.App",
    [string]$JavaFxSdk = "",
    [string]$AppVersion = "",
    [string]$UpgradeUuid = "0cdbd508-a4a5-4f9d-bf1c-b2ea7fdf6d16"
)

$ErrorActionPreference = "Stop"

$ScriptDir = Split-Path -Parent $MyInvocation.MyCommand.Path
$ProjectRoot = (Resolve-Path (Join-Path $ScriptDir "..")).Path
[xml]$Pom = Get-Content (Join-Path $ProjectRoot "pom.xml")

if ([string]::IsNullOrWhiteSpace($AppVersion)) {
    $AppVersion = $Pom.project.version
}

$ArtifactId = $Pom.project.artifactId
$MainJar = "$ArtifactId-$AppVersion.jar"
$InputDir = Join-Path $ProjectRoot "target\jpackage\input"
$DistDir = Join-Path $ProjectRoot "target\jpackage\dist"
$JavaFxSdkResolved = if ([string]::IsNullOrWhiteSpace($JavaFxSdk)) {
    Join-Path $ProjectRoot "lib\javafx-sdk-21"
} else {
    $JavaFxSdk
}
$JavaFxLib = Join-Path $JavaFxSdkResolved "lib"
$IconPath = Join-Path $ProjectRoot "packaging\windows\app.ico"
$JPackageExe = if ($env:JAVA_HOME) {
    Join-Path $env:JAVA_HOME "bin\jpackage.exe"
} else {
    $null
}
$MavenDependencyGoal = "org.apache.maven.plugins:maven-dependency-plugin:3.6.1:copy-dependencies"
$ExcludedArtifacts = "javafx-base,javafx-controls,javafx-fxml,javafx-graphics,javafx-media,javafx-swing,javafx-web"
$JPackageModules = "java.base,java.datatransfer,java.desktop,java.logging,java.management,java.naming,java.prefs,java.sql,java.xml,jdk.unsupported,javafx.controls,javafx.fxml,javafx.graphics"

function Test-CommandExists {
    param([string]$CommandName)
    return [bool](Get-Command $CommandName -ErrorAction SilentlyContinue)
}

if (-not $env:JAVA_HOME) {
    throw "JAVA_HOME n'est pas defini."
}

if (-not (Test-Path $JPackageExe)) {
    throw "jpackage est introuvable dans JAVA_HOME: $JPackageExe"
}

if (-not (Test-Path $JavaFxLib)) {
    throw "JavaFX SDK introuvable: $JavaFxLib"
}

if (-not (Test-CommandExists "mvn")) {
    throw "La commande 'mvn' est introuvable dans le PATH."
}

if ($PackageType -eq "exe") {
    if (-not (Test-CommandExists "candle.exe") -or -not (Test-CommandExists "light.exe")) {
        throw "WiX Toolset 3.x est requis pour generer un .exe avec jpackage (candle.exe et light.exe)."
    }
}

Write-Host "Preparation du packaging Windows..."
Write-Host "Project root  : $ProjectRoot"
Write-Host "Package type  : $PackageType"
Write-Host "Main class    : $MainClass"
Write-Host "Main jar      : $MainJar"
Write-Host "JavaFX SDK    : $JavaFxSdkResolved"

Push-Location $ProjectRoot
try {
    Remove-Item -Recurse -Force $InputDir, $DistDir -ErrorAction SilentlyContinue
    New-Item -ItemType Directory -Force -Path $InputDir, $DistDir | Out-Null

    & mvn `
        clean package `
        $MavenDependencyGoal `
        "-DincludeScope=runtime" `
        "-DexcludeArtifactIds=$ExcludedArtifacts" `
        "-DoutputDirectory=$InputDir"

    $BuiltJar = Join-Path $ProjectRoot "target\$MainJar"
    if (-not (Test-Path $BuiltJar)) {
        throw "JAR principal introuvable apres build: $BuiltJar"
    }

    Copy-Item $BuiltJar $InputDir -Force

    $JPackageArgs = @(
        "--type", $PackageType,
        "--name", $AppName,
        "--app-version", $AppVersion,
        "--vendor", $Vendor,
        "--description", "Gestion desktop de l'importation de voitures",
        "--dest", $DistDir,
        "--input", $InputDir,
        "--main-jar", $MainJar,
        "--main-class", $MainClass,
        "--module-path", "$env:JAVA_HOME\jmods;$JavaFxLib",
        "--add-modules", $JPackageModules,
        "--java-options", "--add-modules=javafx.controls,javafx.fxml,javafx.graphics",
        "--java-options", "-Dfile.encoding=UTF-8",
        "--win-menu",
        "--win-menu-group", "Import Voitures Guinee",
        "--win-shortcut",
        "--win-dir-chooser",
        "--win-per-user-install",
        "--install-dir", "ImportVoituresGuinee",
        "--win-upgrade-uuid", $UpgradeUuid,
        "--verbose"
    )

    if (Test-Path $IconPath) {
        $JPackageArgs += @("--icon", $IconPath)
    } else {
        Write-Warning "Aucune icone .ico trouvee dans packaging/windows/app.ico. Le packaging continuera sans icone Windows."
    }

    & $JPackageExe @JPackageArgs

    Write-Host ""
    Write-Host "Packaging termine."
    Write-Host "Sortie : $DistDir"
} finally {
    Pop-Location
}
