--  推送表
IF NOT EXISTS
    (
        SELECT
            *
        FROM
            KSQL_USERTABLES
        WHERE
            KSQL_TABNAME ='T_GMOBILE_PUSH')
    CREATE TABLE
        T_GMOBILE_PUSH
        (
            FPushToken VARCHAR(100) NOT NULL,
            FLastUserID VARCHAR(50),
            FLastUpdateTime DATETIME,
            FCreateTime DATETIME,
            CONSTRAINT PK_T_GMOBILE_PUSH PRIMARY KEY (FPushToken)
        );