FROM --platform=linux/amd64 ubuntu:22.04 AS builder

RUN apt-get update && \
    apt-get install -y openjdk-17-jdk wget unzip make cmake libtool sqlite3 swig ant

# install android command-line tools, then android sdk & android ndk
ARG ANDROID_SDK_ROOT=/root/android_sdk
RUN mkdir $ANDROID_SDK_ROOT && \
    cd $ANDROID_SDK_ROOT && \
    wget https://dl.google.com/android/repository/commandlinetools-linux-11076708_latest.zip && \
    unzip commandlinetools-linux-11076708_latest.zip

RUN cd $ANDROID_SDK_ROOT && \
    yes | ./cmdline-tools/bin/sdkmanager --licenses --sdk_root=$ANDROID_SDK_ROOT > /dev/null && \
    ./cmdline-tools/bin/sdkmanager "build-tools;30.0.3" --sdk_root=$ANDROID_SDK_ROOT && \
    ./cmdline-tools/bin/sdkmanager "ndk;25.1.8937393" --sdk_root=$ANDROID_SDK_ROOT

ENV ANDROID_HOME=$ANDROID_SDK_ROOT
