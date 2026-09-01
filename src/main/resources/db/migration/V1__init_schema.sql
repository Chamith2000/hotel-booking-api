CREATE TABLE `admins` (
                          `id` bigint NOT NULL AUTO_INCREMENT,
                          `approval_status` enum('APPROVED','PENDING','REJECTED') NOT NULL,
                          `email` varchar(255) DEFAULT NULL,
                          `password` varchar(255) DEFAULT NULL,
                          `profile_photo_url` varchar(255) DEFAULT NULL,
                          `user_name` varchar(255) DEFAULT NULL,
                          PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


CREATE TABLE `loyalty_points` (
                                  `id` bigint NOT NULL AUTO_INCREMENT,
                                  `count` double NOT NULL,
                                  `earned_date` date DEFAULT NULL,
                                  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


CREATE TABLE `customers` (
                             `id` bigint NOT NULL AUTO_INCREMENT,
                             `email` varchar(255) DEFAULT NULL,
                             `password` varchar(255) DEFAULT NULL,
                             `profile_photo_url` varchar(255) DEFAULT NULL,
                             `user_name` varchar(255) DEFAULT NULL,
                             `loyalty_point_id` bigint DEFAULT NULL,
                             PRIMARY KEY (`id`),
                             UNIQUE KEY `UKn5tulvcj64moe1w7vsqaconjo` (`loyalty_point_id`),
                             CONSTRAINT `FKsdiopm90py3ep8m7388i1w8wx` FOREIGN KEY (`loyalty_point_id`) REFERENCES `loyalty_points` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


CREATE TABLE `users` (
                         `id` bigint NOT NULL AUTO_INCREMENT,
                         `enabled` bit(1) NOT NULL,
                         `password` varchar(255) DEFAULT NULL,
                         `username` varchar(255) DEFAULT NULL,
                         `role` enum('ADMIN','CUSTOMER','HOTEL') NOT NULL,
                         PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


CREATE TABLE `facility_categories` (
                                       `id` bigint NOT NULL AUTO_INCREMENT,
                                       `category_name` varchar(255) NOT NULL,
                                       PRIMARY KEY (`id`),
                                       UNIQUE KEY `UKqgq9yb6cvb7pcdg5kq3kdi5x1` (`category_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


CREATE TABLE `facility_items` (
                                  `id` bigint NOT NULL AUTO_INCREMENT,
                                  `is_active` bit(1) DEFAULT NULL,
                                  `item_name` varchar(255) DEFAULT NULL,
                                  `facility_category_id` bigint DEFAULT NULL,
                                  PRIMARY KEY (`id`),
                                  KEY `FKle1bs4etqiqwgnu3blhpi6ixn` (`facility_category_id`),
                                  CONSTRAINT `FKle1bs4etqiqwgnu3blhpi6ixn` FOREIGN KEY (`facility_category_id`) REFERENCES `facility_categories` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


CREATE TABLE `room_types` (
                              `id` bigint NOT NULL AUTO_INCREMENT,
                              `price` double NOT NULL,
                              `type` varchar(255) DEFAULT NULL,
                              PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


CREATE TABLE `package_types` (
                                 `id` bigint NOT NULL AUTO_INCREMENT,
                                 `name` varchar(255) DEFAULT NULL,
                                 PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


CREATE TABLE `hotels` (
                          `id` bigint NOT NULL AUTO_INCREMENT,
                          `city` varchar(255) DEFAULT NULL,
                          `no` varchar(255) DEFAULT NULL,
                          `province` varchar(255) DEFAULT NULL,
                          `street` varchar(255) DEFAULT NULL,
                          `boost_package_limit` varchar(255) DEFAULT NULL,
                          `email` varchar(255) DEFAULT NULL,
                          `logo` varchar(255) DEFAULT NULL,
                          `name` varchar(255) DEFAULT NULL,
                          `password` varchar(255) DEFAULT NULL,
                          `status` bit(1) DEFAULT NULL,
                          `super_deal_limit` varchar(255) DEFAULT NULL,
                          `approval_status` enum('APPROVED','PENDING','REJECTED') NOT NULL,
                          PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=133 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


CREATE TABLE `authorities` (
                               `id` bigint NOT NULL AUTO_INCREMENT,
                               `authority` varchar(255) DEFAULT NULL,
                               `user_id` bigint DEFAULT NULL,
                               `hotel_id` bigint DEFAULT NULL,
                               `role` enum('ADMIN','CUSTOMER','HOTEL') NOT NULL,
                               PRIMARY KEY (`id`),
                               KEY `FKk91upmbueyim93v469wj7b2qh` (`user_id`),
                               KEY `FKm3b4hvfo72bt4fmflg0lwx8n0` (`hotel_id`),
                               CONSTRAINT `FKk91upmbueyim93v469wj7b2qh` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`),
                               CONSTRAINT `FKm3b4hvfo72bt4fmflg0lwx8n0` FOREIGN KEY (`hotel_id`) REFERENCES `hotels` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


CREATE TABLE `notifications` (
                                 `id` bigint NOT NULL AUTO_INCREMENT,
                                 `message` varchar(255) DEFAULT NULL,
                                 `role` enum('ADMIN','CUSTOMER','HOTEL') NOT NULL,
                                 `sent_date_and_time` datetime(6) DEFAULT NULL,
                                 `status` bit(1) NOT NULL,
                                 `admin_id` bigint DEFAULT NULL,
                                 PRIMARY KEY (`id`),
                                 KEY `FKd39kijysujlr1dus78qn9elw6` (`admin_id`),
                                 CONSTRAINT `FKd39kijysujlr1dus78qn9elw6` FOREIGN KEY (`admin_id`) REFERENCES `admins` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


CREATE TABLE `hotel_images` (
                                `id` bigint NOT NULL AUTO_INCREMENT,
                                `url` varchar(255) DEFAULT NULL,
                                `hotel_id` bigint DEFAULT NULL,
                                PRIMARY KEY (`id`),
                                KEY `FKrj3n45f8oqy1yr996g14j757i` (`hotel_id`),
                                CONSTRAINT `FKrj3n45f8oqy1yr996g14j757i` FOREIGN KEY (`hotel_id`) REFERENCES `hotels` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


CREATE TABLE `hotel_contact_numbers` (
                                         `id` bigint NOT NULL AUTO_INCREMENT,
                                         `number` varchar(255) DEFAULT NULL,
                                         `hotel_id` bigint DEFAULT NULL,
                                         PRIMARY KEY (`id`),
                                         KEY `FK4664sqo2o1a1ayykfm17rdg8f` (`hotel_id`),
                                         CONSTRAINT `FK4664sqo2o1a1ayykfm17rdg8f` FOREIGN KEY (`hotel_id`) REFERENCES `hotels` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


CREATE TABLE `hotel_menus` (
                               `id` bigint NOT NULL AUTO_INCREMENT,
                               `type` varchar(255) DEFAULT NULL,
                               `hotel_id` bigint DEFAULT NULL,
                               PRIMARY KEY (`id`),
                               KEY `FKnftpsr4ad8lrjkwbwq12h3b45` (`hotel_id`),
                               CONSTRAINT `FKnftpsr4ad8lrjkwbwq12h3b45` FOREIGN KEY (`hotel_id`) REFERENCES `hotels` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


CREATE TABLE `menu_items` (
                              `id` bigint NOT NULL AUTO_INCREMENT,
                              `item_name` varchar(255) DEFAULT NULL,
                              `price` double NOT NULL,
                              `hotel_menu_id` bigint DEFAULT NULL,
                              PRIMARY KEY (`id`),
                              KEY `FKlk3khob98nqqidwbqmr7pr7gq` (`hotel_menu_id`),
                              CONSTRAINT `FKlk3khob98nqqidwbqmr7pr7gq` FOREIGN KEY (`hotel_menu_id`) REFERENCES `hotel_menus` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


CREATE TABLE `hotel_room_types` (
                                    `hotel_id` bigint NOT NULL,
                                    `room_type_id` bigint NOT NULL,
                                    KEY `FK6otdxp2qa4l5kmp0ijpbspegs` (`room_type_id`),
                                    KEY `FKr1op5ia2dcxem6g9oj4ahm2k5` (`hotel_id`),
                                    CONSTRAINT `FK6otdxp2qa4l5kmp0ijpbspegs` FOREIGN KEY (`room_type_id`) REFERENCES `room_types` (`id`),
                                    CONSTRAINT `FKr1op5ia2dcxem6g9oj4ahm2k5` FOREIGN KEY (`hotel_id`) REFERENCES `hotels` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


CREATE TABLE `reviews` (
                           `id` bigint NOT NULL AUTO_INCREMENT,
                           `comment` varchar(255) DEFAULT NULL,
                           `posted_date_and_time` datetime(6) DEFAULT NULL,
                           `rating` int NOT NULL,
                           `customer_id` bigint DEFAULT NULL,
                           `hotel_id` bigint DEFAULT NULL,
                           PRIMARY KEY (`id`),
                           KEY `FK4sm0k8kw740iyuex3vwwv1etu` (`customer_id`),
                           KEY `FKb9igk5exfb4knqklcvka6cdhx` (`hotel_id`),
                           CONSTRAINT `FK4sm0k8kw740iyuex3vwwv1etu` FOREIGN KEY (`customer_id`) REFERENCES `customers` (`id`),
                           CONSTRAINT `FKb9igk5exfb4knqklcvka6cdhx` FOREIGN KEY (`hotel_id`) REFERENCES `hotels` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


CREATE TABLE `hotel_packages` (
                                  `id` bigint NOT NULL AUTO_INCREMENT,
                                  `description` varchar(255) DEFAULT NULL,
                                  `end_date` date DEFAULT NULL,
                                  `adults` int DEFAULT NULL,
                                  `children` int DEFAULT NULL,
                                  `name` varchar(255) DEFAULT NULL,
                                  `price` double NOT NULL,
                                  `start_date` date DEFAULT NULL,
                                  `status` bit(1) DEFAULT NULL,
                                  `terms_and_condition` varchar(255) DEFAULT NULL,
                                  `visitor_count` int NOT NULL,
                                  `hotel_id` bigint DEFAULT NULL,
                                  `hotel_menu_id` bigint DEFAULT NULL,
                                  `package_type_id` bigint DEFAULT NULL,
                                  `room_type_id` bigint DEFAULT NULL,
                                  `package_status` enum('APPROVED','PENDING','REJECTED') DEFAULT NULL,
                                  PRIMARY KEY (`id`),
                                  KEY `FKoqtp3ib4b8krl5lr4jl2kubuj` (`hotel_id`),
                                  KEY `FKa8ypdjca3sj8wdkcf1tix57xm` (`hotel_menu_id`),
                                  KEY `FKndfatw5g5g0jblnh0hd743n3v` (`package_type_id`),
                                  KEY `FKf3lswa56vxeub1cp3jipfubcg` (`room_type_id`),
                                  CONSTRAINT `FKa8ypdjca3sj8wdkcf1tix57xm` FOREIGN KEY (`hotel_menu_id`) REFERENCES `hotel_menus` (`id`),
                                  CONSTRAINT `FKf3lswa56vxeub1cp3jipfubcg` FOREIGN KEY (`room_type_id`) REFERENCES `room_types` (`id`),
                                  CONSTRAINT `FKndfatw5g5g0jblnh0hd743n3v` FOREIGN KEY (`package_type_id`) REFERENCES `package_types` (`id`),
                                  CONSTRAINT `FKoqtp3ib4b8krl5lr4jl2kubuj` FOREIGN KEY (`hotel_id`) REFERENCES `hotels` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=142 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


CREATE TABLE `boost_packages` (
                                  `id` bigint NOT NULL AUTO_INCREMENT,
                                  `boosted_date` date DEFAULT NULL,
                                  `hotel_id` bigint DEFAULT NULL,
                                  `package_id` bigint DEFAULT NULL,
                                  PRIMARY KEY (`id`),
                                  UNIQUE KEY `UKqkoh3o3vhdsvb6y029rpwqc77` (`package_id`),
                                  KEY `FK1m2xm4el1ktcv02b8fla0fq98` (`hotel_id`),
                                  CONSTRAINT `FK1m2xm4el1ktcv02b8fla0fq98` FOREIGN KEY (`hotel_id`) REFERENCES `hotels` (`id`),
                                  CONSTRAINT `FK1np1ycvnjvgkcccdju1k8f1ci` FOREIGN KEY (`package_id`) REFERENCES `hotel_packages` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=66 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


CREATE TABLE `super_deals` (
                               `id` bigint NOT NULL AUTO_INCREMENT,
                               `discounted_price` double NOT NULL,
                               `end_date` date DEFAULT NULL,
                               `start_date` date DEFAULT NULL,
                               `hotel_id` bigint DEFAULT NULL,
                               `hotel_package_id` bigint DEFAULT NULL,
                               PRIMARY KEY (`id`),
                               UNIQUE KEY `UK6f3g68t22w8r37m70dtf8yp1h` (`hotel_package_id`),
                               KEY `FKhdt1upf7a8sbgpegwbpcr64e6` (`hotel_id`),
                               CONSTRAINT `FK9a6tvx5hqnig6rhnk8d8ec2k9` FOREIGN KEY (`hotel_package_id`) REFERENCES `hotel_packages` (`id`),
                               CONSTRAINT `FKhdt1upf7a8sbgpegwbpcr64e6` FOREIGN KEY (`hotel_id`) REFERENCES `hotels` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


CREATE TABLE `package_images` (
                                  `id` bigint NOT NULL AUTO_INCREMENT,
                                  `url` varchar(255) DEFAULT NULL,
                                  `hotel_package_id` bigint DEFAULT NULL,
                                  PRIMARY KEY (`id`),
                                  KEY `FK6x9h4qj2jhl6jv8sw8lhtkcn2` (`hotel_package_id`),
                                  CONSTRAINT `FK6x9h4qj2jhl6jv8sw8lhtkcn2` FOREIGN KEY (`hotel_package_id`) REFERENCES `hotel_packages` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=27 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


CREATE TABLE `hotel_package_facility_item` (
                                               `hotel_package_id` bigint NOT NULL,
                                               `facility_item_id` bigint NOT NULL,
                                               KEY `FKh15043dniydnp53l8a6hbc09h` (`facility_item_id`),
                                               KEY `FK1lj4qb4qjiudbjqlhvysmfpug` (`hotel_package_id`),
                                               CONSTRAINT `FK1lj4qb4qjiudbjqlhvysmfpug` FOREIGN KEY (`hotel_package_id`) REFERENCES `hotel_packages` (`id`),
                                               CONSTRAINT `FKh15043dniydnp53l8a6hbc09h` FOREIGN KEY (`facility_item_id`) REFERENCES `facility_items` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


CREATE TABLE `hotel_package_type` (
                                      `hotel_id` bigint NOT NULL,
                                      `package_type_id` bigint NOT NULL,
                                      KEY `FKjwnhv5e8v0nuy23vqmxj2p7yh` (`package_type_id`),
                                      KEY `FKhpvmf6o4tey9u3fbhehwyec08` (`hotel_id`),
                                      CONSTRAINT `FKhpvmf6o4tey9u3fbhehwyec08` FOREIGN KEY (`hotel_id`) REFERENCES `hotels` (`id`),
                                      CONSTRAINT `FKjwnhv5e8v0nuy23vqmxj2p7yh` FOREIGN KEY (`package_type_id`) REFERENCES `package_types` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


CREATE TABLE `hotel_notifications` (
                                       `notification_id` bigint NOT NULL,
                                       `hotel_id` bigint NOT NULL,
                                       KEY `FKe6f1cbwelrub90dcg7rc3tnax` (`hotel_id`),
                                       KEY `FKqhl0rg63becajrav9o4qtsqiw` (`notification_id`),
                                       CONSTRAINT `FKe6f1cbwelrub90dcg7rc3tnax` FOREIGN KEY (`hotel_id`) REFERENCES `hotels` (`id`),
                                       CONSTRAINT `FKqhl0rg63becajrav9o4qtsqiw` FOREIGN KEY (`notification_id`) REFERENCES `notifications` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


CREATE TABLE `customer_notifications` (
                                          `notification_id` bigint NOT NULL,
                                          `customer_id` bigint NOT NULL,
                                          KEY `FK3195hk1a47mqgstpj8jg696ot` (`customer_id`),
                                          KEY `FK6nv5yc5exnxatcw369x8rhvjg` (`notification_id`),
                                          CONSTRAINT `FK3195hk1a47mqgstpj8jg696ot` FOREIGN KEY (`customer_id`) REFERENCES `customers` (`id`),
                                          CONSTRAINT `FK6nv5yc5exnxatcw369x8rhvjg` FOREIGN KEY (`notification_id`) REFERENCES `notifications` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

