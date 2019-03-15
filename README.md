# seng468

## Clone project
`git clone https://github.com/BenDHillier/SENG468/`

## Postgres needed (10.6):
`sudo apt install postgresql postgresql-contrib`
`sudo -i -u postgres`
`psql`
`postgres=# create database stocktraderdb;`
`postgres=# password` -> `postgres`

To run on lab machine add file ~/.m2/settings.xml with contents
```
<settings>
    <proxies>
        <proxy>
            <active>true</active>
            <protocol>http</protocol>
            <host>192.168.1.1</host>
            <port>3128</port>
        </proxy>
    </proxies>
</settings>
```

`mvn spring-boot:run -Dspring.profiles.active=prod -Dhttp.proxyHost=192.168.1.1 -Dhttp.proxyPort=3128`

## setup 
```
git clone https://github.com/BenDHillier/SENG468/
cd SENG468/StockTrader
git fetch
git checkout prod-stuff-ben
sudo docker swarm init
sudo docker stack deploy -c docker-compose.yml stock-trader
ifconfig
```
get ip address that starts with 192.168
