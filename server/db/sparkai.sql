-- 需要先安装pgvector这个扩展（https://github.com/pgvector/pgvector）
-- CREATE EXTENSION vector;

SET client_encoding = 'UTF8';
CREATE SCHEMA public;

CREATE TABLE "public"."knowledge_dataset" (
    "dataset_id" VARCHAR (64) COLLATE "pg_catalog"."default",
    "title" VARCHAR (155) COLLATE "pg_catalog"."default" DEFAULT '' :: CHARACTER VARYING,
    "description" VARCHAR (255) COLLATE "pg_catalog"."default" DEFAULT '' :: CHARACTER VARYING,
    "user_id" VARCHAR (64) COLLATE "pg_catalog"."default" DEFAULT 0,
    "type" INT2 DEFAULT 1,
    "embedding_model_id" VARCHAR (64) COLLATE "pg_catalog"."default",
    "embedding_model" VARCHAR (64) COLLATE "pg_catalog"."default" DEFAULT '' :: CHARACTER VARYING,
    "create_time" TIMESTAMP (6),
    "update_time" TIMESTAMP (6)
);
ALTER TABLE "public"."knowledge_dataset" OWNER TO "postgres";
COMMENT ON COLUMN "public"."knowledge_dataset"."dataset_id" IS '唯一标识';
COMMENT ON COLUMN "public"."knowledge_dataset"."title" IS '知识库标题';
COMMENT ON COLUMN "public"."knowledge_dataset"."description" IS '知识库描述';
COMMENT ON COLUMN "public"."knowledge_dataset"."user_id" IS '创建人id';
COMMENT ON COLUMN "public"."knowledge_dataset"."type" IS '类型 1:通用 2:web站点';
COMMENT ON COLUMN "public"."knowledge_dataset"."embedding_model_id" IS '模型的uuid';
COMMENT ON COLUMN "public"."knowledge_dataset"."embedding_model" IS '模型名称';
COMMENT ON COLUMN "public"."knowledge_dataset"."create_time" IS '创建时间';
COMMENT ON COLUMN "public"."knowledge_dataset"."update_time" IS '更新时间';
COMMENT ON TABLE "public"."knowledge_dataset" IS '知识库表';


CREATE TABLE "public"."knowledge_document" (
    "document_id" varchar(64) COLLATE "pg_catalog"."default" DEFAULT ''::character varying,
    "name" varchar(255) COLLATE "pg_catalog"."default" DEFAULT ''::character varying,
    "file_size" int4 DEFAULT 0,
    "status" int2 DEFAULT 1,
    "question_status" int2 DEFAULT 1,
    "active" int2 DEFAULT 1,
    "dataset_id" varchar(64) COLLATE "pg_catalog"."default" DEFAULT ''::character varying,
    "paragraph_num" int4 DEFAULT 0,
    "embedding_time" timestamp(6),
    "question_time" timestamp(6),
    "answer_type" varchar(55) COLLATE "pg_catalog"."default" DEFAULT ''::character varying,
    "redirect_similar" numeric(10,3) DEFAULT 0.900,
    "create_time" timestamp(6),
    "update_time" timestamp(6)
);

ALTER TABLE "public"."knowledge_document"
    OWNER TO "postgres";

CREATE INDEX "idx_dataset" ON "public"."knowledge_document" USING btree (
    "dataset_id" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST
    );

COMMENT ON COLUMN "public"."knowledge_document"."document_id" IS '唯一标识';
COMMENT ON COLUMN "public"."knowledge_document"."name" IS '文件名称';
COMMENT ON COLUMN "public"."knowledge_document"."file_size" IS '字符长度';
COMMENT ON COLUMN "public"."knowledge_document"."status" IS '状态 1:待索引 2:索引中 3:索引完成';
COMMENT ON COLUMN "public"."knowledge_document"."question_status" IS '生成问题状态 1:待生成 2:生成中 3:生成完成';
COMMENT ON COLUMN "public"."knowledge_document"."active" IS '状态 1:正常 2:禁用';
COMMENT ON COLUMN "public"."knowledge_document"."dataset_id" IS '所属知识库';
COMMENT ON COLUMN "public"."knowledge_document"."paragraph_num" IS '段落数';
COMMENT ON COLUMN "public"."knowledge_document"."embedding_time" IS '向量化时间';
COMMENT ON COLUMN "public"."knowledge_document"."question_time" IS '生成问题时间';
COMMENT ON COLUMN "public"."knowledge_document"."answer_type" IS '命中处理方式';
COMMENT ON COLUMN "public"."knowledge_document"."redirect_similar" IS '返回相似度';
COMMENT ON COLUMN "public"."knowledge_document"."create_time" IS '创建时间';
COMMENT ON COLUMN "public"."knowledge_document"."update_time" IS '更新时间';
COMMENT ON TABLE "public"."knowledge_document" IS '知识库文档表';


CREATE TABLE "public"."knowledge_embedding" (
    "embedding_id" varchar(64) COLLATE "pg_catalog"."default" DEFAULT ''::character varying,
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

COMMENT ON COLUMN "public"."knowledge_embedding"."embedding_id" IS '唯一标识';
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
    "paragraph_id" varchar(64) COLLATE "pg_catalog"."default" DEFAULT ''::character varying,
    "title" varchar(255) COLLATE "pg_catalog"."default" DEFAULT ''::character varying,
    "content" varchar(8000) COLLATE "pg_catalog"."default" DEFAULT ''::character varying,
    "dataset_id" varchar(64) COLLATE "pg_catalog"."default" DEFAULT ''::character varying,
    "document_id" varchar(64) COLLATE "pg_catalog"."default" DEFAULT ''::character varying,
    "status" int2 DEFAULT 1,
    "active" int2 DEFAULT 1,
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

COMMENT ON COLUMN "public"."knowledge_paragraph"."paragraph_id" IS '唯一标识';
COMMENT ON COLUMN "public"."knowledge_paragraph"."title" IS '段落标题';
COMMENT ON COLUMN "public"."knowledge_paragraph"."content" IS '段落内容';
COMMENT ON COLUMN "public"."knowledge_paragraph"."dataset_id" IS '知识库id';
COMMENT ON COLUMN "public"."knowledge_paragraph"."document_id" IS '文档id';
COMMENT ON COLUMN "public"."knowledge_paragraph"."status" IS '状态 1:待索引 2:索引中 3:索引完成';
COMMENT ON COLUMN "public"."knowledge_paragraph"."active" IS '状态 1:正常 2:禁用';
COMMENT ON COLUMN "public"."knowledge_paragraph"."create_time" IS '创建时间';
COMMENT ON COLUMN "public"."knowledge_paragraph"."update_time" IS '更新时间';
COMMENT ON TABLE "public"."knowledge_paragraph" IS '文档段落表';


CREATE TABLE "public"."knowledge_question" (
    "question_id" varchar(64) COLLATE "pg_catalog"."default" DEFAULT ''::character varying,
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

COMMENT ON COLUMN "public"."knowledge_question"."question_id" IS '唯一标识';
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
    "user_id" varchar(64) COLLATE "pg_catalog"."default" DEFAULT ''::character varying,
    "name" varchar(155) COLLATE "pg_catalog"."default" DEFAULT ''::character varying,
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

COMMENT ON COLUMN "public"."system_users"."user_id" IS '唯一编码';
COMMENT ON COLUMN "public"."system_users"."name" IS '登录账号';
COMMENT ON COLUMN "public"."system_users"."nickname" IS '昵称';
COMMENT ON COLUMN "public"."system_users"."avatar" IS '头像';
COMMENT ON COLUMN "public"."system_users"."password" IS '密码';
COMMENT ON COLUMN "public"."system_users"."salt" IS '加密盐';
COMMENT ON COLUMN "public"."system_users"."deleted" IS '是否删除 1:正常 2:删除';
COMMENT ON COLUMN "public"."system_users"."status" IS '状态 1:正常 2:禁用';
COMMENT ON COLUMN "public"."system_users"."create_time" IS '创建时间';
COMMENT ON COLUMN "public"."system_users"."update_time" IS '更新时间';
COMMENT ON TABLE "public"."system_users" IS '系统用户表';


CREATE TABLE "public"."application" (
    "app_id" VARCHAR (64) COLLATE "pg_catalog"."default" NOT NULL,
    "access_token" VARCHAR (64) COLLATE "pg_catalog"."default" DEFAULT '' :: CHARACTER VARYING,
    "name" VARCHAR (155) COLLATE "pg_catalog"."default" DEFAULT '' :: CHARACTER VARYING,
    "description" VARCHAR (255) COLLATE "pg_catalog"."default",
    "icon" VARCHAR (255) COLLATE "pg_catalog"."default" DEFAULT '' :: CHARACTER VARYING,
    "model_id" VARCHAR (64) COLLATE "pg_catalog"."default" DEFAULT '' :: CHARACTER VARYING,
    "model_name" VARCHAR (255) COLLATE "pg_catalog"."default" DEFAULT '' :: CHARACTER VARYING,
    "prompt" VARCHAR (255) COLLATE "pg_catalog"."default" DEFAULT '' :: CHARACTER VARYING,
    "relation_dataset" INT2 DEFAULT 2,
    "prologue" VARCHAR (500) COLLATE "pg_catalog"."default" DEFAULT '' :: CHARACTER VARYING,
    "show_relation" INT2 DEFAULT 1,
    "show_time" INT2 DEFAULT 1,
    "show_tokens" INT2 DEFAULT 1,
    "show_appraise" INT2 DEFAULT 1,
    "user_id" VARCHAR (64) COLLATE "pg_catalog"."default" DEFAULT '' :: CHARACTER VARYING,
    "show_think" INT2 DEFAULT 2,
    "voice_input" INT2 DEFAULT 2,
    "voice_out" INT2 DEFAULT 2,
    "empty_reply" INT2 DEFAULT 1,
    "reply_content" VARCHAR (255) COLLATE "pg_catalog"."default" DEFAULT '' :: CHARACTER VARYING,
    "search_mode" VARCHAR (55) COLLATE "pg_catalog"."default" DEFAULT 'embedding' :: CHARACTER VARYING,
    "similarity" NUMERIC (10, 3) DEFAULT 0.600,
    "top_rank" INT2 DEFAULT 3,
    "rerank_model_id" VARCHAR (64) COLLATE "pg_catalog"."default" DEFAULT '' :: CHARACTER VARYING,
    "memory_num" INT2 DEFAULT 2,
    "max_reply_token" INT8 DEFAULT 1024,
    "temperature" NUMERIC (10, 2) DEFAULT 3,
    "type" INT2 DEFAULT 1,
    "compressing_query" INT2 DEFAULT 2,
    "status" INT2 DEFAULT 1,
    "create_time" TIMESTAMP (6),
    "update_time" TIMESTAMP (6),
    CONSTRAINT "application_pkey" PRIMARY KEY ("app_id")
);
ALTER TABLE "public"."application" OWNER TO "postgres";
COMMENT ON COLUMN "public"."application"."app_id" IS 'id';
COMMENT ON COLUMN "public"."application"."access_token" IS '访问token';
COMMENT ON COLUMN "public"."application"."name" IS '应用名称';
COMMENT ON COLUMN "public"."application"."description" IS '应用描述';
COMMENT ON COLUMN "public"."application"."icon" IS '应用的头像';
COMMENT ON COLUMN "public"."application"."model_id" IS '使用的模型';
COMMENT ON COLUMN "public"."application"."model_name" IS '使用的模型名称';
COMMENT ON COLUMN "public"."application"."prompt" IS '提示词';
COMMENT ON COLUMN "public"."application"."relation_dataset" IS '是否关联知识库 1:关联 2:不关联';
COMMENT ON COLUMN "public"."application"."prologue" IS '开场白';
COMMENT ON COLUMN "public"."application"."show_relation" IS '显示知识库引用 1:显示 2:不显示';
COMMENT ON COLUMN "public"."application"."show_time" IS '显示耗时 1:显示 2:不显示';
COMMENT ON COLUMN "public"."application"."show_tokens" IS '显示消耗token 1:显示 2:不显示';
COMMENT ON COLUMN "public"."application"."show_appraise" IS '显示评价 1:显示 2:不显示';
COMMENT ON COLUMN "public"."application"."user_id" IS '创建人id';
COMMENT ON COLUMN "public"."application"."show_think" IS '显示思考过程 1:显示 2:不显示';
COMMENT ON COLUMN "public"."application"."voice_input" IS '语音输入 1:开启 2:关闭';
COMMENT ON COLUMN "public"."application"."voice_out" IS '语音播放 1:开启 2:关闭';
COMMENT ON COLUMN "public"."application"."empty_reply" IS '空搜索回复 1:AI 2:人工';
COMMENT ON COLUMN "public"."application"."reply_content" IS '空搜索回复内容';
COMMENT ON COLUMN "public"."application"."search_mode" IS '搜索模式：embedding,text,mix';
COMMENT ON COLUMN "public"."application"."similarity" IS '相似度';
COMMENT ON COLUMN "public"."application"."top_rank" IS '召回数量';
COMMENT ON COLUMN "public"."application"."rerank_model_id" IS '重排索引模型';
COMMENT ON COLUMN "public"."application"."memory_num" IS '记忆条数';
COMMENT ON COLUMN "public"."application"."max_reply_token" IS '回复上限';
COMMENT ON COLUMN "public"."application"."temperature" IS '回复温度';
COMMENT ON COLUMN "public"."application"."type" IS '类型 1:普通 2:编排';
COMMENT ON COLUMN "public"."application"."compressing_query" IS '问题优化 1:开启 2:关闭';
COMMENT ON COLUMN "public"."application"."status" IS '状态 1:待发布 2:已发布';
COMMENT ON COLUMN "public"."application"."create_time" IS '创建时间';
COMMENT ON COLUMN "public"."application"."update_time" IS '更新时间';
COMMENT ON TABLE "public"."application" IS '系统应用表';


CREATE TABLE "public"."application_dataset_relation" (
    "app_id" varchar(64) COLLATE "pg_catalog"."default" DEFAULT ''::character varying,
    "dataset_id" varchar(64) COLLATE "pg_catalog"."default" DEFAULT ''::character varying,
    "create_time" timestamp(6),
    "update_time" timestamp(6)
);

ALTER TABLE "public"."application_dataset_relation"
    OWNER TO "postgres";

COMMENT ON COLUMN "public"."application_dataset_relation"."app_id" IS '应用id';
COMMENT ON COLUMN "public"."application_dataset_relation"."dataset_id" IS '知识库id';
COMMENT ON COLUMN "public"."application_dataset_relation"."create_time" IS '创建时间';
COMMENT ON COLUMN "public"."application_dataset_relation"."update_time" IS '更新时间';
COMMENT ON TABLE "public"."application_dataset_relation" IS '应用知识库关联表';


CREATE TABLE "public"."application_chat_log" (
    "log_id" int4 NOT NULL GENERATED ALWAYS AS IDENTITY (
    INCREMENT 1
    MINVALUE  1
    MAXVALUE 2147483647
    START 1
    CACHE 1
    ),
    "app_id" varchar(64) COLLATE "pg_catalog"."default" DEFAULT ''::character varying,
    "user_id" varchar(64) COLLATE "pg_catalog"."default" DEFAULT ''::character varying,
    "session_id" varchar(64) COLLATE "pg_catalog"."default" DEFAULT ''::character varying,
    "question" varchar(255) COLLATE "pg_catalog"."default" DEFAULT ''::character varying,
    "content" text COLLATE "pg_catalog"."default" DEFAULT ''::text,
    "time" int4 DEFAULT 0,
    "tokens" int4 DEFAULT 0,
    "retrieved_list" text COLLATE "pg_catalog"."default" DEFAULT ''::text,
    "appraise" int4 DEFAULT 0,
    "create_time" timestamp(6),
    "update_time" timestamp(6),
    CONSTRAINT "application_chat_log_pkey" PRIMARY KEY ("log_id")
);

ALTER TABLE "public"."application_chat_log"
    OWNER TO "postgres";

COMMENT ON COLUMN "public"."application_chat_log"."log_id" IS 'id';
COMMENT ON COLUMN "public"."application_chat_log"."app_id" IS '所属应用id';
COMMENT ON COLUMN "public"."application_chat_log"."user_id" IS '聊天的用户id';
COMMENT ON COLUMN "public"."application_chat_log"."session_id" IS '所属对话id';
COMMENT ON COLUMN "public"."application_chat_log"."question" IS '问题';
COMMENT ON COLUMN "public"."application_chat_log"."content" IS '内容';
COMMENT ON COLUMN "public"."application_chat_log"."time" IS '消耗时间';
COMMENT ON COLUMN "public"."application_chat_log"."tokens" IS '消耗的token';
COMMENT ON COLUMN "public"."application_chat_log"."retrieved_list" IS '引用的知识库';
COMMENT ON COLUMN "public"."application_chat_log"."appraise" IS '评价 1:好评 2:差评';
COMMENT ON COLUMN "public"."application_chat_log"."create_time" IS '创建时间';
COMMENT ON COLUMN "public"."application_chat_log"."update_time" IS '更新时间';
COMMENT ON TABLE "public"."application_chat_log" IS '聊天日志表';


CREATE TABLE "public"."application_chat_session" (
    "session_id" varchar(64) COLLATE "pg_catalog"."default" DEFAULT ''::character varying,
    "app_id" varchar(64) COLLATE "pg_catalog"."default" DEFAULT ''::character varying,
    "title" varchar(25) COLLATE "pg_catalog"."default" DEFAULT ''::character varying,
    "user_id" varchar(64) COLLATE "pg_catalog"."default" DEFAULT ''::character varying,
    "create_time" timestamp(6),
    "update_time" timestamp(6)
);

ALTER TABLE "public"."application_chat_session"
    OWNER TO "postgres";

CREATE INDEX "idx_user_log" ON "public"."application_chat_session" USING btree (
    "app_id" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST,
    "user_id" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST
    );

COMMENT ON COLUMN "public"."application_chat_session"."session_id" IS '会话id';
COMMENT ON COLUMN "public"."application_chat_session"."app_id" IS '所属应用';
COMMENT ON COLUMN "public"."application_chat_session"."title" IS '会话标题';
COMMENT ON COLUMN "public"."application_chat_session"."user_id" IS '用户id';
COMMENT ON COLUMN "public"."application_chat_session"."create_time" IS '创建时间';
COMMENT ON COLUMN "public"."application_chat_session"."update_time" IS '更新时间';
COMMENT ON TABLE "public"."application_chat_session" IS '应用会话表';


CREATE TABLE "public"."models" (
    "model_id" varchar(64) COLLATE "pg_catalog"."default" NOT NULL DEFAULT ''::character varying,
    "name" varchar(255) COLLATE "pg_catalog"."default",
    "model_flag" varchar(255) COLLATE "pg_catalog"."default",
    "type" int2 DEFAULT 0,
    "credential" varchar(500) COLLATE "pg_catalog"."default" DEFAULT ''::character varying,
    "options" varchar(500) COLLATE "pg_catalog"."default" DEFAULT ''::character varying,
    "status" int2 DEFAULT 1,
    "models" varchar(1000) COLLATE "pg_catalog"."default" DEFAULT ''::character varying,
    "icon" varchar(255) COLLATE "pg_catalog"."default" DEFAULT ''::character varying,
    "create_time" timestamp(6),
    "update_time" timestamp(6),
    CONSTRAINT "models_pkey" PRIMARY KEY ("model_id")
);

ALTER TABLE "public"."models"
    OWNER TO "postgres";

COMMENT ON COLUMN "public"."models"."model_id" IS '模型id';
COMMENT ON COLUMN "public"."models"."name" IS '模型名称';
COMMENT ON COLUMN "public"."models"."model_flag" IS '模型标识';
COMMENT ON COLUMN "public"."models"."type" IS '类型 1:语言模型 2:向量模型 3:重排模型';
COMMENT ON COLUMN "public"."models"."credential" IS '鉴权配置';
COMMENT ON COLUMN "public"."models"."options" IS '配置项';
COMMENT ON COLUMN "public"."models"."status" IS '状态1:正常 2:禁用';
COMMENT ON COLUMN "public"."models"."models" IS '可使用的模型';
COMMENT ON COLUMN "public"."models"."icon" IS '图标';
COMMENT ON COLUMN "public"."models"."create_time" IS '创建时间';
COMMENT ON COLUMN "public"."models"."update_time" IS '更新时间';
COMMENT ON TABLE "public"."models" IS '模型表';

INSERT INTO "public"."models" VALUES ('a4bc4132-d274-411d-89e2-ba7d98778754', 'SparkAI', 'sparkai', 2, '[]', '[]', 1, 'AllMiniLmL6V2Embedding', '/icons/sparkai.png', '2025-03-31 14:58:46', NULL);
INSERT INTO "public"."models" VALUES ('5f4f2e11-df8b-408d-a54b-ed271b6cf5c4', '百度千帆', 'qianfan', 1, '[{"field":"apiKey","value":""},{"field":"secretKey","value":""}]', '[{"field":"temperature","name":"温度","range":[0.01,1],"value":0.95}]', 1, 'ERNIE-Bot,ERNIE-Bot 4.0,ERNIE-Bot-8K,ERNIE-Bot-turbo,ERNIE-Speed-128K,EB-turbo-AppBuilder,Yi-34B-Chat,BLOOMZ-7B,Qianfan-BLOOMZ-7B-compressed,Mixtral-8x7B-Instruct,Llama-2-7b-chat,Llama-2-13b-chat,Llama-2-70b-chat,Qianfan-Chinese-Llama-2-7B,ChatGLM2-6B-32K,AquilaChat-7B', '/icons/baidu.png', '2025-03-30 21:22:35', '2025-04-01 10:53:15');
INSERT INTO "public"."models" VALUES ('5f6f2e21-df9b-418d-a54b-ed271g6cf6c5', '智普AI', 'zhipu', 1, '[{"field":"apiKey","value":""}]', '[{"field":"temperature","name":"温度","range":[0.01,1],"value":0.95}]', 1, 'glm-4,glm-4v,glm-4-0520,glm-4-air,glm-4-airx,glm-4-flash,glm-3-turbo,chatglm_turbo', '/icons/zhipu.png', '2025-07-01 10:36:08', NULL);
INSERT INTO "public"."models" VALUES ('5f6f2e31-df9b-418d-a56b-ed271g8cf7c6', '通义千问', 'qwen', 1, '[{"field":"apiKey","value":""}]', '[{"field":"temperature","name":"温度","range":[0.01,1],"value":0.95}]', 1, 'qwen-plus,qwen-turbo,qwen-max,qwen-long', '/icons/qwen.png', '2025-07-01 13:56:11', NULL);
INSERT INTO "public"."models" VALUES ('5f7f2e32-df7b-428d-a56b-ed272f9cf7c8', '字节豆包', 'doubao', 1, '[{"field":"apiKey","value":""}]', '[{"field":"temperature","name":"温度","range":[0.01,1],"value":0.95}, {"field":"url","name":"模型地址","value": "https://ark.cn-beijing.volces.com/api/v3"}]', 1, 'doubao-1-5-pro-32k-250115,doubao-1-5-pro-256k-250115', '/icons/doubao.png', '2025-07-02 10:40:08', NULL);
INSERT INTO "public"."models" VALUES ('5f6f2e31-df9b-419d-a56b-ed281g6cf8c9', 'GPT', 'gpt', 1, '[{"field":"apiKey","value":""}]', '[{"field":"temperature","name":"温度","range":[0.01,1],"value":0.95},{"field":"url","name":"模型地址","value": "https://api.fireaigc.cn/v1"}]', 1, 'gpt-3.5-turbo,gpt-4o', '/icons/gpt.png', '2025-07-02 13:48:13', NULL);
INSERT INTO "public"."models" VALUES ('a4bc5132-d374-411d-89e2-ba8d98778865', '智普AI', 'zhipu', 2, '[{"field":"apiKey","value":""}]', '[]', 1, 'embedding-2,embedding-3', '/icons/zhipu.png', '2025-03-31 14:58:46', NULL);
INSERT INTO "public"."models" VALUES ('5f6f2e33-df9c-429d-a57b-ed282g6cf9c9', 'GPT', 'gpt', 2, '[{"field":"apiKey","value":""}]', '[{"field":"url","name":"模型地址","value": "https://api.fireaigc.cn/v1"}]', 1, 'text-embedding-004,text-embedding-3-large,text-embedding-3-small,text-embedding-ada-002,text-embedding-v1', '/icons/gpt.png', '2025-07-02 13:48:13', NULL);
INSERT INTO "public"."models" VALUES ('5f6f2e36-df9d-429d-a58c-ed282g6cf8d2', '百度千帆', 'qianfan', 2, '[{"field":"apiKey","value":""},{"field":"secretKey","value":""}]', '[]', 1, 'embedding-v1', '/icons/baidu.png', '2025-03-30 21:22:35', '2025-04-01 10:53:15');

CREATE TABLE "public"."application_workflow" (
    "id" INT8 NOT NULL GENERATED ALWAYS AS IDENTITY (INCREMENT 1 MINVALUE 1 MAXVALUE 9223372036854775807 START 1 CACHE 1),
    "app_id" VARCHAR (64) COLLATE "pg_catalog"."default" DEFAULT '' :: CHARACTER VARYING,
    "flow_data" TEXT COLLATE "pg_catalog"."default" DEFAULT '' :: TEXT,
    "create_time" TIMESTAMP (6),
    "update_time" TIMESTAMP (6),
    CONSTRAINT "application_workflow_pkey" PRIMARY KEY ("id")
);
ALTER TABLE "public"."application_workflow" OWNER TO "postgres";
CREATE INDEX "idx_app" ON "public"."application_workflow" USING btree ("app_id" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST);
COMMENT ON COLUMN "public"."application_workflow"."id" IS 'id';
COMMENT ON COLUMN "public"."application_workflow"."app_id" IS '应用id';
COMMENT ON COLUMN "public"."application_workflow"."flow_data" IS '设计的数据';
COMMENT ON COLUMN "public"."application_workflow"."create_time" IS '创建时间';
COMMENT ON COLUMN "public"."application_workflow"."update_time" IS '更新时间';
COMMENT ON TABLE "public"."application_workflow" IS '编排流程表';

CREATE TABLE "public"."application_workflow_runtime" (
    "id" INT4 NOT NULL GENERATED ALWAYS AS IDENTITY (INCREMENT 1 MINVALUE 1 MAXVALUE 2147483647 START 1 CACHE 1),
    "user_id" VARCHAR (64) COLLATE "pg_catalog"."default" DEFAULT '' :: CHARACTER VARYING,
    "flow_id" INT4 DEFAULT 0,
    "title" VARCHAR (255) COLLATE "pg_catalog"."default" DEFAULT '' :: CHARACTER VARYING,
    "create_time" TIMESTAMP (6),
    "update_time" TIMESTAMP (6),
    CONSTRAINT "application_workflow_runtime_pkey" PRIMARY KEY ("id")
);
ALTER TABLE "public"."application_workflow_runtime" OWNER TO "postgres";
COMMENT ON COLUMN "public"."application_workflow_runtime"."id" IS '主键';
COMMENT ON COLUMN "public"."application_workflow_runtime"."user_id" IS '用户id';
COMMENT ON COLUMN "public"."application_workflow_runtime"."flow_id" IS '关联的流程id';
COMMENT ON COLUMN "public"."application_workflow_runtime"."title" IS '首个问题';
COMMENT ON COLUMN "public"."application_workflow_runtime"."create_time" IS '创建时间';
COMMENT ON COLUMN "public"."application_workflow_runtime"."update_time" IS '更新时间';
COMMENT ON TABLE "public"."application_workflow_runtime" IS '流程运行时';

CREATE TABLE "public"."application_workflow_runtime_context" (
    "id" INT4 NOT NULL GENERATED ALWAYS AS IDENTITY (INCREMENT 1 MINVALUE 1 MAXVALUE 2147483647 START 1 CACHE 1),
    "runtime_id" INT4 DEFAULT 0,
    "node_type" VARCHAR (55) COLLATE "pg_catalog"."default" DEFAULT '' :: CHARACTER VARYING,
    "step" INT2 DEFAULT 0,
    "output_data" TEXT COLLATE "pg_catalog"."default",
    "model_data" TEXT COLLATE "pg_catalog"."default",
    "cell" VARCHAR (155) COLLATE "pg_catalog"."default" DEFAULT '' :: CHARACTER VARYING,
    "create_time" TIMESTAMP (6),
    "update_time" TIMESTAMP (6),
    CONSTRAINT "application_workflow_runtime_context_pkey" PRIMARY KEY ("id")
);
ALTER TABLE "public"."application_workflow_runtime_context" OWNER TO "postgres";
COMMENT ON COLUMN "public"."application_workflow_runtime_context"."id" IS '主键';
COMMENT ON COLUMN "public"."application_workflow_runtime_context"."runtime_id" IS '运行时id';
COMMENT ON COLUMN "public"."application_workflow_runtime_context"."node_type" IS '节点类型';
COMMENT ON COLUMN "public"."application_workflow_runtime_context"."step" IS '步骤号';
COMMENT ON COLUMN "public"."application_workflow_runtime_context"."output_data" IS '出参数据';
COMMENT ON COLUMN "public"."application_workflow_runtime_context"."model_data" IS '模型数据';
COMMENT ON COLUMN "public"."application_workflow_runtime_context"."cell" IS '节点id';
COMMENT ON COLUMN "public"."application_workflow_runtime_context"."create_time" IS '创建时间';
COMMENT ON COLUMN "public"."application_workflow_runtime_context"."update_time" IS '更新时间';
COMMENT ON TABLE "public"."application_workflow_runtime_context" IS '工作流运行时上下文';

CREATE TABLE "public"."system_team" (
    "team_id" INT4 NOT NULL GENERATED ALWAYS AS IDENTITY (INCREMENT 1 MINVALUE 1 MAXVALUE 2147483647 START 1 CACHE 1),
    "team_code" VARCHAR (64) COLLATE "pg_catalog"."default" DEFAULT '' :: CHARACTER VARYING,
    "user_id" VARCHAR (64) COLLATE "pg_catalog"."default" DEFAULT '' :: CHARACTER VARYING,
    "create_time" TIMESTAMP (6),
    "update_time" TIMESTAMP (6),
    CONSTRAINT "system_team_pkey" PRIMARY KEY ("team_id")
);
ALTER TABLE "public"."system_team" OWNER TO "postgres";
COMMENT ON COLUMN "public"."system_team"."team_id" IS '团队id';
COMMENT ON COLUMN "public"."system_team"."team_code" IS '团队编码';
COMMENT ON COLUMN "public"."system_team"."user_id" IS '团队管理员';
COMMENT ON COLUMN "public"."system_team"."create_time" IS '创建时间';
COMMENT ON COLUMN "public"."system_team"."update_time" IS '更新时间';
COMMENT ON TABLE "public"."system_team" IS '团队表';

CREATE TABLE "public"."system_team_user" (
    "id" INT4 NOT NULL GENERATED ALWAYS AS IDENTITY (INCREMENT 1 MINVALUE 1 MAXVALUE 2147483647 START 1 CACHE 1),
    "team_id" INT4 DEFAULT 0,
    "user_id" VARCHAR (64) COLLATE "pg_catalog"."default" DEFAULT '' :: CHARACTER VARYING,
    "dataset_permission" TEXT COLLATE "pg_catalog"."default" DEFAULT '' :: TEXT,
    "app_permission" TEXT COLLATE "pg_catalog"."default" DEFAULT '' :: TEXT,
    "create_time" TIMESTAMP (6),
    "update_time" TIMESTAMP (6),
    CONSTRAINT "system_team_user_pkey" PRIMARY KEY ("id")
);
ALTER TABLE "public"."system_team_user" OWNER TO "postgres";
COMMENT ON COLUMN "public"."system_team_user"."id" IS 'id';
COMMENT ON COLUMN "public"."system_team_user"."team_id" IS '团队id';
COMMENT ON COLUMN "public"."system_team_user"."user_id" IS '用户id';
COMMENT ON COLUMN "public"."system_team_user"."dataset_permission" IS '知识库权限';
COMMENT ON COLUMN "public"."system_team_user"."app_permission" IS '应用权限';
COMMENT ON COLUMN "public"."system_team_user"."create_time" IS '创建时间';
COMMENT ON COLUMN "public"."system_team_user"."update_time" IS '更新时间';
COMMENT ON TABLE "public"."system_team_user" IS '团队用户表';

CREATE TABLE "public"."application_customer" (
    "customer_id" VARCHAR (64) COLLATE "pg_catalog"."default" NOT NULL DEFAULT '' :: CHARACTER VARYING,
    "customer_ip" VARCHAR (15) COLLATE "pg_catalog"."default" DEFAULT '' :: CHARACTER VARYING,
    "app_token" VARCHAR (64) COLLATE "pg_catalog"."default" DEFAULT '' :: CHARACTER VARYING,
    "create_time" TIMESTAMP (6),
    "update_time" TIMESTAMP (6),
    CONSTRAINT "application_customer_pkey" PRIMARY KEY ("customer_id")
);
ALTER TABLE "public"."application_customer" OWNER TO "postgres";
COMMENT ON COLUMN "public"."application_customer"."customer_id" IS '访客标识';
COMMENT ON COLUMN "public"."application_customer"."customer_ip" IS '访客的ip';
COMMENT ON COLUMN "public"."application_customer"."app_token" IS '关联的应用token';
COMMENT ON COLUMN "public"."application_customer"."create_time" IS '创建时间';
COMMENT ON COLUMN "public"."application_customer"."update_time" IS '更新时间';
COMMENT ON TABLE "public"."application_customer" IS '应用访客';


CREATE TABLE "public"."system_tokens" (
    "id" INT4 NOT NULL GENERATED ALWAYS AS IDENTITY (INCREMENT 1 MINVALUE 1 MAXVALUE 2147483647 START 1 CACHE 1),
    "source" VARCHAR (55) COLLATE "pg_catalog"."default" DEFAULT '' :: CHARACTER VARYING,
    "platform" VARCHAR (155) COLLATE "pg_catalog"."default" DEFAULT '' :: CHARACTER VARYING,
    "input_token" INT4 DEFAULT 0,
    "output_token" INT4 DEFAULT 0,
    "total_token" INT4,
    "create_time" TIMESTAMP (6),
    "update_time" TIMESTAMP (6),
    CONSTRAINT "system_tokens_pkey" PRIMARY KEY ("id")
);
ALTER TABLE "public"."system_tokens" OWNER TO "postgres";
COMMENT ON COLUMN "public"."system_tokens"."id" IS 'id';
COMMENT ON COLUMN "public"."system_tokens"."source" IS '消耗来源';
COMMENT ON COLUMN "public"."system_tokens"."platform" IS '模型平台';
COMMENT ON COLUMN "public"."system_tokens"."input_token" IS '输入token数';
COMMENT ON COLUMN "public"."system_tokens"."output_token" IS '输出token数';
COMMENT ON COLUMN "public"."system_tokens"."total_token" IS '累计消耗数';
COMMENT ON COLUMN "public"."system_tokens"."create_time" IS '创建时间';
COMMENT ON COLUMN "public"."system_tokens"."update_time" IS '更新时间';
COMMENT ON TABLE "public"."system_tokens" IS '系统消耗token表';