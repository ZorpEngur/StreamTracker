# Stream Tracker
This bot allows you to get notified when a stream goes live and automatically record the stream.\
Currently supports Twitch for streaming platform and Discord for sending notifications.

## Setup
To run the bot you will need Java, [Streamlink](https://streamlink.github.io/) and FFmpeg (is installed with Streamlink).

Then you will need to set up postgres database.\
Easy way is to install [Docker](https://www.docker.com/products/docker-desktop/).
* Then you run: `docker pull postgres:latest`
* Then: `docker run --name my-postgres -e POSTGRES_PASSWORD=admin -e POSTGRES_USER=admin -e POSTGRES_DB=db --publish 5432:5432 --detach postgres`
* After this your properties would be:
  * `database.url=jdbc:postgresql://localhost:5432/db`
  * `database.user=admin`
  * `database.password=admin`

To get the `TwitchBot.jar` file, you can either download compiled artifact from GitHub or compile the code yourself with Maven.\
Start the bot with command: `java -jar {path to the bot}/TwitchBot.jar --spring.config.additional-location={path to properties}/application.properties`

You will also need to create file with properties. It is possible to use any format supported by Spring Boot.\
Create `application.properties` file, where you put:
```
database.url=jdbc:postgresql:{url of the database}
database.user={name of the user to access the database}
database.password={password to he database}

stream.tracker.twitchName={name of the bot on twitch; see https://dev.twitch.tv/docs/authentication/register-app/}
stream.tracker.twitchToken={authorization token of the bot; use https://twitchtokengenerator.com/}
stream.tracker.discordToken={token of a discord bot; create new application here https://discord.com/developers/applications}
```

Optional properties, you don't need to set them, they either have default values or are not needed:
```
database.driver={database driver; uses postgres driver and probably thats the only one functioning}

stream.tracker.filePath={full directory path where the vods and logs are saved; default is your home}
stream.tracker.manageChannel={twitch channel from where you can add new streams to follow (currently not maintained and might not work properly)}
stream.tracker.vodResolution={resolution of stream, in which the vod is recorded; e.g. 1080p,best}
stream.tracker.messageDelay={delay between repeated messages for same stream; e.g. PT10M}
stream.tracker.spaceThreshold={space on disk in GB that is required for recoding streams; if there is not enough space bot will delete old vods}
stream.tracker.discordShutdownDelay={delay after which discord bot is shut down after sending message}
```

After running the bot, database will get initialized and you will need to insert for who to get notifications.\
With SQL:
```
INSERT INTO stream_tracker.discord_users VALUES ({your discord id; see https://support.discord.com/hc/en-us/articles/206346498-Where-can-I-find-my-User-Server-Message-ID}, '{your name, is just used for logs}');
INSERT INTO stream_tracker.twitch_streams VALUES (1, '{name of the stream}');
INSERT INTO stream_tracker.twitch_streams_discord_users_rel VALUES (1, {your discord id}, {true/false; if you want to record stream}, {true/false; if you want to get predictive notifications(experimental)});
```
With command: not working at the moment.