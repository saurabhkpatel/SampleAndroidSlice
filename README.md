# Android Slices Sample Application

by [Saurabh Patel](https://medium.com/@saurabhkpatel)

## Overview
Slices are templated views that have the ability to show and interact with content from an app from Google Search and later, places like the Google Assistant. 

It's just a sample android applciation which demonstrate the usage of different types of Android Slices. Please go through the below articles for the detailed information about Slices.

- [Android Jetpack: Android Slices Introduction Part-1 🍰](https://medium.com/@saurabhkpatel/android-jetpack-android-slices-introduction-cf0ce0f3e885)
- [Android Jetpack: Android Slices Part-2 🍰](https://medium.com/@saurabhkpatel/android-jetpack-android-slices-part-2-688bede57f2d)

## Prerequisites

- AndroidX refactoring tool: required if you're working in a project that uses AndroidX libraries.
- Slices lint checks: catches common anti-practices when building Slices
- SliceProvider template: handles the boilerplate when building a SliceProvider

## Notes
- This Sample application is using Kotlin.
- Using Slice Builders KTX lib, not using the core slice builders lib.

## How to test/display Slices.
- You need SliceViewer tool installed on your device.
- Download and install the SliceViewer tool APK, download it from [here](https://github.com/googlesamples/android-SliceViewer/releases) and install it using the below command. SliceViewer tool displays the slices based on the content URI and you change the mode of slice as well.

`adb install -r -t slice-viewer.apk`

- Install your android application via command line or Android Studio whichever you prefer. 
- Now, Run the following command to display the Slice. It’s the normal command to start the action intent with data URI. After running this command you can see your slice on SliceViewer screen. If it’s asking for the permission then give it because slice viewer tool requests the necessary permission to your application’s slice provider.

```
adb shell am start -a android.intent.action.VIEW -d slice-<content_uri_name>
for eg. "content://com.saurabh.androidslices/basicRowSlice"
```


## Slices in Action

![Slice Click Action](https://github.com/saurabhkpatel/SampleAndroidSlice/blob/master/screenshots/SliceAction.gif)

![Slice Click Action](https://github.com/saurabhkpatel/SampleAndroidSlice/blob/master/screenshots/GridRowSlices.jpg)

![Slice Click Action](https://github.com/saurabhkpatel/SampleAndroidSlice/blob/master/screenshots/DelayContentSlice.gif)

![Slice Click Action](https://github.com/saurabhkpatel/SampleAndroidSlice/blob/master/screenshots/InputRangeSlice.png)


[-Saurabh Patel](https://medium.com/@saurabhkpatel)

