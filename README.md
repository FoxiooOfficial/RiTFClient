# RiTF Client (under construction)

RiTF is a Client-Host application that allows you to get photos/videos with tags that can be displayed on old Android devices.

- RiTF (Client) is a lightweight Android app (requiring API 8+) written in Java 7 and the raw SDK (without Android Studio) that sends unencrypted HTTP GET requests to the Host to retrieve data from specific tags (as on Booru-style sites).
- RiTF (Host) is an HTTP server that stores all photos and videos that the Client can search for using tags (it can be modified to use third-party APIs instead of a local database)

It is not recommended to place the Host on a public network. Communication between the Client and the Host uses unencrypted HTTP (for compatibility reasons), so it is possible that someone could intercept the Client’s requests or photos sent by the Host. Use a LAN connection instead.
