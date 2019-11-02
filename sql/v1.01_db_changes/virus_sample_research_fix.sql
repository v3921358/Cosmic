/* 
Quest: 28192 - Virus Sample Research
Issue: Unable to complete quest due to items not dropping from the correct mobs.
*/
USE `dietstory`;

-- Adding Orange Mushroom Sample (4001364) into drop table of Orange Mushroom (1210102)
INSERT INTO drop_data (`dropperid`, `itemid`, `minimum_quantity`, `maximum_quantity`, `questid`, `chance`)
VALUE (1210102, 4001364, 1, 1, 28192, 10000);

-- Adding Octopus Sample (4001365) into drop table of Octopus (1120100)
INSERT INTO drop_data (`dropperid`, `itemid`, `minimum_quantity`, `maximum_quantity`, `questid`, `chance`)
VALUE (1120100, 4001365, 1, 1, 28192, 10000);