-- Add itemLevel and itemExp to dueyitems

ALTER TABLE dueyitems ADD COLUMN itemLevel INT DEFAULT 1 AFTER level, ADD COLUMN itemExp INT DEFAULT 0 AFTER itemLevel;