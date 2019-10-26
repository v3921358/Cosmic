# Alpine Linux JDK7, JCE Policy Unlimited Strength 7
FROM anapsix/alpine-java:7_jdk

MAINTAINER Benjamin Zhao <yl4zhao@edu.uwaterloo.ca>

# Apply JCE Patch
RUN apk upgrade --update \
    && apk add --update curl unzip \
    && curl -jksSLH "Cookie: oraclelicense=accept-securebackup-cookie" -o /tmp/unlimited_jce_policy.zip "http://download.oracle.com/otn-pub/java/jce/7/UnlimitedJCEPolicyJDK7.zip" \
    && unzip -jo -d ${JAVA_HOME}/jre/lib/security /tmp/unlimited_jce_policy.zip \
    && unzip -jo -d ${JAVA_HOME}/lib /tmp/unlimited_jce_policy.zip \
    && apk del curl unzip \
    && rm -rf /tmp/* /var/cache/apk/*
