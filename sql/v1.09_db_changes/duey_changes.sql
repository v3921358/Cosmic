-- Duey table changes to accommodate account packages
-- Correcting typo @_@
ALTER TABLE dueypackages CHANGE COLUMN `recieverid` receiverid INT UNSIGNED NOT NULL;

-- Add column for account IDs
ALTER TABLE dueypackages ADD COLUMN ReceiverAccountID INT UNSIGNED DEFAULT 0;

-- Add column for expiration time

ALTER TABLE dueyitems 
ADD COLUMN TimeLimit BIGINT DEFAULT -1,
ADD COLUMN IsTimeLimitActive BOOLEAN DEFAULT 0;

-- Add column for item flags

ALTER TABLE dueyitems
ADD COLUMN Flags TINYINT DEFAULT 0 AFTER jump;
