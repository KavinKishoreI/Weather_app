-- Database Reset Script for Weather App
-- Run this script in your Oracle database to clean up existing tables

-- Drop existing tables in correct order (respecting foreign key constraints)
DROP TABLE user_preferences CASCADE CONSTRAINTS;
DROP TABLE saved_locations CASCADE CONSTRAINTS;
DROP TABLE users CASCADE CONSTRAINTS;

-- Optional: Drop sequences if they exist
-- DROP SEQUENCE users_seq;
-- DROP SEQUENCE user_preferences_seq;
-- DROP SEQUENCE saved_locations_seq;

COMMIT;
