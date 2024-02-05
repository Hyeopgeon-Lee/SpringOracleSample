create table NOTICE
(
    NOTICE_SEQ NUMBER not null
        constraint NOTICE_PK
        primary key,
    TITLE      VARCHAR2(1000),
    NOTICE_YN  VARCHAR2(1),
    CONTENTS   VARCHAR2(4000),
    USER_ID    VARCHAR2(20),
    READ_CNT   NUMBER,
    REG_ID     VARCHAR2(20),
    REG_DT     DATE default SYSDATE,
    CHG_ID     VARCHAR2(20),
    CHG_DT     DATE default SYSDATE
)