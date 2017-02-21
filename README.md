#Remote Control for Mplayer
This application provides a web interface to remotely control mplayer.
With this you can easily create a Webradio which is controlled by a web browser

It should work on any linux driven device which has java and mplayer installed on it

__Disclaimer: Not tested, only available as nightly builds, no guarantee or whatsoever__

##Installation
1. Install [mplayer](https://wiki.ubuntuusers.de/MPlayer/)
2. Create a named pipe (aka fifo File) __mkfifo fifo__
3. Run __java -jar Radio3.jar -c=pathToPipe__