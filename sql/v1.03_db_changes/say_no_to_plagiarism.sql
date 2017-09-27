/* 
Quest: Say 'NO' to Plagiarism (2017)
Issue: Unable to complete quest due to Spirit of Rock's Music Score not dropping from Spirit of Rock.
*/

-- Adding Spirit of Rock's Music Score (4000538) into drop table of Spirit of Rock (4300013)
INSERT INTO maplesolaxia.drop_data (`dropperid`, `itemid`, `minimum_quantity`, `maximum_quantity`, `questid`, `chance`)
VALUES (4300013, 4000538, 1, 1, 2288, 600000);