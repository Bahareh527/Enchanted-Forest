#!/usr/bin/env bash
set -euo pipefail

repo_root="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
main_classes="$repo_root/build/classes"

rm -rf "$main_classes"
mkdir -p "$main_classes"

find "$repo_root/src/main/java" -name '*.java' -print0 \
  | xargs -0 javac --release 17 -d "$main_classes"

java -cp "$main_classes" io.github.bahareh527.enchantedforest.Main
