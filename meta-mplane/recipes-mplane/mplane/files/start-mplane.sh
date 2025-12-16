#!/bin/sh

LOGDIR=/var/log/mplane
mkdir -p $LOGDIR

log() { echo "[mplane] $*" >> $LOGDIR/mplane-start.log; }

log "Starting netopeer2-server..."
/usr/local/sbin/netopeer2-server -d -v2 >> $LOGDIR/np2.log 2>&1 &

log "Launching M-plane binaries..."
cd /opt/mplane

for bin in h3mp.elf callhome.elf filemanager.elf supervision.elf \
           troubleshooting.elf swmanagement.elf usermgmt.elf; do
  if [ -x "$bin" ]; then
    log "Running $bin..."
    "./$bin" >> $LOGDIR/${bin}.log 2>&1 &
  else
    log "Missing binary: $bin"
  fi
done

log "M-plane started."
exit 0
