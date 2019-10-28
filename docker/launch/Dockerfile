FROM benjixd/alpine-jdk7-jce-unlimited:latest

ENV DIETSTORY_ROOT="/mnt"

CMD cd ${DIETSTORY_ROOT} \
	&& java -Xmx2048m -cp "dist/*:cores/*" -Dwzpath="wz/" net.server.Server