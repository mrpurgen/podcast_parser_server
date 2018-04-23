CREATE TABLE IF NOT EXISTS category(id INT  PRIMARY KEY AUTO_INCREMENT, name VARCHAR(255), url VARCHAR(255)) ENGINE=InnoDB CHARACTER SET=UTF8;
CREATE TABLE IF NOT EXISTS podcast(id INT  PRIMARY KEY AUTO_INCREMENT, categoryId INT, name VARCHAR(255), date DATE, url VARCHAR(255)) ENGINE=InnoDB CHARACTER SET=UTF8;