# Stream Tracker
This bot allows you to get notified when a stream goes live and automatically record the stream.\
Currently supports Twitch for streaming platform and Discord for sending notifications.

## Setup
To run the bot you will need Java, [Streamlink](https://streamlink.github.io/) and FFmpeg (is installed with Streamlink).

Then you will need to set up postgres database.\
Easy way is to install [Docker](https://www.docker.com/products/docker-desktop/).
* Then you run: `docker pull postgres:latest`
* Then: `docker run --name my-postgres -e POSTGRES_PASSWORD=admin -e POSTGRES_USER=admin -e POSTGRES_DB=db --publish 5432:5432 --detach postgres`

To get the `TwitchBot.jar` file, you can either download compiled artifact from GitHub or compile the code yourself with Maven.\
Start the bot with command: `java -jar {path to the bot}/TwitchBot.jar --spring.config.additional-location={path to properties}/application.properties`

You will also need to create file with properties. It is possible to use any format supported by Spring Boot.\
Create `application.properties` file, where you put:
```properties
stream.tracker.twitchName={name of the bot on twitch; see https://dev.twitch.tv/docs/authentication/register-app/}
stream.tracker.twitchToken={authorization token of the bot; use https://twitchtokengenerator.com/}
stream.tracker.discordToken={token of a discord bot; create new application here https://discord.com/developers/applications}
stream.tracker.manageChannel={your twitch name to get access to the bots commands}
```
Adn if you haven't used default database configuration provided earlier, add these properties with your values:
```properties
database.url=jdbc:postgresql:{url of the database}
database.user={name of the user to access the database}
database.password={password to the database}
```

Optional properties, you don't need to set them, they either have default values or are not needed:
```properties
stream.tracker.filePath={full directory path where the vods and logs are saved; default is your home}
stream.tracker.manageChannel={twitch channel from where you can add new streams to follow (currently not maintained and might not work properly)}
stream.tracker.vodResolution={resolution of stream, in which the vod is recorded; e.g. 1080p,best}
stream.tracker.messageDelay={delay between repeated messages for same stream; e.g. PT10M}
stream.tracker.spaceThreshold={space on disk in GB that is required for recoding streams; if there is not enough space bot will delete old vods}
stream.tracker.discordShutdownDelay={delay after which discord bot is shut down after sending message}
```

After running the bot, database will get initialized and you will need to insert for who to get notifications.\
The bot will join its own twitch channel, where user specified in `stream.tracker.manageChannel` property will be able to use its commands.
1. You need to register yourself: `register -discord:{your discord ID}`
    * For obtaining Discord ID see [this guide](https://support.discord.com/hc/en-us/articles/206346498-Where-can-I-find-my-User-Server-Message-ID#h_01HRSTXPS5H5D7JBY2QKKPVKNA).
    * Add parameter `-name:{your name for reference in other commands}` if you want to use different name than your twitch name for this user.
    * If you are registering different user, add parameter `-notme` to avoid linking your twitch ID with this user.
2. To set notification event use: `set -twitch:{name of the channel you want to be notifed for} -platform:{name of the platform where you will be notified}`
    * Currently supported platforms: `DISCORD`
    * If the user was registered with `-notme`, then add parameter `-user:{name of the usre you used in register command}`
    * If you want to record the stream, add parameter `-record`
    * If you want to be notified in advance of the stream (experimental feature) add parameter `-predict`

Parameters can be specified in any order, they just have to be separated by space.\
You can always use `help` or `help -{name of the command}` to get to the manual for the commands.
