-- Create the Actors table with required schema --

CREATE TABLE `Actors` (
`Name` VARCHAR(40),
`Movie` VARCHAR(80),
`Year` INTEGER,
`Role` VARCHAR(40)
);

-- Load the table with the actors.csv file --

LOAD DATA LOCAL INFILE '~/ebay-data/actors.csv' 
INTO TABLE `Actors`
FIELDS TERMINATED BY ',' OPTIONALLY ENCLOSED BY '"';

-- Query for all the actors in 'Die Another Day' --

SELECT `Name`
FROM `Actors`
WHERE `Movie` = 'Die Another Day';

-- Drop the Actors table --

DROP TABLE `Actors`;
