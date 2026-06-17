# script for bulding app :3

$env:PATH = "C:\Program Files\Java\jdk1.7.0\bin;" + $env:PATH
$env:JAVA_HOME = "C:\Program Files\Java\jdk1.7.0"
ant.bat debug

adb uninstall com.foxioo.ritfclient
adb install .\bin\RiTFClient-debug.apk
adb shell am start -n com.foxioo.ritfclient/com.foxioo.ritfclient.MainActivity
