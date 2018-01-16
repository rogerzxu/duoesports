# --- !Ups

CREATE TABLE User (
  id                BIGINT(20)   NOT NULL                                                    AUTO_INCREMENT,
  email             VARCHAR(128) NOT NULL,
  password          VARCHAR(255) NOT NULL,
  firstName         VARCHAR(50)  NOT NULL,
  lastName          VARCHAR(50)  NOT NULL,
  user_role         VARCHAR(20)  NOT NULL,
  summonerName      VARCHAR(50)                                                              DEFAULT NULL,
  summoner_id       BIGINT(20)                                                               DEFAULT NULL,
  region            VARCHAR(5)                                                               DEFAULT NULL,
  team_id           BIGINT(20)                                                               DEFAULT NULL,
  activated         BOOLEAN                                                                  DEFAULT FALSE,
  eligible          BOOLEAN                                                                  DEFAULT FALSE,
  verification_code VARCHAR(50)                                                              DEFAULT NULL,
  roles             SET ('Middle', 'Jungle', 'Top', 'Bottom', 'Support', 'Coach', 'Analyst') DEFAULT NULL,

  CONSTRAINT user_pk PRIMARY KEY (id),
  UNIQUE INDEX email_idx (email),
  UNIQUE INDEX summoner_idx (summonerName, region)
);

CREATE TABLE AuthToken (
  id                VARCHAR(50) NOT NULL,
  user_id           BIGINT(20) NOT NULL,
  expiry            TIMESTAMP NOT NULL,

  CONSTRAINT auth_token_pk PRIMARY KEY (id),
  CONSTRAINT auth_token_user_id FOREIGN KEY (user_id) REFERENCES User (id) ON DELETE CASCADE
);

# --- !Downs

DROP TABLE User;