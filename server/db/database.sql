-- 需要先安装pgvector这个扩展（https://github.com/pgvector/pgvector）
-- CREATE EXTENSION vector;

SET client_encoding = 'UTF8';
CREATE SCHEMA public;

CREATE TABLE "public"."users" (
    "id" int4 NOT NULL GENERATED ALWAYS AS IDENTITY (
        INCREMENT 1
        MINVALUE  1
        MAXVALUE 2147483647
        START 1
        CACHE 1
    ),
    "name" varchar(155) COLLATE "pg_catalog"."default" DEFAULT ''::character varying,
    "code" varchar(64) COLLATE "pg_catalog"."default" DEFAULT ''::character varying,
    "nickname" varchar(155) COLLATE "pg_catalog"."default" DEFAULT ''::character varying,
    "avatar" varchar(155) COLLATE "pg_catalog"."default" DEFAULT ''::character varying,
    "password" varchar(64) COLLATE "pg_catalog"."default" DEFAULT ''::character varying,
    "salt" varchar(64) COLLATE "pg_catalog"."default" DEFAULT ''::character varying,
    "deleted" int2 DEFAULT 1,
    "status" int2 DEFAULT 1,
    "create_time" timestamp(6),
    "update_time" date,
    CONSTRAINT "users_pkey" PRIMARY KEY ("id")
);

ALTER TABLE "public"."users"
    OWNER TO "postgres";

COMMENT ON COLUMN "public"."users"."id" IS 'id';
COMMENT ON COLUMN "public"."users"."name" IS '登录账号';
COMMENT ON COLUMN "public"."users"."code" IS '唯一编码';
COMMENT ON COLUMN "public"."users"."nickname" IS '昵称';
COMMENT ON COLUMN "public"."users"."avatar" IS '头像';
COMMENT ON COLUMN "public"."users"."password" IS '密码';
COMMENT ON COLUMN "public"."users"."salt" IS '加密盐';
COMMENT ON COLUMN "public"."users"."deleted" IS '是否删除 1:正常 2:删除';
COMMENT ON COLUMN "public"."users"."status" IS '状态 1:正常 2:禁用';
COMMENT ON COLUMN "public"."users"."create_time" IS '创建时间';
COMMENT ON COLUMN "public"."users"."update_time" IS '更新时间';