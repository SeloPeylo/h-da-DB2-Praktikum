-- DROP SCHEMA master_data_knowledge_test ;

-- DROP USER jpauser;

CREATE USER jpauser WITH
  LOGIN
  NOSUPERUSER
  NOCREATEROLE
  INHERIT
  CREATEDB
  REPLICATION
  CONNECTION LIMIT -1
  PASSWORD 'JpaPassword'
  ;

CREATE SCHEMA master_data_knowledge_test
    AUTHORIZATION jpauser;