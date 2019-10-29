USE `dietstory`;

INSERT IGNORE INTO drop_data (`dropperid`, `itemid`, `minimum_quantity`, `maximum_quantity`, `questid`, `chance`)
VALUES 
(3300003, 4001317, 1, 1, 2326, 5000), -- Helmet Pepe | Helmet Pepe's Helmet | James's Whereabouts (2)
(1210103, 4032137, 1, 1, 20711, 5000), -- Bubbling | Bubbling Doll | Fake Doll 4032139
(1210103, 4032139, 1, 1, 20713, 5000), -- Bubbling | Old Key | A Deal With Mr. Pickall
(1210101, 4001357, 1, 1, 28244, 7000), -- Ribbon Pig | Pork | Grendel the Really Tipsy
(1120100, 4001361, 1, 1, 28250, 7000), -- Octopus | Ink | The Mystery of the White Portion
(2230110, 4032146, 1, 1, 20722, 5000), -- Wooden Mask | Wooden Mask Doll | Wooden Mask
(2230111, 4032147, 1, 1, 20723, 5000); -- Rocky Mask | Rocky Mask Doll | Stone Mask

INSERT INTO reactordrops (`reactorid`, `itemid`, `chance`, `questid`)
VALUES (1012000, 4032143, 6, 20717); -- Plant | Fruit | The Plants are Suspicious!

-- There are two Jr. Boogies mob ids for some unknown reason. 3230301 had no drops, but 3230300 had all the correct drops.
-- Just copying the drops from the one with the correct drop data.
INSERT IGNORE INTO drop_data (`dropperid`, `itemid`, `minimum_quantity`, `maximum_quantity`, `questid`, `chance`)
SELECT 3230301, `itemid`, `minimum_quantity`, `maximum_quantity`, `questid`, `chance` FROM drop_data WHERE `dropperid` = 3230300;

-- Wrong quest id for 'Calming the Soul with Music' quest.
UPDATE drop_data SET `questid` = 28248 WHERE `dropperid` = 130100 AND `itemid` = 4001358;
UPDATE drop_data SET `questid` = 28248 WHERE `dropperid` = 130101 AND `itemid` = 4001359;