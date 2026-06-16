ALTER TABLE twitch_streams_users_rel ADD COLUMN title_change_enabled boolean NOT NULL DEFAULT TRUE;
ALTER TABLE twitch_streams_users_rel ADD COLUMN game_change_enabled boolean NOT NULL DEFAULT TRUE;

ALTER TABLE twitch_streams_users_rel ALTER COLUMN title_change_enabled DROP DEFAULT;
ALTER TABLE twitch_streams_users_rel ALTER COLUMN game_change_enabled DROP DEFAULT;
