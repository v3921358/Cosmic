# Belts and Medals
SELECT inventoryitemid, itemid, characterid, name FROM inventoryitems AS ii
INNER JOIN characters AS cc ON cc.id = ii.characterid
WHERE
((ii.itemid BETWEEN 1132005 AND 1132016) OR
(ii.itemid BETWEEN 1142000 AND 1142101) OR
(ii.itemid BETWEEN 1142107 AND 1142142)) AND
ii.itemid <> 1142107 AND ii.itemid <> 1142108
ORDER BY cc.name ASC;




SELECT inventoryitemid, itemid, characterid, name FROM inventoryitems AS ii
INNER JOIN characters AS cc ON cc.id = ii.characterid
WHERE
((ii.itemid IN (1122018, 1122007, 1122001, 1122002, 1122003, 1122004, 1122005, 1122006, 1122058)) OR #pendants
(ii.itemid IN (1012181, 1012182, 1012183, 1012184, 1012185, 1012186, 1012108, 1012109, 1012110, 1012111)) OR #face accessories
(ii.itemid IN (1022073, 1022088, 1022103, 1022089)) OR #eye accessories
(ii.itemid IN (1112407, 1112408, 1112401, 1112413, 1112414, 1112405, 1112402))) #rings
ORDER BY cc.name ASC;