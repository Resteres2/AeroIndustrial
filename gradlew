#!/usr/bin/env sh
set -eu
ROOT="$(CDPATH= cd -- "$(dirname -- "$0")" && pwd)"
GRADLE_VERSION=8.12.1
DIST="$ROOT/.gradle-dist/gradle-$GRADLE_VERSION"
if command -v gradle >/dev/null 2>&1; then
  exec gradle "$@"
fi
if [ ! -x "$DIST/bin/gradle" ]; then
  mkdir -p "$ROOT/.gradle-dist"
  ZIP="$ROOT/.gradle-dist/gradle-$GRADLE_VERSION-bin.zip"
  if [ ! -f "$ZIP" ]; then
    if command -v curl >/dev/null 2>&1; then
      curl -fL --retry 3 "https://services.gradle.org/distributions/gradle-$GRADLE_VERSION-bin.zip" -o "$ZIP"
    elif command -v wget >/dev/null 2>&1; then
      wget -O "$ZIP" "https://services.gradle.org/distributions/gradle-$GRADLE_VERSION-bin.zip"
    else
      python3 - "$ZIP" <<'PY'
import sys, urllib.request
urllib.request.urlretrieve('https://services.gradle.org/distributions/gradle-8.12.1-bin.zip', sys.argv[1])
PY
    fi
  fi
  python3 - "$ZIP" "$ROOT/.gradle-dist" <<'PY'
import sys, zipfile, os
zipfile.ZipFile(sys.argv[1]).extractall(sys.argv[2])
PY
fi
exec "$DIST/bin/gradle" "$@"
