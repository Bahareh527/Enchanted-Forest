#!/usr/bin/env bash
set -euo pipefail

repo_root="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
main_classes="$repo_root/build/classes"
test_classes="$repo_root/build/test-classes"

rm -rf "$main_classes" "$test_classes"
mkdir -p "$main_classes" "$test_classes"

find "$repo_root/src/main/java" -name '*.java' -print0 \
  | xargs -0 javac -Xlint:all -Werror --release 17 -d "$main_classes"

find "$repo_root/src/test/java" -name '*.java' -print0 \
  | xargs -0 javac -Xlint:all -Werror --release 17 -cp "$main_classes" -d "$test_classes"

java -ea -cp "$main_classes:$test_classes" io.github.bahareh527.enchantedforest.AllTests
