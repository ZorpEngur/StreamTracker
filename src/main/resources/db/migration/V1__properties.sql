CREATE TABLE properties(
    property_name VARCHAR(100) PRIMARY KEY,
    property_value VARCHAR(100) NOT NULL
);

INSERT INTO properties (property_name, property_value)
VALUES
('BOT_FILE', 'Enter full file path where bot will store its data.'),
('TWITCH_NAME', 'Login credentials for twitch bot.'),
('TWITCH_TOKEN', 'Login credentials for twitch bot.'),
('DISCORD_TOKEN', 'Token for discord bot.'),
('MANAGE_CHANNEL_NAME', 'Name of channel from where users can be added.')
