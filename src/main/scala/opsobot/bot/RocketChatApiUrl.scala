package opsobot.bot

trait RocketChatApiUrl {
  val HOST: String
  val API_PATH: String = "/api/v1"
  val CORE_URL: String = s"$HOST$API_PATH"

  val ROOM_ID: String

  def SEND_MESSAGE = s"$CORE_URL/chat.sendMessage"
  def ROOMS_INFO = s"$CORE_URL/rooms.info?roomId=$ROOM_ID"
}


