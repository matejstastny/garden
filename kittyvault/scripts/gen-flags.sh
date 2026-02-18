#!/usr/bin/env bash

set -euo pipefail

# --------------------------------------------------------------------------------------------
# gen-flags.sh - reusable script for CLI projects using gengetoptions
# --------------------------------------------------------------------------------------------
# Author: Matej Stastny
# Date: 2025-02-09
# License: MIT license
# See: https://github.com/ko1nksm/getoptions
# --------------------------------------------------------------------------------------------

SCRIPT_PATH="../src/kittyvault" # path to script relative to location of this script
CMD="gengetoptions"             # generating tool

# Logging ------------------------------------------------------------------------------------

info() { echo -e " \033[1;34m📦 $* \033[0m"; }
warn() { echo -e " \033[1;33m⚠ $* \033[0m"; }
error() { echo -e " \033[1;31m❌ $* \033[0m" >&2; }
success() { echo -e " \033[1;32m✅ $* \033[0m"; }

# Main ---------------------------------------------------------------------------------------

if ! command -v "$CMD" >/dev/null 2>&1; then
    error "Missing dependency: $CMD"
    exit 1
fi

path_temp="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)/$SCRIPT_PATH"
script="$(realpath "$path_temp" 2>/dev/null)" || {
    error "Target script not found: $path_temp"
    exit 2
}
script_name=$(basename "$script")

info "Generating flags for $script_name"
if "$CMD" embed --overwrite "$script"; then
    success "Flag code successfully embedded into $script_name"
else
    error "Flag generation failed"
    exit 1
fi
