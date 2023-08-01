# Setting up JCE Java Cryptographic Extensions

With the discontinuation of the Spring-cloud cli the end points left open via [M] config-server/config/SecurityConfig
can be used to encrypt/decrypt passwords.

The Encrypt key needs to be set up as a system environment variable, the key for this
can be found in cloud-config-server-repository2024_07_21 in memos. The git key for the
config-server-repository will run out on that date.

export ENCRYPT_KEY='adfasdfasdf'
add this line to ~/.zshenv for permanence
echo "$ENCRYPT_KEY" to check

Once the ENCRYPT_KEY env var is set the post_decrypt/encrypt endpoints can be used.

--------------------------------





┌──(kali㉿kaliPerm)-[~]
└─$ curl -s "https://get.sdkman.io" | bash

                                -+syyyyyyys:
                            `/yho:`       -yd.
                         `/yh/`             +m.
                       .oho.                 hy                          .`
                     .sh/`                   :N`                `-/o`  `+dyyo:.
                   .yh:`                     `M-          `-/osysoym  :hs` `-+sys:      hhyssssssssy+
                 .sh:`                       `N:          ms/-``  yy.yh-      -hy.    `.N-````````+N.
               `od/`                         `N-       -/oM-      ddd+`     `sd:     hNNm        -N:
              :do`                           .M.       dMMM-     `ms.      /d+`     `NMMs       `do
            .yy-                             :N`    ```mMMM.      -      -hy.       /MMM:       yh
          `+d+`           `:/oo/`       `-/osyh/ossssssdNMM`           .sh:         yMMN`      /m.
         -dh-           :ymNMMMMy  `-/shmNm-`:N/-.``   `.sN            /N-         `NMMy      .m/
       `oNs`          -hysosmMMMMydmNmds+-.:ohm           :             sd`        :MMM/      yy
      .hN+           /d:    -MMMmhs/-.`   .MMMh   .ss+-                 `yy`       sMMN`     :N.
     :mN/           `N/     `o/-`         :MMMo   +MMMN-         .`      `ds       mMMh      do
    /NN/            `N+....--:/+oooosooo+:sMMM:   hMMMM:        `my       .m+     -MMM+     :N.

/NMo -+ooooo+/:-....`...:+hNMN.  `NMMMd`        .MM/ -m:    oMMN. hs
-NMd`                                    :mm -MMMm- .s/ -MMm. /m- mMMd -N.
`mMM/ .- /MMh. -dMo -MMMy        od. .MMMs..---yh
+MMM. sNo`.sNMM+     :MMMM/ sh`+MMMNmNm+++-
mMMM- /--ohmMMM+     :MMMMm.       `hyymmmdddo
MMMMh.                  ````                  `-+yy/`yMMM/     :MMMMMy -sm:.``..-:-.`
dMMMMmo-.``````..-:/osyhddddho.           `+shdh+. hMMM:     :MmMMMM/ ./yy/` `:sys+/+sh/
.dMMMMMMmdddddmmNMMMNNNNNMMMMMs           sNdo- dMMM-  `-/yd/MMMMm-:sy+.   :hs- /N`
`/ymNNNNNNNmmdys+/::----/dMMm:          +m- mMMM+ohmo/.` sMMMMdo- .om:       `sh
`.-----+/.`       `.-+hh/`         `od. NMMNmds/     `mmy:`     +mMy      `:yy.
/moyso+//+ossso:. .yy`          `dy+:`         ..       :MMMN+---/oys:
/+m:  `.-:::-`               /d+ +MMMMMMMNh:`
+MN/ -yh.                                     `+hddhy+.
/MM+ .sh:
:NMo -sh/
-NMs                    `/yy:
.NMy                  `:sh+.
`mMm`               ./yds-
`dMMMmyo:-.````.-:oymNy:`
+NMMMMMMMMMMMMMMMMms:`
-+shmNMMMNmdy+:`

                                                                 Now attempting installation...

Looking for a previous installation of SDKMAN...
Looking for unzip...
Looking for zip...
Looking for curl...
Looking for sed...
Installing SDKMAN scripts...
Create distribution directories...
Getting available candidates...
Prime platform file...
Prime the config file...
Installing script cli archive...

* Downloading...
  ######################################################################## 100.0%
* Checking archive integrity...
* Extracting archive...
* Copying archive contents...
* Cleaning up...

Installing script cli archive...

* Downloading...
  ######################################################################## 100.0%
* Checking archive integrity...
* Extracting archive...
* Copying archive contents...
* Cleaning up...

Set version to 5.18.2 ...
Set native version to 0.3.2 ...
Attempt update of interactive bash profile on regular UNIX...
Added sdkman init snippet to /home/kali/.bashrc
Attempt update of zsh profile...
Updated existing /home/kali/.zshrc

All done!

You are subscribed to the STABLE channel.

Please open a new terminal, or run the following in the existing one:

    source "/home/kali/.sdkman/bin/sdkman-init.sh"

Then issue the following command:

    sdk help

Enjoy!!!

┌──(kali㉿kaliPerm)-[~]
└─$ sdk version  
zsh: command not found: sdk

┌──(kali㉿kaliPerm)-[~]
└─$ source "$HOME/.sdkman/bin/sdkman-init.sh"

┌──(kali㉿kaliPerm)-[~]
└─$ sdk version

SDKMAN!
script: 5.18.2
native: 0.3.2

       ┌──(kali㉿kaliPerm)-[~/IdeaProjects/microservices-demo]

└─$ sdk install springboot

Downloading: springboot 3.1.2

In progress...

################################################################################################################################################################################################################################
100.0%

Installing: springboot 3.1.2
Done installing!

Setting springboot 3.1.2 as default.

```ignorelang
Check installed in ~/.sdkman/candidates/springboot

```

then check for latest version of:
<!-- https://mvnrepository.com/artifact/org.springframework.cloud/spring-cloud-cli -->
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-cli</artifactId>
    <version>3.1.1</version>
</dependency>

and install with:

```ignorelang

starting a nested shell:
spring shell

then:

spring install org.springframework.cloud:spring-cloud-cli:3.1.1
```