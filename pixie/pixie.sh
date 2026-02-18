#!/usr/bin/env bash
set -e

# ============================================================
#  pixie ✨ — tiny Gradle project starter
# ============================================================
#  This script bootstraps a new Gradle project from a template
#  It renames the directory and replaces placeholders in files
# ============================================================

echo ""
echo "✨ pixie — Gradle project sprouter"
echo ""


TEMPLATE_REPO="https://github.com/matejstastny/pixie.git"

PIXIE_IGNORE=(
  ".git"
  "pixie.sh"
)

shopt -s nullglob dotglob
FILES=(*)
shopt -u nullglob dotglob

if [[ ${#FILES[@]} -gt 0 ]]; then
  echo "⚠️  This directory is not empty."
  read -rp "Continue and replace files with template? [y/N]: " CONFIRM
  case "$CONFIRM" in
    y|Y|yes|YES)
      echo "📦 Continuing..."
      ;;
    *)
      echo "❌ Aborted."
      exit 1
      ;;
  esac
fi

echo "📦 Cloning starter template..."
git clone "$TEMPLATE_REPO" .pixie-tmp >/dev/null 2>&1

RSYNC_EXCLUDES=()
for pattern in "${PIXIE_IGNORE[@]}"; do
  RSYNC_EXCLUDES+=(--exclude "$pattern")
done

rsync -a --delete \
  "${RSYNC_EXCLUDES[@]}" \
  .pixie-tmp/ ./

rm -rf .pixie-tmp

# ---------------------------
# Prompt for project name
# ---------------------------

DEFAULT_PROJECT_NAME="$(basename "$(pwd)")"
read -rp "Project name [$DEFAULT_PROJECT_NAME]: " PROJECT_NAME

if [[ -z "$PROJECT_NAME" ]]; then
  PROJECT_NAME="$DEFAULT_PROJECT_NAME"
fi

# Group ID (auto-generated)
DEFAULT_GROUP="matejstastny.$PROJECT_NAME"
read -rp "Group ID [$DEFAULT_GROUP]: " GROUP_ID

if [[ -z "$GROUP_ID" ]]; then
  GROUP_ID="$DEFAULT_GROUP"
fi

# ---------------------------
# Rename root directory
# ---------------------------

CURRENT_DIR="$(pwd)"
PARENT_DIR="$(dirname "$CURRENT_DIR")"
CURRENT_NAME="$(basename "$CURRENT_DIR")"

if [[ "$CURRENT_NAME" != "$PROJECT_NAME" ]]; then
  cd "$PARENT_DIR"
  mv "$CURRENT_NAME" "$PROJECT_NAME"
  cd "$PROJECT_NAME"
fi

# ---------------------------
# Rename project directory
# ---------------------------

JAVA_BASE="app/src/main/java"
OLD_PACKAGE_PATH="$JAVA_BASE/matejstastny/pixie"
NEW_PACKAGE_PATH="$JAVA_BASE/$(echo "$GROUP_ID" | tr '.' '/')"

if [[ -d "$OLD_PACKAGE_PATH" ]]; then
  mkdir -p "$(dirname "$NEW_PACKAGE_PATH")"
  mv "$OLD_PACKAGE_PATH" "$NEW_PACKAGE_PATH"
fi

# ---------------------------
# Replace placeholders
# ---------------------------

echo "🔧 Applying template values..."

find . -type f \
  ! -path "./.git/*" \
  ! -path "./gradle/wrapper/*" \
  -exec sed -i '' "s/__PROJECT_NAME__/$PROJECT_NAME/g" {} +

find . -type f \
  ! -path "./.git/*" \
  ! -path "./gradle/wrapper/*" \
  -exec sed -i '' "s/__GROUP__/$GROUP_ID/g" {} +

# ---------------------------
# Git cleanup
# ---------------------------

if [[ ! -d ".git" ]]; then
  git init -q
fi

git add .
git commit -q -m "Initial commit from pixie"

# ---------------------------
# Done
# ---------------------------

echo ""
echo "🌱 Project '$PROJECT_NAME' is ready"
echo ""
