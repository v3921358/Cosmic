/* 
Quest: Arwen and the Glass Shoe (2017)
Issue: Unable to complete quest due to Arwen's Glass Shoe not dropping from Fire Boars.
*/

-- Adding Arwen's Glass Shoe (4001000) into drop table of Fire Boar (3210100)
INSERT INTO maplesolaxia.drop_data (`dropperid`, `itemid`, `minimum_quantity`, `maximum_quantity`, `questid`, `chance`)
VALUES (3210100, 4001000, 1, 1, 2017, 5000);