FROM junlong5/centos7-java:20210405

COPY ./target/tcpserver.tar.gz .
RUN \
 mkdir -p /data1/service/tcpserver && \
 tar -zxf tcpserver.tar.gz -C /data1/service/tcpserver && \
 chmod +x /data1/service/tcpserver/bin/start.sh && \
 rm -rf tcpserver.tar.gz

EXPOSE 7001
EXPOSE 8001

CMD ["/data1/service/tcpserver/bin/start.sh"]
