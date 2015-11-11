//sql queries

//creating the Flags-Table
CREATE TABLE Flags
(
flagID INT NOT NULL AUTO_INCREMENT,
userName VARCHAR(255) NOT NULL,
gpsCoordinates DOUBLE,
categoryName VARCHAR(255),
date TIMESTAMP,
content TEXT,
PRIMARY KEY (ID)
)

//inserting demo data into Flags
INSERT INTO `distsys`.`Flags` (`flagID`, `userName`, `gpsCoordinates`, `categoryName`, `date`, `content`) VALUES (NULL, 'Pascal', '1.0', 'debug', CURRENT_TIMESTAMP, 'Lorem Ipsum dolor sit amet');


//query
SELECT * FROM `Flags`