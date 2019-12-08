--
-- Table structure for table `driver`
--

CREATE TABLE `driver` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `driverName` varchar(45) NOT NULL,
  `lat` varchar(45) NOT NULL,
  `longitude` varchar(45) NOT NULL,
  `driverStatus` varchar(45) NOT NULL DEFAULT 'AVAILABLE',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8;

--
-- Table structure for table `customer`
--

CREATE TABLE `customer` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `orderNumber` varchar(45) NOT NULL,
  `customerName` varchar(45) NOT NULL,
  `lat` varchar(45) NOT NULL,
  `longitude` varchar(45) NOT NULL,
  `driverId` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `driverIdlFK_idx` (`driverId`),
  CONSTRAINT `driverIdlFK` FOREIGN KEY (`driverId`) REFERENCES `driver` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

--
-- Dumping data for table `driver`
--

INSERT INTO `driver` VALUES (9,'Michal','-37.750000','145.136667','AVAILABLE'),(10,'John','-37.759859','145.138708','AVAILABLE'),(11,'Billy','-37.765015','145.133858','AVAILABLE'),(12,'Keran','-37.770104','145.133299','AVAILABLE'),(13,'Mary','-37.773700','145.135187','AVAILABLE');

