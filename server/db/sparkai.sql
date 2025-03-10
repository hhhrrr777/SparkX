-- 需要先安装pgvector这个扩展（https://github.com/pgvector/pgvector）
-- CREATE EXTENSION vector;

SET client_encoding = 'UTF8';
CREATE SCHEMA public;

CREATE TABLE "public"."knowledge_dataset" (
    "uuid" varchar(64) COLLATE "pg_catalog"."default",
    "title" varchar(155) COLLATE "pg_catalog"."default" DEFAULT ''::character varying,
    "description" varchar(255) COLLATE "pg_catalog"."default" DEFAULT ''::character varying,
    "user_id" varchar(64) COLLATE "pg_catalog"."default" DEFAULT 0,
    "type" int2 DEFAULT 1,
    "embedding_mode_id" varchar(64) COLLATE "pg_catalog"."default",
    "create_time" timestamp(6),
    "update_time" timestamp(6)
);

ALTER TABLE "public"."knowledge_dataset"
    OWNER TO "postgres";

COMMENT ON COLUMN "public"."knowledge_dataset"."uuid" IS 'uuid';
COMMENT ON COLUMN "public"."knowledge_dataset"."title" IS '知识库标题';
COMMENT ON COLUMN "public"."knowledge_dataset"."description" IS '知识库描述';
COMMENT ON COLUMN "public"."knowledge_dataset"."user_id" IS '创建人id';
COMMENT ON COLUMN "public"."knowledge_dataset"."type" IS '类型 1:通用 2:web站点';
COMMENT ON COLUMN "public"."knowledge_dataset"."embedding_mode_id" IS '模型的uuid';
COMMENT ON COLUMN "public"."knowledge_dataset"."create_time" IS '创建时间';
COMMENT ON COLUMN "public"."knowledge_dataset"."update_time" IS '更新时间';
COMMENT ON TABLE "public"."knowledge_dataset" IS '知识库表';


CREATE TABLE "public"."knowledge_document" (
    "name" varchar(255) COLLATE "pg_catalog"."default" DEFAULT ''::character varying,
    "uuid" varchar(64) COLLATE "pg_catalog"."default" DEFAULT ''::character varying,
    "file_size" int4 DEFAULT 0,
    "status" int2 DEFAULT 1,
    "question_status" int2 DEFAULT 1,
    "active" int2 DEFAULT 1,
    "dataset_id" varchar(64) COLLATE "pg_catalog"."default" DEFAULT ''::character varying,
    "status_meta" varchar(500) COLLATE "pg_catalog"."default",
    "create_time" timestamp(6),
    "update_time" timestamp(6)
);

ALTER TABLE "public"."knowledge_document"
    OWNER TO "postgres";

CREATE INDEX "idx_dataset" ON "public"."knowledge_document" USING btree (
    "dataset_id" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST
    );

COMMENT ON COLUMN "public"."knowledge_document"."name" IS '文件名称';
COMMENT ON COLUMN "public"."knowledge_document"."uuid" IS '唯一标识';
COMMENT ON COLUMN "public"."knowledge_document"."file_size" IS '字符长度';
COMMENT ON COLUMN "public"."knowledge_document"."status" IS '状态 1:待索引 2:索引中 3:索引完成';
COMMENT ON COLUMN "public"."knowledge_document"."question_status" IS '生成问题状态 1:待生成 2:生成中 3:生成完成';
COMMENT ON COLUMN "public"."knowledge_document"."active" IS '状态 1:正常 2:禁用';
COMMENT ON COLUMN "public"."knowledge_document"."dataset_id" IS '所属知识库';
COMMENT ON COLUMN "public"."knowledge_document"."status_meta" IS '状态json数据';
COMMENT ON COLUMN "public"."knowledge_document"."create_time" IS '创建时间';
COMMENT ON COLUMN "public"."knowledge_document"."update_time" IS '更新时间';
COMMENT ON TABLE "public"."knowledge_document" IS '知识库文档表';


CREATE TABLE "public"."knowledge_embedding" (
    "uuid" varchar(64) COLLATE "pg_catalog"."default" DEFAULT ''::character varying,
    "dataset_id" varchar(64) COLLATE "pg_catalog"."default" DEFAULT ''::character varying,
    "document_id" varchar(64) COLLATE "pg_catalog"."default" DEFAULT ''::character varying,
    "paragraph_id" varchar(64) COLLATE "pg_catalog"."default" DEFAULT ''::character varying,
    "embedding" "public"."vector",
    "search_vector" tsvector,
    "active" int2 DEFAULT 1,
    "source_type" int2,
    "source_id" varchar(64) COLLATE "pg_catalog"."default" DEFAULT ''::character varying,
    "create_time" timestamp(6),
    "update_time" timestamp(6)
);

ALTER TABLE "public"."knowledge_embedding"
    OWNER TO "postgres";

CREATE INDEX "idx_dataset_y" ON "public"."knowledge_embedding" USING btree (
    "dataset_id" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST
    );

CREATE INDEX "idx_document_y" ON "public"."knowledge_embedding" USING btree (
    "document_id" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST
    );

CREATE INDEX "idx_paragraph_y" ON "public"."knowledge_embedding" USING btree (
    "paragraph_id" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST
    );

COMMENT ON COLUMN "public"."knowledge_embedding"."uuid" IS '唯一标识';
COMMENT ON COLUMN "public"."knowledge_embedding"."dataset_id" IS '所属的知识库';
COMMENT ON COLUMN "public"."knowledge_embedding"."document_id" IS '所属文档';
COMMENT ON COLUMN "public"."knowledge_embedding"."paragraph_id" IS '所属段落';
COMMENT ON COLUMN "public"."knowledge_embedding"."embedding" IS '向量数据';
COMMENT ON COLUMN "public"."knowledge_embedding"."search_vector" IS '全文索引';
COMMENT ON COLUMN "public"."knowledge_embedding"."active" IS '状态 1:正常 2:禁用';
COMMENT ON COLUMN "public"."knowledge_embedding"."source_type" IS '来源 1:文档 2:问题';
COMMENT ON COLUMN "public"."knowledge_embedding"."source_id" IS '来源id';
COMMENT ON COLUMN "public"."knowledge_embedding"."create_time" IS '创建时间';
COMMENT ON COLUMN "public"."knowledge_embedding"."update_time" IS '更新时间';
COMMENT ON TABLE "public"."knowledge_embedding" IS '向量索引表';


CREATE TABLE "public"."knowledge_paragraph" (
    "uuid" varchar(64) COLLATE "pg_catalog"."default" DEFAULT ''::character varying,
    "title" varchar(255) COLLATE "pg_catalog"."default" DEFAULT ''::character varying,
    "content" varchar(8000) COLLATE "pg_catalog"."default" DEFAULT ''::character varying,
    "dataset_id" varchar(64) COLLATE "pg_catalog"."default" DEFAULT ''::character varying,
    "document_id" varchar(64) COLLATE "pg_catalog"."default" DEFAULT ''::character varying,
    "status" int2 DEFAULT 1,
    "active" int2 DEFAULT 1,
    "status_meta" varchar(500) COLLATE "pg_catalog"."default",
    "create_time" timestamp(6),
    "update_time" timestamp(6)
);

ALTER TABLE "public"."knowledge_paragraph"
    OWNER TO "postgres";

CREATE INDEX "idx_dataset_x" ON "public"."knowledge_paragraph" USING btree (
    "dataset_id" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST
    );

CREATE INDEX "idx_document" ON "public"."knowledge_paragraph" USING btree (
    "document_id" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST
    );

COMMENT ON COLUMN "public"."knowledge_paragraph"."uuid" IS '唯一标识';
COMMENT ON COLUMN "public"."knowledge_paragraph"."title" IS '段落标题';
COMMENT ON COLUMN "public"."knowledge_paragraph"."content" IS '段落内容';
COMMENT ON COLUMN "public"."knowledge_paragraph"."dataset_id" IS '知识库id';
COMMENT ON COLUMN "public"."knowledge_paragraph"."document_id" IS '文档id';
COMMENT ON COLUMN "public"."knowledge_paragraph"."status" IS '状态 1:待索引 2:索引中 3:索引完成';
COMMENT ON COLUMN "public"."knowledge_paragraph"."active" IS '状态 1:正常 2:禁用';
COMMENT ON COLUMN "public"."knowledge_paragraph"."status_meta" IS '状态数据';
COMMENT ON COLUMN "public"."knowledge_paragraph"."create_time" IS '创建时间';
COMMENT ON COLUMN "public"."knowledge_paragraph"."update_time" IS '更新时间';
COMMENT ON TABLE "public"."knowledge_paragraph" IS '文档段落表';


CREATE TABLE "public"."knowledge_question" (
    "uuid" varchar(64) COLLATE "pg_catalog"."default" DEFAULT ''::character varying,
    "content" varchar(255) COLLATE "pg_catalog"."default" DEFAULT ''::character varying,
    "hit_nums" int4 DEFAULT 0,
    "dataset_id" varchar(64) COLLATE "pg_catalog"."default" DEFAULT ''::character varying,
    "create_time" timestamp(6),
    "update_time" timestamp(6)
);

ALTER TABLE "public"."knowledge_question"
    OWNER TO "postgres";

CREATE INDEX "idx_dataset_z" ON "public"."knowledge_question" USING btree (
    "dataset_id" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST
    );

COMMENT ON COLUMN "public"."knowledge_question"."uuid" IS '唯一标识';
COMMENT ON COLUMN "public"."knowledge_question"."content" IS '问题内容';
COMMENT ON COLUMN "public"."knowledge_question"."hit_nums" IS '命中次数';
COMMENT ON COLUMN "public"."knowledge_question"."dataset_id" IS '所属知识库';
COMMENT ON COLUMN "public"."knowledge_question"."create_time" IS '创建时间';
COMMENT ON COLUMN "public"."knowledge_question"."update_time" IS '更新时间';
COMMENT ON TABLE "public"."knowledge_question" IS '知识库问题表';


CREATE TABLE "public"."knowledge_question_paragraph" (
    "dataset_id" varchar(64) COLLATE "pg_catalog"."default" DEFAULT ''::character varying,
    "document_id" varchar(64) COLLATE "pg_catalog"."default" DEFAULT ''::character varying,
    "paragraph_id" varchar(64) COLLATE "pg_catalog"."default" DEFAULT ''::character varying,
    "question_id" varchar(64) COLLATE "pg_catalog"."default",
    "create_time" timestamp(6),
    "update_time" timestamp(6),
    "uuid" varchar(64) COLLATE "pg_catalog"."default"
);

ALTER TABLE "public"."knowledge_question_paragraph"
    OWNER TO "postgres";

CREATE INDEX "idx_dataset_k" ON "public"."knowledge_question_paragraph" USING btree (
    "dataset_id" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST
    );

CREATE INDEX "idx_document_k" ON "public"."knowledge_question_paragraph" USING btree (
    "document_id" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST
    );

CREATE INDEX "idx_paragraph_k" ON "public"."knowledge_question_paragraph" USING btree (
    "paragraph_id" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST
    );

CREATE INDEX "idx_question_k" ON "public"."knowledge_question_paragraph" USING btree (
    "question_id" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST
    );

COMMENT ON COLUMN "public"."knowledge_question_paragraph"."dataset_id" IS '关联的知识库';
COMMENT ON COLUMN "public"."knowledge_question_paragraph"."document_id" IS '关联的文档';
COMMENT ON COLUMN "public"."knowledge_question_paragraph"."paragraph_id" IS '关联的段落';
COMMENT ON COLUMN "public"."knowledge_question_paragraph"."question_id" IS '关联的问题';
COMMENT ON COLUMN "public"."knowledge_question_paragraph"."create_time" IS '创建时间';
COMMENT ON COLUMN "public"."knowledge_question_paragraph"."update_time" IS '更新时间';
COMMENT ON COLUMN "public"."knowledge_question_paragraph"."uuid" IS '唯一标识';
COMMENT ON TABLE "public"."knowledge_question_paragraph" IS '段落问题关联表';


CREATE TABLE "public"."system_users" (
    "name" varchar(155) COLLATE "pg_catalog"."default" DEFAULT ''::character varying,
    "uuid" varchar(64) COLLATE "pg_catalog"."default" DEFAULT ''::character varying,
    "nickname" varchar(155) COLLATE "pg_catalog"."default" DEFAULT ''::character varying,
    "avatar" varchar(155) COLLATE "pg_catalog"."default" DEFAULT ''::character varying,
    "password" varchar(64) COLLATE "pg_catalog"."default" DEFAULT ''::character varying,
    "salt" varchar(64) COLLATE "pg_catalog"."default" DEFAULT ''::character varying,
    "deleted" int2 DEFAULT 1,
    "status" int2 DEFAULT 1,
    "create_time" timestamp(6),
    "update_time" timestamp(0)
);

ALTER TABLE "public"."system_users"
    OWNER TO "postgres";

COMMENT ON COLUMN "public"."system_users"."name" IS '登录账号';
COMMENT ON COLUMN "public"."system_users"."uuid" IS '唯一编码';
COMMENT ON COLUMN "public"."system_users"."nickname" IS '昵称';
COMMENT ON COLUMN "public"."system_users"."avatar" IS '头像';
COMMENT ON COLUMN "public"."system_users"."password" IS '密码';
COMMENT ON COLUMN "public"."system_users"."salt" IS '加密盐';
COMMENT ON COLUMN "public"."system_users"."deleted" IS '是否删除 1:正常 2:删除';
COMMENT ON COLUMN "public"."system_users"."status" IS '状态 1:正常 2:禁用';
COMMENT ON COLUMN "public"."system_users"."create_time" IS '创建时间';
COMMENT ON COLUMN "public"."system_users"."update_time" IS '更新时间';
COMMENT ON TABLE "public"."system_users" IS '系统用户表';