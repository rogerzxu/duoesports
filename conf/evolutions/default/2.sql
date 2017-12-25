# --- !Ups

ALTER TABLE User
  ADD COLUMN verification_code VARCHAR(50) DEFAULT NULL,
  ADD COLUMN roles SET ('Middle', 'Jungle', 'Top', 'Bottom', 'Support', 'Coach', 'Analyst') DEFAULT NULL,
  ADD COLUMN summoner_id BIGINT(20) after summonerName,
  CHANGE role user_role VARCHAR(20) NOT NULL;

# --- !Downs
ALTER TABLE User
  DROP COLUMN verification_code,
  DROP COLUMN roles,
  DROP COLUMN summoner_id,
  CHANGE user_role role VARCHAR(20) NOT NULL;