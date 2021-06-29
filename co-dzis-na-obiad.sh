#!/bin/bash

#	 			SETTINGS
# 				--------

interval="+15 sec"
filename="src/main/resources/prezentacja.conf"
rocketchatURL="localhost:3000/channel/menu-na-dzis"


#	 			Get cronExp
# 				-----------
secondMinuteHour=$(date --date="$interval" +"%S %M %H") 
cronExp="$secondMinuteHour ? * * *"


#	 			Save configuration file
# 				-----------------------
rm "$filename" 2> /dev/null

# write lines with configuration to prezentacja.conf
cat << EOF > $filename
opsobot {
  credentials {
    token = "50odCBxP9sJFkm8ZpQiL_0MP3S1YFc5cwRtvJ4qZa13"
    user_id = "PHP5o4Aq7XDmDMMhq"
    avatar = "https://avatars.slack-edge.com/2020-08-07/1281096213222_ad3d6fc601b6e272eb7e_512.png"
  }
  environment {
    host = "https://chat.czk.comarch.com"
    room_id = "mj2b2pds8Laht8nac"
  }
}

akka.quartz {
  defaultTimezone = "GMT+2"
  schedules {
    MondaysAndWednesdays {
      expression = "* 18 13 ? * * 2021"
      description = "Next year"
    }
    PizzaDays {
      expression = "$cronExp"
      description = "With $interval interval from now"
    }
  }
}
EOF

#	 			Run opsobot
# 				-----------
#firefox $rocketchatURL &
sbt run -Dconfig.resource=prezentacja.conf

