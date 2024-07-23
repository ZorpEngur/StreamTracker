UPDATE properties
SET property_value = '720p,480p,best'
WHERE property_name = 'VOD_RESOLUTION';

UPDATE properties
SET property_value = '1'
WHERE property_name = 'MESSAGE_DELAY';

UPDATE properties
SET property_value = '10'
WHERE property_name = 'SPACE_THRESHOLD';

UPDATE properties
SET property_value = 'PT10M'
WHERE property_name = 'DISCORD_SHUTDOWN_DELAY';
