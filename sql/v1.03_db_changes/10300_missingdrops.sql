INSERT INTO dietstory.drop_data (`dropperid`, `itemid`, `minimum_quantity`, `maximum_quantity`, `questid`, `chance`)
VALUES 
(3300003, 4001317, 1, 1, 2326, 5000); -- Helmet Pepe | Helmet Pepe's Helmet | James's Whereabouts (2)






-- There are two Jr. Boogies mob ids for some unknown reason. 3230301 had no drops, but 3230300 had all the correct drops.
-- Just copying the drops from the one with the correct drop data.
INSERT INTO dietstory.drop_data (`dropperid`, `itemid`, `minimum_quantity`, `maximum_quantity`, `questid`, `chance`)
SELECT 3230301, `itemid`, `minimum_quantity`, `maximum_quantity`, `questid`, `chance` FROM dietstory.drop_data WHERE dropperid = 3230300;
