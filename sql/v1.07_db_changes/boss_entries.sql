CREATE TABLE IF NOT EXISTS `boss_entries`(
`id` int(11) NOT NULL AUTO_INCREMENT,
`charid` int(11) NOT NULL,
`papEntries` int(11) NOT NULL default 0,
`zakumEntries`int(11) NOT NULL default 0,
`horntailEntries`int(11) NOT NULL default 0,
`pinkbeanEntries` int(11) NOT NULL default 0,
`fantasybossEntries` int(11) NOT NULL default 0,
PRIMARY KEY (`charid`),
KEY(`id`)
)ENGINE=InnoDB DEFAULT CHARSET=latin1 AUTO_INCREMENT=1;