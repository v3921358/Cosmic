-- Missing drops
USE dietstory;

-- Cornians | Busted Dagger (Rarely Cornian's Dagger) | Horntail Pre-Quest
INSERT IGNORE INTO drop_data (`dropperid`, `itemid`, `chance`, `questid`)
VALUES
(8150200, 4001079, 50000, 7301),
(8150201, 4001079, 50000, 7301),
(8150200, 4001078, 500, 7301),
(8150201, 4001078, 500, 7301);

-- Fire Raccoons | Whatever Flaming Raccoons (9400010) are supposed to drop
INSERT IGNORE INTO drop_data (`dropperid`, `itemid`, `minimum_quantity`, `maximum_quantity`, `chance`) 
SELECT 9400001, `itemid`, `minimum_quantity`, `maximum_quantity`, `chance` FROM drop_data WHERE `dropperid`=9400010;

-- (Annoyed) Zombie Mushrooms | Annoyed Zombie Mushroom Doll | Your Fourth Informant Assignment (Aran only)
INSERT IGNORE INTO drop_data (`dropperid`, `itemid`, `chance`, `questid`)
VALUES
(2230101, 4032321, 7000, 21727),
(2230131, 4032321, 7000, 21727),
(9300238, 4032321, 7000, 21727),
(9300245, 4032321, 7000, 21727),
(9101001, 4032321, 7000, 21727);

-- Puppeteer | Puppeteer Document | Eliminate the Puppeteer! (21731) (Aran only)
INSERT IGNORE INTO drop_data (`dropperid`, `itemid`, `chance`, `questid`)
VALUES (9300344, 4032322, 999999, 21731);

-- Giant Nependeath | Rapid Growth Stimulator | The Giant Nependeaths Appear (21737)
INSERT IGNORE INTO drop_data (`dropperid`, `itemid`, `chance`, `questid`)
VALUES (9300378, 4032324, 50000, 21737);

-- Primitive Boar | Wooden Key | Find the Key (21752)
INSERT IGNORE INTO drop_data  (`dropperid`, `itemid`, `chance`, `questid`)
VALUES (5250002, 4032326, 50000, 21752);