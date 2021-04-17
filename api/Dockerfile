FROM junlong5/centos7-java:20210405

COPY ./target/api.tar.gz .
RUN \
 mkdir -p /data1/service/api && \
 tar -zxf api.tar.gz -C /data1/service/api && \
 chmod +x /data1/service/api/bin/start.sh && \
 rm -rf api.tar.gz

EXPOSE 8000

CMD ["/data1/service/api/bin/start.sh docker"]
