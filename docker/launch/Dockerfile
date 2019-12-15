FROM benjixd/alpine-jdk7-jce-unlimited:latest

# Default values for server if they weren't provided
ENV SERVER_HOST=localhost
ENV MYSQL_DB_USER=root
ENV MYSQL_ROOT_PASSWORD=password
ENV MYSQL_HOST_NAME=localhost
ENV JAVA_STATIC_MEM=2048

# Install envsubst
RUN apk add --update openssl \
	&& wget https://github.com/a8m/envsubst/releases/download/v1.1.0/envsubst-Linux-x86_64 \
	&& mv envsubst-Linux-x86_64 /bin/envsubst \
	&& chmod 755 /bin/envsubst

ENV DIETSTORY_ROOT="/mnt"

CMD cd ${DIETSTORY_ROOT} \
	&& envsubst < configuration_template.ini > configuration.ini \
	&& java -Xmx${JAVA_STATIC_MEM}m -cp "dist/*:cores/*" -Dwzpath="wz/" net.server.Server