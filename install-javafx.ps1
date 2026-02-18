param(
    [string]$ProjectPath = (Get-Location).Path,
    [string]$JavaFXVersion = "21.0.3"
)

Write-Host "╔════════════════════════════════════════╗"
Write-Host "║ Installation JavaFX SDK 21            ║"
Write-Host "╚════════════════════════════════════════╝"
Write-Host ""

$LibPath = Join-Path $ProjectPath "lib"
$JavaFXPath = Join-Path $LibPath "javafx-sdk-21"
$ZipFile = Join-Path $LibPath "javafx-sdk-21.zip"

# Créer le dossier lib s'il n'existe pas
if (-not (Test-Path $LibPath)) {
    Write-Host "✓ Création du dossier lib..."
    New-Item -ItemType Directory -Path $LibPath | Out-Null
}

Write-Host "ℹ Téléchargement de JavaFX SDK $JavaFXVersion..."
Write-Host ""

# URLs de téléchargement (directes depuis Maven Central)
$DownloadUrl = "https://repo1.maven.org/maven2/org/openjfx/javafx-sdk/21.0.3/javafx-sdk-21.0.3-windows.zip"

# Télécharger le fichier
try {
    $WebClient = New-Object System.Net.ServicePointManager
    $WebClient.SecurityProtocol = [System.Net.SecurityProtocolType]::Tls12
    
    $downloader = New-Object System.Net.WebClient
    Write-Host "⏳ Téléchargement... (cela peut prendre quelques minutes)"
    $downloader.DownloadFile($DownloadUrl, $ZipFile)
    
    if (Test-Path $ZipFile) {
        $FileSize = [math]::Round((Get-Item $ZipFile).Length / 1MB, 2)
        Write-Host "✓ Téléchargement réussi ($FileSize MB)"
    } else {
        Write-Host "✗ Erreur: Fichier non téléchargé"
        exit 1
    }
} catch {
    Write-Host "✗ Erreur de téléchargement: $_"
    Write-Host ""
    Write-Host "Téléchargement alternatif manuellement depuis:"
    Write-Host "  https://gluonhq.com/download/javafx-21-0-3-sdk-windows/"
    exit 1
}

# Extraction
Write-Host ""
Write-Host "⏳ Extraction du fichier..."

if (Test-Path $JavaFXPath) {
    Write-Host "⚠ Suppression de l'ancienne installation..."
    Remove-Item -Recurse -Force $JavaFXPath
}

try {
    Add-Type -AssemblyName System.IO.Compression.FileSystem
    [System.IO.Compression.ZipFile]::ExtractToDirectory($ZipFile, $LibPath)
    Write-Host "✓ Extraction réussie"
} catch {
    Write-Host "✗ Erreur d'extraction: $_"
    exit 1
}

# Nettoyage
Write-Host ""
Write-Host "⏳ Nettoyage..."
Remove-Item $ZipFile -Force
Write-Host "✓ Fichier ZIP supprimé"

# Vérification
Write-Host ""
Write-Host "⏳ Vérification de l'installation..."
$LibFiles = @()
if (Test-Path (Join-Path $JavaFXPath "lib")) {
    $LibFiles = Get-ChildItem (Join-Path $JavaFXPath "lib") -Filter "*.jar"
}

if ($LibFiles.Count -gt 0) {
    Write-Host "✓ JavaFX SDK 21 installé avec succès!"
    Write-Host ""
    Write-Host "📊 Détails:"
    Write-Host "  • Chemin: $JavaFXPath"
    Write-Host "  • Fichiers JAR: $($LibFiles.Count)"
    Write-Host ""
    Write-Host "✅ L'application est prête à être utilisée!"
} else {
    Write-Host "✗ Erreur: Les fichiers JAR n'ont pas été trouvés"
    exit 1
}
