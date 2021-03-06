﻿namespace GameServer.Controllers.MessageHandlers
{
    public static class MessageHelper
    {
        public static bool IsExtendedMessage(byte[] message)
        {
            return (message[3] & 2) == 2 && message.Length > 6;
        }

        public static bool IsReceivedOperatorMessage(byte[] message)
        {
            return message[4] == Commands.GET_OPERATOR && ((message[3] & 1) == 1);
        }

        public static bool IsRequestHighScore(byte[] message)
        {
            return message[4] == Commands.REQUEST_PERSIST_DATA;
        }
    }
}
