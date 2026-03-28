#!/usr/bin/env bash
# =============================================================================
#  CoinDesk — Backend Deploy Script
#  Lives at  : Backend/scripts/deploy.sh
#  Called as : bash Backend/scripts/deploy.sh  (from repo root)
# =============================================================================

set -euo pipefail

# ─── Resolve paths ───────────────────────────────────────────────────────────
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
BACKEND_DIR="$(cd "$SCRIPT_DIR/.." && pwd)"       # .../Crypto-Trading-Simulator/Backend
REPO_ROOT="$(cd "$BACKEND_DIR/.." && pwd)"         # .../Crypto-Trading-Simulator
ENV_FILE="$BACKEND_DIR/.env"
LOG_DIR="$BACKEND_DIR/logs"
DEPLOY_LOG="$LOG_DIR/deploy.log"

# ─── Bootstrap logging ───────────────────────────────────────────────────────
mkdir -p "$LOG_DIR"
exec > >(tee -a "$DEPLOY_LOG") 2>&1

# ─── Helpers ─────────────────────────────────────────────────────────────────
log()  { echo "[$(date '+%Y-%m-%d %H:%M:%S')] [INFO]  $*"; }
warn() { echo "[$(date '+%Y-%m-%d %H:%M:%S')] [WARN]  $*"; }
err()  { echo "[$(date '+%Y-%m-%d %H:%M:%S')] [ERROR] $*"; }
sep()  { echo ""; echo "════════════════════════════════════════════════════"; echo ""; }

# ─── Banner ──────────────────────────────────────────────────────────────────
sep
log "╔══════════════════════════════════════╗"
log "║     COINDESK BACKEND — DEPLOYING     ║"
log "╚══════════════════════════════════════╝"
log "Repo root  : $REPO_ROOT"
log "Backend dir: $BACKEND_DIR"
log "Commit SHA : $(git -C "$REPO_ROOT" rev-parse HEAD)"
log "Branch     : $(git -C "$REPO_ROOT" rev-parse --abbrev-ref HEAD)"
log "Author     : $(git -C "$REPO_ROOT" log -1 --pretty='%an <%ae>')"
log "Message    : $(git -C "$REPO_ROOT" log -1 --pretty='%s')"
sep

# ─── Sanity checks ───────────────────────────────────────────────────────────
if [[ ! -f "$ENV_FILE" ]]; then
  err ".env not found at: $ENV_FILE"
  err "Run on VM:  cp $BACKEND_DIR/.env.example $ENV_FILE && nano $ENV_FILE"
  exit 1
fi

for cmd in docker curl ss; do
  if ! command -v "$cmd" &>/dev/null; then
    err "'$cmd' is not installed. Please install it on the VM."
    exit 1
  fi
done

if ! docker compose version &>/dev/null; then
  err "docker compose plugin not found."
  exit 1
fi

# ─── Load .env ───────────────────────────────────────────────────────────────
# Strip spaces around '=' before sourcing (handles "REDIS_PASS =value" typo)
set -a
source <(sed 's/ *= */=/' "$ENV_FILE" | grep -E '^[A-Za-z_][A-Za-z0-9_]*=.+' | grep -v '^\s*#')
set +a

# Only REGISTRY_PORT and GATEWAY_PORT are host-exposed → only these need conflict check
REGISTRY_PORT="${REGISTRY_PORT:-8761}"
GATEWAY_PORT="${GATEWAY_PORT:-8081}"

log "Loaded $ENV_FILE"
log "  REGISTRY_PORT (Eureka)  = $REGISTRY_PORT"
log "  GATEWAY_PORT (Gateway)  = $GATEWAY_PORT"

# ─── Port conflict checker ────────────────────────────────────────────────────
# Finds next free host port, patches .env if changed, echoes final port.
ensure_port_free() {
  local env_key="$1"
  local port="$2"
  local original="$port"

  while ss -tlnp 2>/dev/null | awk '{print $4}' | grep -qE "(^|:)${port}$"; do
    warn "Port $port is occupied by a foreign process."
    port=$((port + 1))
    warn "  → Trying port $port ..."
  done

  if [[ "$port" -ne "$original" ]]; then
    log "Port reassigned: $original → $port  (key: $env_key)"
    if grep -q "^${env_key}=" "$ENV_FILE"; then
      sed -i "s|^${env_key}=.*|${env_key}=${port}|" "$ENV_FILE"
    else
      echo "${env_key}=${port}" >> "$ENV_FILE"
    fi
    warn "⚠️  .env patched: ${env_key}=${port}"
    [[ "$env_key" == "GATEWAY_PORT" ]] && \
      warn "   Update VITE_API_URL in Vercel to use the new port!"
  else
    log "Port $port is free ✓"
  fi

  echo "$port"
}

# ─── Stop existing containers first (so our own ports are freed) ──────────────
sep
log "Stopping existing containers..."
cd "$BACKEND_DIR"
docker compose down --remove-orphans 2>&1 || true
log "Containers stopped."

# ─── Port checks (after down — self ports are now free) ──────────────────────
sep
log "Checking host-exposed ports..."
REGISTRY_PORT=$(ensure_port_free "REGISTRY_PORT" "$REGISTRY_PORT")
GATEWAY_PORT=$(ensure_port_free  "GATEWAY_PORT"  "$GATEWAY_PORT")
export REGISTRY_PORT GATEWAY_PORT

# ─── Build + start ───────────────────────────────────────────────────────────
sep
log "Building and starting all services..."
docker compose up --build -d 2>&1
log "All containers launched."

# ─── Health checks ───────────────────────────────────────────────────────────
sep
log "Running health checks..."

# wait_for_service <name> <port> <path> <max_retries>
wait_for_service() {
  local name="$1"
  local port="$2"
  local path="${3:-/actuator/health}"
  local max_retries="${4:-36}"     # 36 × 5s = 3 minutes
  local attempt=1

  log "Waiting for $name on port $port ..."

  until curl -sf "http://localhost:${port}${path}" -o /dev/null 2>/dev/null; do
    if [[ $attempt -ge $max_retries ]]; then
      err "$name did not become healthy after $((max_retries * 5))s."
      err "Debug: docker compose logs $(echo "$name" | tr '[:upper:]' '[:lower:]' | tr ' ' '-')"
      return 1
    fi
    log "  [$attempt/$max_retries] Not ready, retrying in 5s..."
    sleep 5
    ((attempt++))
  done

  log "$name is healthy ✓"
}

wait_for_service "Service Registry (Eureka)" "$REGISTRY_PORT" "/"                36
wait_for_service "API Gateway"               "$GATEWAY_PORT"  "/actuator/health" 36

# ─── Summary ─────────────────────────────────────────────────────────────────
sep
log "╔══════════════════════════════════════╗"
log "║       DEPLOY COMPLETE ✓              ║"
log "╚══════════════════════════════════════╝"
log ""
log "  Eureka Dashboard : http://prakhar.systems:${REGISTRY_PORT}"
log "  API Gateway      : http://prakhar.systems:${GATEWAY_PORT}"
log ""
log "  Deploy log : $DEPLOY_LOG"
log ""
log "Running containers:"
docker compose ps --format "table {{.Name}}\t{{.Status}}\t{{.Ports}}"
sep
