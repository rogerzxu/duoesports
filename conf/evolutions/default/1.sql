# --- !Ups

CREATE TABLE User (
  id           BIGINT(20)   NOT NULL      AUTO_INCREMENT,
  email        VARCHAR(128) NOT NULL,
  password     VARCHAR(255) NOT NULL,
  firstName    VARCHAR(50)  NOT NULL,
  lastName     VARCHAR(50)  NOT NULL,
  role         VARCHAR(20)  NOT NULL,
  summonerName VARCHAR(50)                DEFAULT NULL,
  region       VARCHAR(5)                 DEFAULT NULL,
  team_id      BIGINT(20)                 DEFAULT NULL,
  activated    BOOLEAN                    DEFAULT FALSE,
  eligible     BOOLEAN                    DEFAULT FALSE,
  CONSTRAINT user_pk PRIMARY KEY (id),
  UNIQUE INDEX email_idx (email),
  UNIQUE INDEX summoner_idx (summonerName, region)
);

# --- !Downs

DROP TABLE User;