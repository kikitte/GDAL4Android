# GDAL4Android

This project builds GDAL into an [Android Archive(AAR)](https://developer.android.com/studio/projects/android-library) file. So you can use GDAL's functionality in your Android App.

Version Info: GDAL 3.7.0, PROJ 9.2.1, SQLITE 3.42.0, EXPAT 2.5.0(used for kml support)

[DOWNLOAD AAR file](https://github.com/kikitte/GDAL4Android/releases)

### Building Requirements

- Linux

  bash

  some utilities: getconf & make & cmake & libtool & ant & ...

  swig: for building gdal java bindings.

- Android Studio 2022.2 or newer

  with latest ndk installed, r25c or newer

  use Android Studio default JDK as Gradle JDK (specified in Gradle settings)

You may encounter problems caused by development environment, if something is missing, just install it.

### Building Processes

Just select gdal module in the project panel and make it from the build menu. The output arr file is located at gdal/build/outputs/aar/gdal-release.aar.

![make_gdal_module](./screenshots/make_gdal_module.png)

You can also download the build version in the release page.

### Credit

https://github.com/OSGeo/gdal/blob/master/.github/workflows/android_cmake/start.sh

https://github.com/paamand/GDAL4Android
