#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")"
case "${1:-test}" in
 test) node --test tests/*.test.cjs ;;
 build) ./build.sh ;;
 *) echo 'Usage: ./cli.sh [test|build]'; exit 1 ;;
esac
