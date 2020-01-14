-- Tera Forest questline drops
USE dietstory;

-- Garbage cans in 2021 | Primary Clue to the Case | The First Clue (3720)
INSERT IGNORE INTO reactordrops (`reactorid`, `itemid`, `chance`, `questid`) 
VALUES
(2402007, 4032512, 10, 3720),
(2402008, 4032512, 10, 3720);

-- Delete existing entries since the db seems to include some of them
DELETE FROM drop_data WHERE itemid >= 4000545 AND itemid <= 4000547;

-- Coloured Slimes | Coloured Squishy Liquids | Dangerous Slimes (3721)
INSERT IGNORE INTO drop_data (`dropperid`, `itemid`, `chance`)
VALUES
(7120103, 4000545, 500000),
(7120104, 4000546, 500000),
(7120105, 4000547, 500000),
(9400202, 4000547, 500000),
(9400203, 4000546, 500000),
(9400204, 4000545, 500000);

-- Coloured Slimes | Wrinkled Sketchbook Loose-Leaf | (3722)
INSERT IGNORE INTO drop_data (`dropperid`, `itemid`, `chance`, `questid`)
VALUES
(7120103, 4032513, 100000, 3722),
(7120104, 4032513, 100000, 3722),
(7120105, 4032513, 100000, 3722),
(9400202, 4032513, 100000, 3722),
(9400203, 4032513, 100000, 3722),
(9400204, 4032513, 100000, 3722);

-- Overlord A/B | Shelter Key | The Shelter Key (3727)
INSERT IGNORE INTO drop_data (`dropperid`, `itemid`, `chance`, `questid`)
VALUES 
(7120106, 4032514, 20000, 3727),
(7120107, 4032514, 20000, 3727);

-- Neo City bosses | Time Sands | Various
INSERT IGNORE INTO drop_data (`dropperid`, `itemid`, `chance`)
VALUES
(7220005, 4032515, 500000), -- Bergamot
(8220010, 4032516, 500000), -- Dunas
(8220011, 4032517, 500000), -- Aufheben
(8220012, 4032518, 500000), -- Oberon
(8220015, 4032519, 500000); -- Nibelung