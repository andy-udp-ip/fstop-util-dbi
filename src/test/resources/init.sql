DROP FUNCTION SP_TEST IF EXISTS;

CREATE FUNCTION SP_TEST (t int)
   RETURNS int
   RETURN t - 1 
   
-- call SP_TEST(1)

--
CREATE TABLE
    TEST1
    (
        T1 INTEGER NOT NULL,
        T2 VARCHAR(50),
        PRIMARY KEY (T1)
    );