package com.zorpengur.notification;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Main {

    public static void main(String[] args) {
        TwitchLiveBot twitchLiveBot = null;
        while (true) {
            try {
                twitchLiveBot = new TwitchLiveBot(System.getenv().get("BOTFILE") + "streamNotifications.txt");
                twitchLiveBot.startBot();
                twitchLiveBot.registerFeatures();
                break;
            } catch (Exception ex) {
                log.error("Bot live error", ex);
                if (twitchLiveBot != null) {
                    twitchLiveBot.destroy();
                }
            }
        }

        TwitchExpandBot twitchExpandBot = null;
        while (true) {
            try {
                twitchExpandBot = new TwitchExpandBot(twitchLiveBot, "zorpengur", System.getenv().get("BOTFILE") + "streamNotifications.txt");
                twitchExpandBot.startBot();
                twitchExpandBot.registerFeatures();
                break;
            } catch (Exception ex) {
                log.error("Expand bot exception", ex);
                if (twitchExpandBot != null) {
                    twitchExpandBot.destroy();
                }
            }
        }

        log.debug("---------- Bot Running ----------");
    }
}