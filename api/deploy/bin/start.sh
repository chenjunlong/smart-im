#!/bin/bash
set -e
cd $(dirname "$0")
cd ..

DEPLOY_DIR=$(pwd)
PID_FILE=${DEPLOY_DIR}/pid
if [[ ! -f ${PID_FILE} ]]; then
  touch "${PID_FILE}"
else
  PID=$(cat "${PID_FILE}")
  PID_EXIST=$(ps aux | grep $PID | grep -v 'grep')
  if [[ ! -z "$PID_EXIST" ]]; then
    echo "agent is running,no need to start again!..."
    exit 1
  fi
fi

CONF_DIR=${DEPLOY_DIR}/conf
LOGS_DIR=${DEPLOY_DIR}/logs
if [[ ! -d ${LOGS_DIR} ]]; then
  mkdir "${LOGS_DIR}"
fi

STDOUT_FILE=${LOGS_DIR}/stdout.log
LIB_DIR=${DEPLOY_DIR}/lib
LIB_JARS=$(ls ${LIB_DIR} | grep .jar | awk '{print "'${LIB_DIR}'/"$0}' | tr "\n" ":")
JAVA_OPTS=" -Djava.awt.headless=true -Djava.net.preferIPv4Stack=true "
JAVA_MEM_OPTS="-server -Xmx2g -Xms2g -Xmn1g -XX:SurvivorRatio=8 -XX:+UseG1GC -XX:MaxTenuringThreshold=14 -XX:ParallelGCThreads=8 -XX:ConcGCThreads=8 -XX:+DisableExplicitGC -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=$LOGS_DIR/gc.dump -XX:+PrintGCDetails -XX:+PrintGCTimeStamps -XX:+PrintHeapAtGC -XX:+TraceClassLoading -XX:+PrintClassHistogram -Xloggc:$LOGS_DIR/gc.log"

echo -e "Starting the ${DEPLOY_DIR} ...\c"
nohup java "${JAVA_OPTS}" "${JAVA_MEM_OPTS}" -classpath "${CONF_DIR}":"${LIB_JARS}" com.smart.api.BootStrapApplication >"${STDOUT_FILE}" 2>&1 &
echo "$!" >"${PID_FILE}"

echo "OK!"
echo "PID: $(cat "${PID_FILE}")"
echo "STDOUT: $STDOUT_FILE"
echo "to show log,please try :"
