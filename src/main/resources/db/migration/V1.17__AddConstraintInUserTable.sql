ALTER TABLE USERTABLE ADD CONSTRAINT unique_email_constraint UNIQUE (EMAIL);
ALTER TABLE USERTABLE ALTER COLUMN EMAIL SET NOT NULL;
ALTER TABLE USERTABLE ALTER COLUMN MOBILE_NUMBER SET NOT NULL;
ALTER TABLE USERTABLE ALTER COLUMN ROLE SET NOT NULL;