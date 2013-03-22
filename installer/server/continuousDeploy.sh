apt-get -y install openjdk-7-jdk
apt-get -y install ant

cp sneerServerBoot.sh /etc/init.d/
chmod +x /etc/init.d/sneerServerBoot.sh
update-rc.d sneerServerBoot.sh defaults 80

cd ~

apt-get -y install junit4
git clone git://github.com/klauswuestefeld/simploy.git
cd simploy
javac -cp .:/usr/share/java/junit4.jar Simploy.java

apt-get install webfs
sed -i "s#web_root=\"/srv/ftp\"#web_root=\"/root/sneer/installer/build\"#" /etc/webfsd.conf
sed -i "s#web_port=\"\"#web_port=\"80\"#" /etc/webfsd.conf
sed -i "s#web_user=\"www-data\"#web_user=\"root\"#" /etc/webfsd.conf
sed -i "s#web_group=\"www-data\"#web_group=\"root\"#" /etc/webfsd.conf
service webfs restart

service sneerServerBoot.sh start
