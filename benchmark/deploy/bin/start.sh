#!/bin/bash
set -e
cd $(dirname "$0")
cd ..

MODE="$1"

DEPLOY_DIR=$(pwd)
PID_FILE=${DEPLOY_DIR}/pid
if [[ ! -f ${PID_FILE} ]]; then
  touch "${PID_FILE}"
else
  PID=$(cat "${PID_FILE}")
  PID_EXIST=$(ps aux | grep "$PID" | grep -v 'grep')
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
LIB_JARS=$(ls "${LIB_DIR}" | grep .jar | awk '{print "'"${LIB_DIR}"'/"$0}' | tr "\n" ":")
JAVA_OPTS="-Djava.awt.headless=true -Djava.net.preferIPv4Stack=true"
JAVA_MEM_OPTS=" -server -Xmx6g -Xms6g -Xmn4g -XX:MaxMetaspaceSize=1g -Xss1m -XX:+DisableExplicitGC -XX:+UseConcMarkSweepGC -XX:+CMSParallelRemarkEnabled -XX:+UseCMSCompactAtFullCollection -XX:LargePageSizeInBytes=128m -XX:+UseFastAccessorMethods -XX:+UseCMSInitiatingOccupancyOnly -XX:CMSInitiatingOccupancyFraction=70 -Xloggc:$LOGS_DIR/gc.log"

echo -e "Starting the ${DEPLOY_DIR} ...\c"

if [ "$MODE" = "docker" ]; then
  java $JAVA_OPTS $JAVA_MEM_OPTS -classpath $CONF_DIR:$LIB_JARS com.smart.benchmark.BenchmarkApplication >$STDOUT_FILE
else
  java $JAVA_OPTS $JAVA_MEM_OPTS -classpath $CONF_DIR:$LIB_JARS com.smart.benchmark.BenchmarkApplication >$STDOUT_FILE 2>&1 &
fi

echo "$!" >"${PID_FILE}"

echo "OK!"
echo "PID: $(cat "${PID_FILE}")"
echo "STDOUT: $STDOUT_FILE"
