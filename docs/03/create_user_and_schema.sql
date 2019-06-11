-- DROP USER postgres;

CREATE USER JpaUser WITH
  LOGIN
  NOSUPERUSER
  NOCREATEROLE
  INHERIT
  CREATEDB
  REPLICATION
  CONNECTION LIMIT -1
  PASSWORD 'JpaPassword'
  ;

-- DROP SCHEMA master_data_knowledge_test ;

CREATE SCHEMA master_data_knowledge_test
    AUTHORIZATION jpauser;