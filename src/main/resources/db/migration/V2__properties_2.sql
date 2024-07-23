INSERT INTO properties (property_name, property_value)
VALUES
('VOD_RESOLUTION', 'Enter quality you want vods to be recoded in. Adhere to StreamLink rules. E.g. 720p,480p,best'),
('MESSAGE_DELAY', 'Delay in minutes before notification from same channel can be sent again.'),
('SPACE_THRESHOLD', 'Minimum available space in GB to record new vod. If space is insufficient old vods will be deleted.'),
('DISCORD_SHUTDOWN_DELAY', 'Duration before discord bot shutdowns. ISO-8601 Format')
