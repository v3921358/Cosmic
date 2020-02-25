USE dietstory;

-- Innocence Scroll 100%
INSERT INTO drop_data_global (`continent`, `dropType`, `itemid`, `minimum_quantity`, `maximum_quantity`, `questid`, `chance`, `comments`)
VALUE (0, 0, 2049610, 1, 1, 0, 300, "Innocence Scroll 100% (Untradeable)");

# Scroll shop at Spindle - adding innocence scroll
INSERT INTO shopitems (`shopitemid`, `shopid`, `itemid`, `price`, `pitch`, `position`) VALUES
(995033, 9201082, 2049610, 6000000, 0, 273);
