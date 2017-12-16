# --- !Ups

ALTER TABLE User
  ADD COLUMN verification_code VARCHAR(50) DEFAULT NULL,
  ADD COLUMN roles VARCHAR(255) DEFAULT NULL,
  CHANGE role user_role VARCHAR(20) NOT NULL;

# --- !Downs
ALTER TABLE User
  DROP COLUMN verification_code,
  DROP COLUMN roles,
  CHANGE user_role role VARCHAR(20) NOT NULL;