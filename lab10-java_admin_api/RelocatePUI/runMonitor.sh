#!/usr/bin/env bash
if [[ -z "${XAPHOMEDIR}" ]]; then
  export XAPHOMEDIR=/home/oferyarom/162/gigaspaces-xap-enterprise-16.1.1
fi

export GS_LOOKUP_GROUPS=xap-16.1.1
java -cp "$XAPHOMEDIR/lib/required/*":"target/*" com.gs.admin.alerts.MonitorStarter $*
