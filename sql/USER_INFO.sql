create table USER_INFO
(
    USER_ID   VARCHAR2(16) not null
        constraint USER_INFO_PK
        primary key,
    USER_NAME VARCHAR2(24),
    PASSWORD  VARCHAR2(64),
    EMAIL     VARCHAR2(64),
    ADDR1     VARCHAR2(200),
    ADDR2     VARCHAR2(200),
    REG_ID    VARCHAR2(20),
    REG_DT    VARCHAR2(20) default SYSDATE,
    CHG_ID    VARCHAR2(20),
    CHG_DT    VARCHAR2(20) default SYSDATE
)