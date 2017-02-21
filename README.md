# WebRadio
Webradio for Raspberry Pi with Webinterface

##Installation
1. Install [mplayer](https://wiki.ubuntuusers.de/MPlayer/)
2. Create a named pipe (aka fifo File) __mkfifo fifo__
3. Run __java -jar Radio3.jar -c=pathToPipe__