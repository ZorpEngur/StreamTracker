CREATE TABLE notification_platform(
    id INT PRIMARY KEY,
    name TEXT UNIQUE NOT NULL
);
INSERT INTO notification_platform VALUES (1, 'Discord');
ALTER TABLE twitch_streams_discord_users_rel RENAME TO twitch_streams_users_rel;
ALTER TABLE twitch_streams_users_rel DROP CONSTRAINT fk_discord_users;
ALTER TABLE twitch_streams_users_rel RENAME COLUMN discord_id TO user_id;
ALTER TABLE twitch_streams_users_rel ADD COLUMN platform_id INT NOT NULL DEFAULT 1;
ALTER TABLE discord_users RENAME TO users;
ALTER TABLE users
    DROP CONSTRAINT discord_users_pkey,
    ADD COLUMN id SERIAL NOT NULL,
    ADD COLUMN twitch_id BIGINT UNIQUE,
    ADD PRIMARY KEY(id),
    ALTER COLUMN discord_id DROP NOT NULL,
    ADD UNIQUE(discord_id),
    ADD UNIQUE(name);
UPDATE twitch_streams_users_rel SET user_id=id FROM users WHERE users.discord_id = twitch_streams_users_rel.user_id;
ALTER TABLE twitch_streams_users_rel
    ALTER COLUMN platform_id DROP DEFAULT,
    ALTER COLUMN twitch_id SET NOT NULL,
    ALTER COLUMN user_id SET NOT NULL,
    ALTER COLUMN recorder_enabled SET NOT NULL,
    ALTER COLUMN stream_prediction_enabled SET NOT NULL,
    ADD CONSTRAINT fk_users FOREIGN KEY(user_id) REFERENCES users(id),
    ADD CONSTRAINT fk_platform_id FOREIGN KEY(platform_id) REFERENCES notification_platform(id);
ALTER TABLE twitch_streams ALTER COLUMN name SET NOT NULL;
