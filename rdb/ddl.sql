CREATE TABLE `companies` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(100) NOT NULL,
  `lock_version` int(11) NOT NULL DEFAULT 1,
  PRIMARY KEY(`id`)
);

CREATE TABLE `members` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(100) NOT NULL,
  `company_id` int(11) NOT NULL,
  `lock_version` int(11) NOT NULL DEFAULT 1,
  PRIMARY KEY(`id`),
  FOREIGN KEY (`company_id`) REFERENCES `companies` (`id`)
);

CREATE TABLE `items` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(100) NOT NULL,
  `price` int(11) NOT NULL,
  `lock_version` int(11) NOT NULL DEFAULT 1,
  `is_deleted` boolean NOT NULL DEFAULT false,
  PRIMARY KEY(`id`)
);

CREATE TABLE `order_summaries` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `orderer` int(11) NOT NULL,
  `status` enum('unsettled', 'settled') NOT NULL DEFAULT 'unsettled',
  `order_date` datetime NOT NULL,
  `settled_date` datetime DEFAULT NULL,
  `lock_version` int(11) NOT NULL DEFAULT 1,
  PRIMARY KEY(`id`),
  FOREIGN KEY (`orderer`) REFERENCES `members` (`id`)
);

CREATE TABLE `order_details` (
  `summary_id` int(11) NOT NULL,
  `item_id` int(11) NOT NULL,
  `number` int(11) NOT NULL,
  `discount_rate` decimal(5,2) NOT NULL,
  `price` decimal(5, 2) NOT NULL,
  PRIMARY KEY(`summary_id`, `item_id`),
  FOREIGN KEY (`summary_id`) REFERENCES `order_summaries` (`id`),
  FOREIGN KEY (`item_id`) REFERENCES `items` (`id`)
);
