INSERT INTO twitch_streams (name)
VALUES ('S1'), ('S2');

INSERT INTO discord_users (discord_id, name)
VALUES (453262634536816283, 'First User'), (393330073179957731, 'Second User'), (584386482042054737, 'Third User');;

INSERT INTO twitch_streams_discord_users_rel (twitch_id, discord_id, recorder_enabled, stream_prediction_enabled)
VALUES (1, 453262634536816283, false, false), (2, 453262634536816283, true, true),
       (2, 393330073179957731, true, false), (2, 393330073179957731, false, true);
