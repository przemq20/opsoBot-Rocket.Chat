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
    token = "HBaSBV-xSkzCBDc-PdaA66XUEgqkfqqfYyZcXVOyTwv"
    user_id = "378dGnBRqNvuwGtdW"
    avatar = "https://avatars.slack-edge.com/2020-08-07/1281096213222_ad3d6fc601b6e272eb7e_512.png"
  }
  environment {
    host = "http://localhost:3000"
    room_id = "ocGiwg5SsYpQ7nBL2"
  }
}

akka.quartz {
  defaultTimezone = "GMT+2"
  schedules {
    MondaysAndWednesdays {
      expression = "* * 10 ? * * 2021"
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
firefox $rocketchatURL &
sbt run -Dconfig.resource=prezentacja.conf

