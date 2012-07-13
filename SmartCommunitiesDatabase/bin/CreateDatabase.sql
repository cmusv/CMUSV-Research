-- REVOKE ALL ON SmartCommunities.* FROM smartspaces@'%';
-- DROP   DATABASE SmartCommunities;
-- DROP   USER smartspaces@localhost;
CREATE USER smartspaces@localhost IDENTIFIED BY 'cmucmu';
CREATE DATABASE SmartCommunities;
GRANT  ALL ON SmartCommunities.* TO smartspaces@'%' IDENTIFIED BY 'cmucmu';
