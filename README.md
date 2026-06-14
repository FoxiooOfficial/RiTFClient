# RiTF (under construction)

**Note: Work on RiTF has just begun, so the GitHub repository is currently empty; files will be added soon!**

RiTF is a Client-Host application that allows you to get photos/videos with tags that can be displayed on old Android devices.

- RiTF (Client) is a lightweight Android app (requiring API 8+) written in Java 7 and the raw SDK (without Android Studio) that sends unencrypted HTTP GET requests to the Host to retrieve data from specific tags (as on Booru-style sites).
- RiTF (Host) consists of Python scripts running on a computer that acts as an intermediary between the Client and the endpoint; its purpose is to translate API requests so they are understandable to the Client by sending JSON; it can also compress photos and videos.

The project is primarily designed so that the connection between the Client and the Host is over a LAN.