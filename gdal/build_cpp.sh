#!/bin/bash

ANDROID_NDK=$1
MIN_SDK_VERSION=$2
JAVA_HOME=$3
BUILD_TYPE=$4 # Debug or Release
TOOLCHAIN=$ANDROID_NDK/toolchains/llvm/prebuilt/linux-x86_64

BUILD_THREADS=$(getconf _NPROCESSORS_ONLN)

# change the current directory
SOURCE_DIR=$(realpath "$(dirname $0)")/cpp

# build for different targets

function build_sqlite() {
  local TARGET=$1
  local ABI=$2
  local API=$3
  local BUILD_DIR=$4
  local INSTALL_DIR=$5
  local BUILD_THREADS=$6

  local SOURCE_DIR=$(pwd)

  cd $BUILD_DIR


  if [[ "${BUILD_TYPE,,}" == "release" ]]; then
    $SOURCE_DIR/configure --host=$TARGET --prefix=$INSTALL_DIR CFLAGS="-O3 -g0 -finline-functions" CPPFLAGS="-O3 -g0 -finline-functions"
  else
    $SOURCE_DIR/configure --host=$TARGET --prefix=$INSTALL_DIR CFLAGS="-O0 -g -fno-inline-functions" CPPFLAGS="-O0 -g -fno-inline-functions"
  fi

  make clean
  make -j$BUILD_THREADS
  make install
}

function build_proj() {
  local TARGET=$1
  local ABI=$2
  local API=$3
  local BUILD_DIR=$4
  local INSTALL_DIR=$5
  local BUILD_THREADS=$6

  # Build guide(9.2):https://github.com/OSGeo/PROJ/blob/9.2/docs/source/install.rst

  cmake -S . -B $BUILD_DIR \
        -DENABLE_TIFF=OFF -DENABLE_CURL=OFF -DBUILD_APPS=OFF -DBUILD_TESTING=OFF \
        -DCMAKE_INSTALL_PREFIX=$INSTALL_DIR \
        -DCMAKE_SYSTEM_NAME=Android \
        -DCMAKE_ANDROID_NDK=$ANDROID_NDK \
        -DCMAKE_ANDROID_ARCH_ABI=$ABI \
        -DCMAKE_SYSTEM_VERSION=$API \
         "-DCMAKE_PREFIX_PATH=$INSTALL_DIR;$TOOLCHAIN/sysroot/usr/" \
        -DCMAKE_FIND_ROOT_PATH_MODE_INCLUDE=NEVER \
        -DCMAKE_FIND_ROOT_PATH_MODE_LIBRARY=NEVER \
        -DCMAKE_FIND_USE_CMAKE_SYSTEM_PATH=NO \
        -DSFCGAL_CONFIG=disabled \
        -DHDF5_C_COMPILER_EXECUTABLE=disabled \
        -DHDF5_CXX_COMPILER_EXECUTABLE=disabled \
        -DEXE_SQLITE3=/usr/bin/sqlite3 \
        -DCMAKE_BUILD_TYPE=${BUILD_TYPE}

  cmake --build $BUILD_DIR --parallel $BUILD_THREADS --target install
}


function build_expat() {
  local TARGET=$1
  local ABI=$2
  local API=$3
  local BUILD_DIR=$4
  local INSTALL_DIR=$5
  local BUILD_THREADS=$6

  local SOURCE_DIR=$(pwd)

  cd $BUILD_DIR

  if [[ "${BUILD_TYPE,,}" == "release" ]]; then
    $SOURCE_DIR/configure --host=$TARGET --prefix=$INSTALL_DIR CFLAGS="-O3 -g0 -finline-functions" CXXFLAGS="-O3 -g0 -finline-functions"
  else
    $SOURCE_DIR/configure --host=$TARGET --prefix=$INSTALL_DIR CFLAGS="-O0 -g -fno-inline-functions" CXXFLAGS="-O0 -g -fno-inline-functions"
  fi

  make clean
  make -j$BUILD_THREADS
  make install
}


function build_gdal() {
  local TARGET=$1
  local ABI=$2
  local API=$3
  local BUILD_DIR=$4
  local INSTALL_DIR=$5
  local BUILD_THREADS=$6

  echo "$INSTALL_DIR/lib/pkgconfig"

  PKG_CONFIG_LIBDIR=$INSTALL_DIR/lib/pkgconfig
  cmake -S . -B $BUILD_DIR \
   -DCMAKE_INSTALL_PREFIX=$INSTALL_DIR \
   -DCMAKE_SYSTEM_NAME=Android \
   -DCMAKE_ANDROID_NDK=$ANDROID_NDK \
   -DCMAKE_ANDROID_ARCH_ABI=$ABI \
   -DCMAKE_SYSTEM_VERSION=$API \
   "-DCMAKE_PREFIX_PATH=$INSTALL_DIR;$TOOLCHAIN/sysroot/usr/" \
   -DCMAKE_FIND_ROOT_PATH_MODE_INCLUDE=NEVER \
   -DCMAKE_FIND_ROOT_PATH_MODE_LIBRARY=NEVER \
   -DCMAKE_FIND_USE_CMAKE_SYSTEM_PATH=NO \
   -DSFCGAL_CONFIG=disabled \
   -DHDF5_C_COMPILER_EXECUTABLE=disabled \
   -DHDF5_CXX_COMPILER_EXECUTABLE=disabled \
   -DGDAL_BUILD_OPTIONAL_DRIVERS=OFF \
   -DOGR_BUILD_OPTIONAL_DRIVERS=OFF \
   -DGDAL_USE_EXTERNAL_LIBS=OFF \
   -DGDAL_USE_SQLITE3=ON \
   -DGDAL_USE_EXPAT=ON \
   -DBUILD_JAVA_BINDINGS=ON \
   -DBUILD_PYTHON_BINDINGS=OFF \
   -DBUILD_CSHARP_BINDINGS=OFF \
   -DCMAKE_BUILD_TYPE=${BUILD_TYPE}


  cmake --build $BUILD_DIR --parallel $BUILD_THREADS --target install
}

function build_for_target() {
    local TARGET=$1
    local ABI=$2
    local API=$3

    echo "############################ Build for $TARGET: $BUILD_TYPE ###############################"

    mkdir -p $SOURCE_DIR
    cd $SOURCE_DIR

    # download file if necessary
    local SQLITE=sqlite-autoconf-3420000
    local PROJ=proj-9.2.1
    local GDAL=gdal-3.7.0
    local EXPAT=expat-2.5.0
    local SQLITE_TARBALL=$SQLITE.tar.gz
    local PROJ_TARBALL=$PROJ.tar.gz
    local GDAL_TARBALL=$GDAL.tar.gz
    local EXPAT_TARBALL=$EXPAT.tar.gz

    if [ ! -f  "$SQLITE_TARBALL" ]; then
      wget https://www.sqlite.org/2023/sqlite-autoconf-3420000.tar.gz -O $SQLITE_TARBALL
    fi
    if [ ! -f "$PROJ_TARBALL" ]; then
      wget https://github.com/OSGeo/PROJ/releases/download/9.2.1/proj-9.2.1.tar.gz -O $PROJ_TARBALL
    fi
    if [ ! -f "$GDAL_TARBALL" ]; then
      wget https://github.com/OSGeo/gdal/releases/download/v3.7.0/gdal-3.7.0.tar.gz -O $GDAL_TARBALL
    fi
    if [ ! -f "$EXPAT_TARBALL" ]; then
      wget https://github.com/libexpat/libexpat/releases/download/R_2_5_0/expat-2.5.0.tar.gz
    fi

    local SQLITE_SOURCE_DIR=$SOURCE_DIR/$SQLITE
    local PROJ_SOURCE_DIR=$SOURCE_DIR/$PROJ
    local GDAL_SOURCE_DIR=$SOURCE_DIR/$GDAL
    local EXPAT_SOURCE_DIR=$SOURCE_DIR/$EXPAT

    rm -rf $SQLITE_SOURCE_DIR $PROJ_SOURCE_DIR $GDAL_SOURCE_DIR $EXPAT_SOURCE_DIR

    tar -xzf $SQLITE_TARBALL
    tar -xzf $PROJ_TARBALL
    tar -xzf $GDAL_TARBALL
    tar -xzf $EXPAT_TARBALL

    # prepare cross compile environment
    export  AR=$TOOLCHAIN/bin/llvm-ar
    export  CC=$TOOLCHAIN/bin/$TARGET$API-clang
    export  AS=$CC
    export  CXX=$TOOLCHAIN/bin/$TARGET$API-clang++
    export  LD=$TOOLCHAIN/bin/ld
    export  RANLIB=$TOOLCHAIN/bin/llvm-ranlib
    export  STRIP=$TOOLCHAIN/bin/llvm-strip
    export JAVA_HOME=$JAVA_HOME

    local BUILD_DIR=$SOURCE_DIR/.build/$TARGET
    local INSTALL_DIR=$SOURCE_DIR/.install/$TARGET

    rm -rf $BUILD_DIR $INSTALL_DIR
    mkdir -p $BUILD_DIR $INSTALL_DIR

    local SQLITE_BUILD_DIR=$BUILD_DIR/sqlite
    local SQLITE_INSTALL_DIR=$INSTALL_DIR
    mkdir -p $SQLITE_BUILD_DIR $SQLITE_INSTALL_DIR
    cd $SQLITE_SOURCE_DIR
    build_sqlite $TARGET $ABI $API $SQLITE_BUILD_DIR $SQLITE_INSTALL_DIR $BUILD_THREADS

    local EXPAT_BUILD_DIR=$BUILD_DIR/sqlite
    local EXPAT_INSTALL_DIR=$INSTALL_DIR
    mkdir -p $EXPAT_BUILD_DIR $EXPAT_INSTALL_DIR
    cd $EXPAT_SOURCE_DIR
    build_expat $TARGET $ABI $API $EXPAT_BUILD_DIR $EXPAT_INSTALL_DIR $BUILD_THREADS

    local PROJ_BUILD_DIR=$BUILD_DIR/proj
    local PROJ_INSTALL_DIR=$INSTALL_DIR
    mkdir -p $PROJ_BUILD_DIR $PROJ_INSTALL_DIR
    cd $PROJ_SOURCE_DIR
    build_proj $TARGET $ABI $API $PROJ_BUILD_DIR $PROJ_INSTALL_DIR $BUILD_THREADS

    local GDAL_BUILD_DIR=$BUILD_DIR/gdal
    local GDAL_INSTALL_DIR=$INSTALL_DIR
    mkdir -p $GDAL_BUILD_DIR $GDAL_INSTALL_DIR
    cd $GDAL_SOURCE_DIR
    build_gdal $TARGET $ABI $API $GDAL_BUILD_DIR $GDAL_INSTALL_DIR $BUILD_THREADS

    # copy output files to destination directories
    local ABI_JNI_DIR=$SOURCE_DIR/../src/main/jniLibs/$ABI
    rm -rf $ABI_JNI_DIR
    mkdir -p $ABI_JNI_DIR
    cp $INSTALL_DIR/lib/*.so $ABI_JNI_DIR

    cp $INSTALL_DIR/share/java/*.so $ABI_JNI_DIR

    local LIBS_DIR=$SOURCE_DIR/../libs
    mkdir -p $LIBS_DIR
    rm -rf $LIBS_DIR/*
    cp $INSTALL_DIR/share/java/$GDAL.jar $LIBS_DIR
}

build_for_target "x86_64-linux-android" "x86_64" 21
build_for_target "aarch64-linux-android" "arm64-v8a" 21