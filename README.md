#Remote Control for Mplayer
This application provides a web interface to remotely control mplayer.
With this you can easily create a Webradio which is controlled by a web browser

It should work on any linux driven device which has java and mplayer installed on it

__Disclaimer: Not tested, only available as nightly builds, no guarantee or whatsoever__

##Installation
1. Install [mplayer](https://wiki.ubuntuusers.de/MPlayer/)
2. Create a named pipe (aka fifo File) __mkfifo fifo__
3. Run __java -jar Radio3.jar -c=pathToPipe [-p=8080 optional web server port. Default: 80]__ #

###Run Radio on start

    //create Bash script
    #!/bin/bash
    nohup authbind --deep java -jar /home/radio/Radio3.jar c=/home/radio/fifoInput > /dev/null 2> /dev/null &     
      
    //Add script to crontab (crontab -e)
    @reboot pathtoscript

    //some usefull commands
	apt-get update
    apt-get install authbind mplayer nohub                        
    sudo touch /etc/authbind/byport/80
    sudo chown radioUser /etc/authbind/byport/80
    sudo chmod 755 /etc/authbind/byport/80                    

Thanks Bob for pointing that out
