#!/usr/bin/env bash
# Clean generated artifacts beneath this script's directory.
set -euo pipefail

usage() {
    cat <<'EOF'
Usage: ./clear_storage.sh [--dry-run]

Remove local build outputs, dependency directories, Python virtual environments,
and common tool caches. Use --dry-run to preview the paths without deleting them.
Tracked files, lockfiles, source files, and global package caches are preserved.
EOF
}

dry_run=false
case "${1:-}" in
    --dry-run) dry_run=true ;;
    -h|--help) usage; exit 0 ;;
    '') ;;
    *) usage >&2; exit 2 ;;
esac
if (( $# > 1 )); then
    usage >&2
    exit 2
fi

root=$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")" && pwd -P)
cd -- "$root"
# Require a checkout so tracked artifacts can be protected before deletion.
git rev-parse --is-inside-work-tree >/dev/null

count=0
while IFS= read -r -d '' path; do
    parent=${path%/*}
    name=${path##*/}
    case "$name" in
        target)
            [[ -f "$parent/pom.xml" || -f "$parent/Cargo.toml" ]] || continue
            ;;
        build|dist)
            [[ -f "$parent/package.json" || -f "$parent/pyproject.toml" ||
               -f "$parent/setup.py" || -f "$parent/setup.cfg" ||
               -f "$parent/build.gradle" || -f "$parent/build.gradle.kts" ]] || continue
            ;;
        venv|.venv|env|.env)
            [[ -f "$path/pyvenv.cfg" ]] || continue
            ;;
    esac

    # Skip the entire artifact if any part of it is version controlled.
    tracked=$(git ls-files -- "$path")
    if [[ -n "$tracked" ]]; then
        printf 'Skipping tracked artifact: %s\n' "$path"
        continue
    fi

    if "$dry_run"; then
        printf 'Would remove: %s\n' "$path"
    else
        printf 'Removing: %s\n' "$path"
        rm -rf -- "$path"
    fi
    count=$((count + 1))
done < <(find . -type d \( -name .git -o -name .agents -o -name .codex -o -name .aws \) -prune -o \
    -type d \( -name target -o -name build -o -name dist -o -name node_modules \
    -o -name __pycache__ -o -name .pytest_cache -o -name .mypy_cache \
    -o -name .ruff_cache -o -name .tox -o -name .nox -o -name .gradle \
    -o -name .next -o -name .nuxt -o -name .parcel-cache -o -name .turbo \
    -o -name venv -o -name .venv -o -name env -o -name .env \) -prune -print0)

if "$dry_run"; then
    printf 'Dry run: %d artifact directories would be removed.\n' "$count"
else
    printf 'Removed %d artifact directories.\n' "$count"
fi
