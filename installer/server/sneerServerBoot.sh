cd /root/sneer/installer/build
python -m SimpleHTTPServer 80 &
cd /root/sneer/installer
java -XX:MaxPermSize=550m -cp /root/simploy/:/usr/share/java/junit4.jar Simploy "ant compile" build/tmp/bin/  build/tmp/bin/ "ant deploy" senhamalucaqq
