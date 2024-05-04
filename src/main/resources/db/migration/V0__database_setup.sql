CREATE TABLE twitch_streams(
    id SERIAL PRIMARY KEY,
    name VARCHAR(100)
);

CREATE TABLE discord_users(
    discord_id BIGINT PRIMARY KEY,
    name VARCHAR(100)
);

CREATE TABLE twitch_streams_discord_users_rel(
    twitch_id INT,
    discord_id BIGINT,
    recorder_enabled boolean,
    stream_prediction_enabled boolean,
    CONSTRAINT fk_twitch_streams FOREIGN KEY(twitch_id)
        REFERENCES twitch_streams(id),
    CONSTRAINT fk_discord_users FOREIGN KEY(discord_id)
        REFERENCES discord_users(discord_id)
);
