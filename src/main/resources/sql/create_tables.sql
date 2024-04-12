CREATE TABLE roles (
	id VARCHAR(40) NOT NULL PRIMARY KEY,
	created timestamp NOT NULL,
    created_by VARCHAR(40) NOT NULL,
    modified timestamp NOT NULL,
    modified_by VARCHAR(40) NOT NULL,
	name VARCHAR(32) DEFAULT NULL
);

CREATE TABLE users (
	id VARCHAR(40) NOT NULL PRIMARY KEY,
	created timestamp DEFAULT NULL,
	created_by VARCHAR(40) NULL DEFAULT NULL,
	modified timestamp DEFAULT NULL,
	modified_by VARCHAR(40) DEFAULT NULL,
	login VARCHAR(32) DEFAULT NULL,
	password VARCHAR(128) DEFAULT NULL,
	first_name VARCHAR(64) DEFAULT NULL,
	last_name VARCHAR(64) DEFAULT NULL,
	email VARCHAR(64) DEFAULT NULL,
	consent timestamp DEFAULT NULL
);

CREATE TABLE user_role (
	user_id VARCHAR(40) NOT NULL,
	role_id VARCHAR(40) NOT NULL
);

ALTER TABLE user_role ADD CONSTRAINT FK_role_id FOREIGN KEY (role_id) REFERENCES roles(id);
ALTER TABLE user_role ADD CONSTRAINT FK_user_id FOREIGN KEY (user_id) REFERENCES users(id);

CREATE TABLE categories (
	id VARCHAR(40) NOT NULL PRIMARY KEY,
	valid_from timestamp DEFAULT NULL,
	valid_to timestamp DEFAULT NULL,
	created timestamp DEFAULT NULL,
	created_by VARCHAR(40) NULL DEFAULT NULL,
	modified timestamp DEFAULT NULL,
	modified_by VARCHAR(40) DEFAULT NULL,
	slug VARCHAR(128) DEFAULT NULL,
	title VARCHAR(128) DEFAULT NULL,
	description VARCHAR(1000) DEFAULT NULL
);

CREATE TABLE articles (
	id VARCHAR(40) NOT NULL PRIMARY KEY,
	external_id VARCHAR(64) DEFAULT NULL,
	valid_from timestamp DEFAULT NULL,
	valid_to timestamp DEFAULT NULL,
	created timestamp DEFAULT NULL,
	created_by VARCHAR(40) NULL DEFAULT NULL,
	modified timestamp DEFAULT NULL,
	modified_by VARCHAR(40) DEFAULT NULL,
	slug VARCHAR(128) DEFAULT NULL,
	title VARCHAR(128) DEFAULT NULL,
	image VARCHAR(128) DEFAULT NULL,
	perex VARCHAR(4000) DEFAULT NULL,
	body TEXT,
	body_markdown TEXT,
	keywords VARCHAR(256) DEFAULT NULL,
	show_on_blog boolean DEFAULT TRUE
);

CREATE TABLE article_category (
	article_id VARCHAR(40) NOT NULL,
	category_id VARCHAR(40) NOT NULL
);

CREATE TABLE media (
    id VARCHAR(128) NOT NULL PRIMARY KEY,
    filename VARCHAR(128) NOT NULL,
    content_type VARCHAR(40) DEFAULT NULL,
    size integer DEFAULT NULL,
    data bytea,
    private_access boolean DEFAULT FALSE,
    width integer DEFAULT NULL,
    height integer DEFAULT NULL
);
CREATE INDEX idx_media_filename ON media (filename);

CREATE TABLE products (
	id VARCHAR(40) NOT NULL PRIMARY KEY,
	created timestamp DEFAULT NULL,
	created_by VARCHAR(40) NULL DEFAULT NULL,
	modified timestamp DEFAULT NULL,
	modified_by VARCHAR(40) DEFAULT NULL,
	slug VARCHAR(128) DEFAULT NULL,
	title VARCHAR(128) DEFAULT NULL,
	filename VARCHAR(128) DEFAULT NULL,
	confirmation_mailing_group_id VARCHAR(128) DEFAULT NULL,
	mailing_group_id VARCHAR(128) DEFAULT NULL
);

CREATE TABLE user_access (
  id VARCHAR(40) NOT NULL PRIMARY KEY,
  access_time timestamp NOT NULL,
  access_date DATE NOT NULL,
  ip VARCHAR(60) NOT NULL,
  user_agent VARCHAR(200) NOT NULL,
  entity_type VARCHAR(20) NOT NULL,
  entity_id VARCHAR(40) NOT NULL
);
-- Index ensuring only unique accesses will be stored. This is important to count visits reasonably correctly.
CREATE UNIQUE INDEX idx_user_access_unique ON user_access (access_date, ip, user_agent, entity_type, entity_id);

-- Aggregated access counts per entities
CREATE TABLE entity_access_count (
	entity_type VARCHAR(20) NOT NULL,
	entity_id VARCHAR(40) NOT NULL,
	access_count integer DEFAULT NULL,
	PRIMARY KEY (entity_type, entity_id)
);

CREATE TABLE orders (
	id VARCHAR(40) NOT NULL PRIMARY KEY,
	created timestamp NOT NULL,
	created_by VARCHAR(40) NOT NULL,
	modified timestamp NOT NULL,
	modified_by VARCHAR(40) NOT NULL
);

CREATE TABLE order_items (
	id VARCHAR(40) NOT NULL PRIMARY KEY,
	created timestamp NOT NULL,
	created_by VARCHAR(40) NOT NULL,
	modified timestamp NOT NULL,
	modified_by VARCHAR(40) NOT NULL,
	order_id VARCHAR(40) NOT NULL,
	product_id VARCHAR(40) NOT NULL,
	quantity integer NOT NULL,
	downloaded timestamp DEFAULT NULL
);

ALTER TABLE order_items ADD CONSTRAINT ordered_product_fk FOREIGN KEY (product_id) REFERENCES products (id);
ALTER TABLE order_items ADD CONSTRAINT order_fk FOREIGN KEY (order_id) REFERENCES orders (id);
-- ALTER TABLE order_items ADD COLUMN downloaded timestamp DEFAULT NULL;
-- ALTER TABLE users ADD COLUMN consent timestamp DEFAULT NULL;

CREATE TABLE comments (
	id VARCHAR(40) NOT NULL PRIMARY KEY,
	parent_id VARCHAR(40) DEFAULT NULL,
	created timestamp NOT NULL,
	created_by VARCHAR(40) NULL DEFAULT NULL,
	modified timestamp NOT NULL,
	modified_by VARCHAR(40) DEFAULT NULL,
	comment VARCHAR(20000) NOT NULL,
	username VARCHAR(64) NOT NULL,
	email VARCHAR(64) NOT NULL,
	entity_type VARCHAR(20) NOT NULL,
    entity_id VARCHAR(40) NOT NULL,
	ip VARCHAR(60) NOT NULL,
    user_agent VARCHAR(200) NOT NULL
);

ALTER TABLE comments ADD CONSTRAINT parent_id_fk FOREIGN KEY (parent_id) REFERENCES comments (id);
CREATE INDEX idx_comments_entity_id ON comments (entity_id);

CREATE TABLE config (
	entry_key VARCHAR(120) NOT NULL PRIMARY KEY,
	entry_value VARCHAR(1024) NOT NULL
);

CREATE TABLE localisation (
	entry_key VARCHAR(120) NOT NULL PRIMARY KEY,
	entry_value VARCHAR(1000) NOT NULL
);

CREATE TABLE antispam_quiz (
    question VARCHAR(128) NOT NULL PRIMARY KEY,
    answer VARCHAR(64) NOT NULL
);
