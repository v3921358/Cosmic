# Dietstory build
FROM benjixd/alpine-jdk7-jce-unlimited:latest

MAINTAINER Benjamin Zhao <yl4zhao@edu.uwaterloo.ca>

# Environment Variables
ENV BUILD_SOURCE="repository"
ENV BUILD_BRANCH="master"
ENV BUILD_REPO_REMOTE="https://github.com/BenjixD/MapleSolaxiaV2/archive"
ENV TARGET_LOCATION="./build.xml"

# Openssl for HTTPS
RUN apk add --update openssl

#ANT Environment
ENV ANT_VERSION="1.9.14"
ENV ANT_HOME="/opt/ant"
ENV PATH="${PATH}:${ANT_HOME}/bin"

# Install Apache ANT
RUN wget http://archive.apache.org/dist/ant/binaries/apache-ant-${ANT_VERSION}-bin.tar.gz \
    && tar -zvxf apache-ant-${ANT_VERSION}-bin.tar.gz -C /opt/ \
    && ln -s /opt/apache-ant-${ANT_VERSION} ${ANT_HOME} \
    && rm -f apache-ant-${ANT_VERSION}-bin.tar.gz 

# Execute Build
CMD if [ "${BUILD_SOURCE}" == "local" ]; then \
    	echo "Building DietStory from mountpoint..." \
      	&& cd mnt \
      	&& ant -f ${TARGET_LOCATION} -Dplatforms.JDK_1.7.home=${JAVA_HOME} -Dnb.internal.action.name=rebuild clean jar; \
    elif [ "${BUILD_SOURCE}" == "repository" ]; then \
    	echo "Building DietStory from remote ${BUILD_REPO_REMOTE}/${BUILD_BRANCH}..." \
    	&& wget ${BUILD_REPO_REMOTE}/${BUILD_BRANCH}.zip \
    	&& mkdir /tmp/build && unzip -o -d /tmp/build ${BUILD_BRANCH}.zip \
    	&& cd /tmp/build/MapleSolaxiaV2-${BUILD_BRANCH} \
    	&& ant -f ${TARGET_LOCATION} -Dplatforms.JDK_1.7.home=${JAVA_HOME} -Dnb.internal.action.name=rebuild clean jar \
    	&& cp -r dist /mnt && cp -r cores /mnt && cp -r build /mnt; \
    else \
      	echo "Error: Unrecognized command..."; \
    fi
