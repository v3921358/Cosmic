-- Duey table changes to accommodate account packages
-- Correcting typo @_@
ALTER TABLE dueypackages CHANGE COLUMN `recieverid` receiverid INT UNSIGNED NOT NULL;

-- Add column for account IDs
ALTER TABLE dueypackages ADD COLUMN ReceiverAccountID INT UNSIGNED DEFAULT 0;