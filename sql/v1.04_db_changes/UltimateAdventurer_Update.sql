USE `dietstory`;

CREATE TABLE `ultimate_adventurers` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `successorId` int(11) NOT NULL,
  `cygnusId` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `cygnusId_UNIQUE` (`cygnusId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
