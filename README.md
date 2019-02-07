# seng468

To run on lab machine add file ~/.m2/settings.xml with contents
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
