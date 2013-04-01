echo ---- Linux Package Upgrades
aptitude -y upgrade
aptitude -y safe-update

echo ---- Timezone
# Choose your timezone: http://en.wikipedia.org/wiki/List_of_tz_database_time_zones
echo "America/Sao_Paulo" > /etc/timezone
dpkg-reconfigure -f noninteractive tzdata

apt-get -y install openjdk-7-jdk
apt-get -y install ant

cp sneerServerBoot.sh /etc/init.d/
chmod +x /etc/init.d/sneerServerBoot.sh
update-rc.d sneerServerBoot.sh defaults 80

apt-get -y install junit4
cd /root
git clone git://github.com/klauswuestefeld/simploy.git

apt-get install webfs
sed -i "s#web_root=\"/srv/ftp\"#web_root=\"/root/sneer/installer/webstart\"#" /etc/webfsd.conf
sed -i "s#web_port=\"\"#web_port=\"80\"#" /etc/webfsd.conf
sed -i "s#web_user=\"www-data\"#web_user=\"root\"#" /etc/webfsd.conf
sed -i "s#web_group=\"www-data\"#web_group=\"root\"#" /etc/webfsd.conf
service webfs restart

service sneerServerBoot.sh start
