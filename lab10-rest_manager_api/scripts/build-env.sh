#!/usr/bin/env bash
#
# Builds the lab10 environment from scratch with the REST Manager API (REST v3):
# GSCs, a partitioned space with backups, and demo data. It needs only curl and jq,
# so it can run anywhere that can reach the REST Manager: a laptop, a CI job, a cron job.
#
# Usage: ./build-env.sh
#
# Settings (environment variables, defaults in brackets):
#   REST_URL    REST Manager base URL         [http://localhost:9090/api/v3]
#   SPACE_NAME  space to deploy               [demo]
#   PARTITIONS  number of partitions          [2]
#   GSC_MEMORY  heap size of each GSC         [100m]
#   OBJECTS     demo objects to load (≤100000) [60000]

set -euo pipefail
shopt -s inherit_errexit   # a failure inside $(...) also stops the script

readonly REST_URL="${REST_URL:-http://localhost:9090/api/v3}"
readonly SPACE_NAME="${SPACE_NAME:-demo}"
readonly PARTITIONS="${PARTITIONS:-2}"
readonly GSC_MEMORY="${GSC_MEMORY:-100m}"
readonly OBJECTS="${OBJECTS:-60000}"

readonly GSC_COUNT=$(( PARTITIONS * 2 ))   # one GSC per instance: a primary and a backup per partition
readonly POLL_SECONDS=2

readonly DATA_TYPE='{
    "id": ["id"],
    "routingProperty": "id",
    "properties": {
        "id":   "java.lang.Integer",
        "txt1": "java.lang.String",
        "txt2": "java.lang.String",
        "txt3": "java.lang.String"
    }
}'


main() {
    require_tools curl jq
    show_grid
    ensure_space_not_deployed
    create_gscs
    deploy_space
    load_demo_data
    show_instances
}


# --- Build steps -------------------------------------------------------------

show_grid() {
    local info
    info=$(rest GET /info | jq -r '"GigaSpaces \(.revision), managers \(.managers | join(","))"')
    log "Grid at $REST_URL: $info"
}

ensure_space_not_deployed() {
    local spaces
    spaces=$(rest GET /spaces)
    if jq -e --arg name "$SPACE_NAME" 'any(.[]; .name == $name)' <<< "$spaces" > /dev/null; then
        fail "space $SPACE_NAME is already deployed"
    fi
}

create_gscs() {
    local host request gsc i
    local requests=()
    host=$(rest GET /hosts | jq -r '.[0].name')
    log "Creating $GSC_COUNT GSCs of $GSC_MEMORY on $host"

    # Submit every request first so the GSCs start in parallel, then wait for each one
    for (( i = 0; i < GSC_COUNT; i++ )); do
        request=$(post_json /containers "$(jq -n --arg host "$host" --arg memory "$GSC_MEMORY" \
                                               '{host: $host, memory: $memory}')")
        requests+=("$request")
    done
    for request in "${requests[@]}"; do
        wait_for_request "$request"
        gsc=$(container_created_by "$request")
        log "  GSC $gsc"
    done
}

deploy_space() {
    local request
    log "Deploying space $SPACE_NAME ($PARTITIONS partitions with backups)"
    request=$(rest POST "/spaces?name=$SPACE_NAME&partitions=$PARTITIONS&backups=true")
    wait_for_request "$request" 180
}

load_demo_data() {
    local request
    log "Loading $OBJECTS Data objects"
    post_json "/spaces/$SPACE_NAME/types/Data" "$DATA_TYPE" > /dev/null
    request=$(rest POST "/spaces/$SPACE_NAME/types/Data/objectsNum/$OBJECTS/generateDemoData")
    wait_for_request "$request" 300
    wait_until_every_instance_has_objects
}

show_instances() {
    log "Done. Instances of $SPACE_NAME:"
    rest GET "/pus/$SPACE_NAME/instancesUtilization" | jq -r '
        sort_by(.id)[] | "  \(.id)\t\(.mode)\t\(.containerId)\t\(.objectCount) objects"'
}


# --- REST helpers ------------------------------------------------------------

# rest METHOD PATH [CURL OPTIONS...]
# Prints the response body. Stops the script if the call fails or the status is not 2xx.
rest() {
    local method="$1" path="$2" response status body
    shift 2
    response=$(curl -s -w '\n%{http_code}' -X "$method" "$REST_URL$path" "$@") \
        || fail "cannot reach $REST_URL"
    status="${response##*$'\n'}"
    body="${response%$'\n'*}"
    [[ "$status" == 2* ]] || fail "$method $path returned HTTP $status: $body"
    echo "$body"
}

# post_json PATH JSON
post_json() {
    rest POST "$1" -H 'Content-Type: application/json' -d "$2"
}

# wait_for_request REQUEST_ID [TIMEOUT_SECONDS]
# Write calls return a request id at once; this waits until the request is successful.
wait_for_request() {
    local request="$1" timeout="${2:-120}" status
    local deadline=$(( SECONDS + timeout ))
    while (( SECONDS < deadline )); do
        status=$(rest GET "/requests/$request" | jq -r .status)
        case "$status" in
            successful)       return 0 ;;
            failed|cancelled) fail "request $request $status: $(rest GET "/requests/$request" | jq -r .error)" ;;
        esac
        sleep "$POLL_SECONDS"
    done
    fail "request $request is still $status after ${timeout}s"
}

# container_created_by REQUEST_ID
# The statusUrl of a successful "create container" request ends with the new container's id.
container_created_by() {
    rest GET "/requests/$1" | jq -r '.statusUrl | split("/") | last'
}

# Right after loading, the per-instance object counts can show 0 for a few seconds.
wait_until_every_instance_has_objects() {
    local fewest
    local deadline=$(( SECONDS + 30 ))
    while (( SECONDS < deadline )); do
        fewest=$(rest GET "/pus/$SPACE_NAME/instancesUtilization" | jq 'map(.objectCount // 0) | min')
        if (( fewest > 0 )); then
            return 0
        fi
        sleep "$POLL_SECONDS"
    done
}


# --- General helpers ---------------------------------------------------------

log()  { echo "$(date '+%H:%M:%S') $*"; }
fail() { log "ERROR: $*" >&2; exit 1; }

require_tools() {
    local tool
    for tool in "$@"; do
        command -v "$tool" > /dev/null || fail "'$tool' is required"
    done
}


main "$@"
