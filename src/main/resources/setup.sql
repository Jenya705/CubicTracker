CREATE TABLE IF NOT EXISTS `worlds_id_map` (
	`id` MEDIUMINT NOT NULL AUTO_INCREMENT,
	`name` VARCHAR(255) NOT NULL,
	PRIMARY KEY(`id`),
	UNIQUE KEY `name` (`name`)
);
CREATE TABLE IF NOT EXISTS `actions_id_map` (
	`id` MEDIUMINT NOT NULL AUTO_INCREMENT,
	`name` VARCHAR(255) NOT NULL,
	PRIMARY KEY(`id`),
	UNIQUE KEY `name` (`name`)
);
CREATE TABLE IF NOT EXISTS `entities_id_map` (
	`id` INT NOT NULL AUTO_INCREMENT,
	`name` VARCHAR(255) NOT NULL,
	PRIMARY KEY(`id`),
	UNIQUE KEY `name` (`name`)
);
CREATE TABLE IF NOT EXISTS `materials_id_map` (
	`id` INT NOT NULL AUTO_INCREMENT,
	`name` VARCHAR(255) NOT NULL,
	PRIMARY KEY(`id`),
	UNIQUE KEY `name` (`name`)
);
CREATE TABLE IF NOT EXISTS `actions_repeat_data` (
	`id` BIGINT NOT NULL AUTO_INCREMENT,
	`md5` BINARY(16) NOT NULL DEFAULT UNHEX(MD5(`data`)),
	`data` TEXT NOT NULL,
	PRIMARY KEY(`id`),
	UNIQUE KEY `hash` (`md5`)
);
CREATE TABLE IF NOT EXISTS `actions_data` (
	`id` BIGINT NOT NULL AUTO_INCREMENT,
	`material` INT NOT NULL,
	`data` TEXT NOT NULL,
	`repeat_data` BIGINT,
	PRIMARY KEY(`id`),
	FOREIGN KEY(`material`) REFERENCES `materials_id_map` (`id`) ON DELETE CASCADE,
	FOREIGN KEY(`repeat_data`) REFERENCES `actions_repeat_data` (`id`) ON DELETE SET NULL
);
CREATE TABLE IF NOT EXISTS `actions_cycle_data` (
	`linked` BIGINT NOT NULL,
	`data` BIGINT NOT NULL,
	FOREIGN KEY(`linked`) REFERENCES `actions_data` (`id`) ON DELETE CASCADE,
	FOREIGN KEY(`data`) REFERENCES `actions_data` (`id`) ON DELETE CASCADE
);
CREATE TABLE IF NOT EXISTS `actions` (
	`id` BIGINT NOT NULL AUTO_INCREMENT,
	`epoch` BIGINT NOT NULL,
	`action` MEDIUMINT NOT NULL,
	`source` INT NOT NULL,
	`world` MEDIUMINT NOT NULL,
	`x` INT NOT NULL,
	`y` INT NOT NULL,
	`z` INT NOT NULL,
	`target` INT,
	`new_data` BIGINT NOT NULL,
	`old_data` BIGINT,
	`rollback` TINYINT(1) NOT NULL DEFAULT FALSE,
	PRIMARY KEY(`id`),
	FOREIGN KEY(`new_data`) REFERENCES `actions_data` (`id`) ON DELETE CASCADE,
	FOREIGN KEY(`old_data`) REFERENCES `actions_data` (`id`) ON DELETE SET NULL,
	FOREIGN KEY(`action`) REFERENCES `actions_id_map` (`id`) ON DELETE CASCADE,
	FOREIGN KEY(`source`) REFERENCES `entities_id_map` (`id`) ON DELETE CASCADE,
	FOREIGN KEY(`world`) REFERENCES `worlds_id_map` (`id`) ON DELETE CASCADE,
	FOREIGN KEY(`target`) REFERENCES `entities_id_map` (`id`) ON DELETE SET NULL,
	KEY `epoch` (`epoch`),
	KEY `location` (`world`, `x`, `y`, `z`)
);
