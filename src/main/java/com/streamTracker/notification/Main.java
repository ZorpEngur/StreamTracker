package com.streamTracker.notification;

import lombok.extern.slf4j.Slf4j;
import org.flywaydb.core.Flyway;

@Slf4j
public class Main {

    public static void main(String[] args) {
        databaseMigration();

        TwitchLiveBot twitchLiveBot = null;
        while (true) {
            try {
                twitchLiveBot = new TwitchLiveBot();
                twitchLiveBot.startBot();
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
                twitchExpandBot = new TwitchExpandBot(twitchLiveBot);
                twitchExpandBot.startBot();
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

    private static void databaseMigration() {
        Flyway flyway = Flyway.configure()
            .defaultSchema("stream_tracker")
            .driver("org.postgresql.Driver")
            .loggers("slf4j")
            .dataSource(System.getenv().get("DB_URL"), System.getenv().get("DB_USER"), System.getenv().get("DB_PASSWORD")).load();
        if (flyway.migrate().initialSchemaVersion == null) {
            throw new RuntimeException("Now you need to set up properties in database");
        }
    }
}
