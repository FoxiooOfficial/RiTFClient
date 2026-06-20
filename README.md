# RiTF Client (under construction)

RiTF is a Client-Host application that allows you to get photos/videos with tags that can be displayed on old Android devices.

- RiTF (Client) is a lightweight Android app (requiring API 8+) written in Java 7 and the raw SDK (without Android Studio) that sends unencrypted HTTP GET requests to the Host to retrieve data from specific tags (as on Booru-style sites).
- RiTF (Host) is an HTTP server that stores all photos and videos that the Client can search for using tags (it can be modified to use third-party APIs instead of a local database)

It is not recommended to place the Host on a public network. Communication between the Client and the Host uses unencrypted HTTP (for compatibility reasons), so it is possible that someone could intercept the Client’s requests or photos sent by the Host. Use a LAN connection instead.

<img src="https://github.com/FoxiooOfficial/RiTFClient/blob/main/readme/screen0.png" alt="Main page">

# Project Requirements:
*RiTF requires an older Android/Java environment and is not compatible with newer versions.*

**Operating system**
- Windows 11 *(Recommended because the project was developed on it)*

**Java**
- [JDK 1.7.0](https://www.oracle.com/java/technologies/javase/javase7-archive-downloads.html)

**Apache Ant**
- [Apache Ant 1.9.16](https://downloads.apache.org/ant/binaries/)

**Android SDK**
- Android SDK API Level 8 (Android 2.2)
- Android SDK Build-tools 19.1
- ADB (Android Debug Bridge)

**RiTF**
- [Host Server](https://github.com/FoxiooOfficial/RiTFHost)
