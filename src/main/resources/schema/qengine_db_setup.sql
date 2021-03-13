/*
 * Licensed to the Apache Software Foundation (ASF) under one
 *  * or more contributor license agreements.  See the NOTICE file
 *  * distributed with this work for additional information
 *  * regarding copyright ownership.  The ASF licenses this file
 *  * to you under the Apache License, Version 2.0 (the
 *  * "License"); you may not use this file except in compliance
 *  * with the License.  You may obtain a copy of the License at
 *  *
 *  *   http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing,
 *  * software distributed under the License is distributed on an
 *  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  * KIND, either express or implied.  See the License for the
 *  * specific language governing permissions and limitations
 *  * under the License.
 *  *
 *  * Copyright (c) 2021
 *  * Date: 13/03/21, 2:45 PM
 *  * Subho Ghosh (subho dot ghosh at outlook.com)
 */

-- qengine.qe_field_alias definition

CREATE TABLE `qe_field_alias`
(
    `entity_type` varchar(256)  NOT NULL,
    `name`        varchar(256)  NOT NULL,
    `field_path`  varchar(2048) NOT NULL,
    PRIMARY KEY (`entity_type`, `name`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8 COMMENT ='QEngine Field Alias definitions';


-- qengine.qe_parameters definition

CREATE TABLE `qe_parameters`
(
    `id`          varchar(128) NOT NULL,
    `name`        varchar(256) NOT NULL,
    `data_type`   varchar(256) NOT NULL,
    `is_dynamic`  tinyint(4)    DEFAULT 1,
    `description` varchar(1024) DEFAULT NULL,
    PRIMARY KEY (`id`),
    KEY `qe_parameters_name_IDX` (`name`, `data_type`) USING BTREE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8 COMMENT ='QEngine Parameter definitions';


-- qengine.qe_paramter_values definition

CREATE TABLE `qe_paramter_values`
(
    `reference_id` varchar(128) NOT NULL,
    `parameter_id` varchar(128) NOT NULL,
    `value`        varchar(4096) DEFAULT NULL,
    PRIMARY KEY (`reference_id`, `parameter_id`),
    KEY `qe_paramter_values_FK` (`parameter_id`),
    CONSTRAINT `qe_paramter_values_FK` FOREIGN KEY (`parameter_id`) REFERENCES `qe_parameters` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8 COMMENT ='QEngine Persisted parameter values';