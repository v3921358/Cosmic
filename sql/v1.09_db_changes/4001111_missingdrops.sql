USE dietstory;

-- Remove quest dependency for dropping [Storybook] Crimson Balrog's Proposal
UPDATE drop_data
SET questid = 0
WHERE dropperid = 8150000 AND itemid = 4001111;