cd /root/sneer/installer
java -XX:MaxPermSize=550m -cp /root/simploy/:/usr/share/java/junit4.jar Simploy "ant compile" build/tmp/bin/  build/tmp/bin/ "ant deploy" senhamalucaqq&

ant compile
mkdir -p /root/stun/code
cp -rf build/tmp/bin /root/stun/code
cd /root/stun/code/bin
java -cp . sneer.main.stun.StunServerSession&
