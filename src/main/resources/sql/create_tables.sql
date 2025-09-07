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
	created timestamp NOT NULL,
	created_by VARCHAR(40) NULL DEFAULT NULL,
	modified timestamp NOT NULL,
	modified_by VARCHAR(40) DEFAULT NULL,
	login VARCHAR(32) NOT NULL,
	password VARCHAR(500) NOT NULL,
	first_name VARCHAR(64) DEFAULT NULL,
	last_name VARCHAR(64) DEFAULT NULL,
	email VARCHAR(64) NOT NULL,
	consent timestamp DEFAULT NULL
);

CREATE UNIQUE INDEX idx_users_login ON users (login);
CREATE UNIQUE INDEX idx_users_email ON users (email);

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
	body_edited TEXT,
	editor VARCHAR(16) DEFAULT 'markdown',
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
	subtitle VARCHAR(256) DEFAULT NULL,
	filename VARCHAR(128) DEFAULT NULL,
	listing_image VARCHAR(128) DEFAULT NULL,
	image VARCHAR(128) DEFAULT NULL,
	confirmation_mailing_group_id VARCHAR(128) DEFAULT NULL,
	mailing_group_id VARCHAR(128) DEFAULT NULL,
	price_regular DECIMAL(19, 4) NOT NULL,
	price_discounted DECIMAL(19, 4)
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
	modified_by VARCHAR(40) NOT NULL,
	order_number VARCHAR(20) NOT NULL,
	state VARCHAR(16) NOT NULL
);
CREATE UNIQUE INDEX idx_orders_order_number ON orders (order_number);

CREATE TABLE order_items (
	id VARCHAR(40) NOT NULL PRIMARY KEY,
	created timestamp NOT NULL,
	created_by VARCHAR(40) NOT NULL,
	modified timestamp NOT NULL,
	modified_by VARCHAR(40) NOT NULL,
	order_id VARCHAR(40) NOT NULL,
	product_id VARCHAR(40) NOT NULL,
	quantity integer NOT NULL,
	price DECIMAL(19, 4) NOT NULL,
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
	state VARCHAR(20) NOT NULL DEFAULT 'WAITING_FOR_APPROVAL',
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
CREATE INDEX idx_comments_state ON comments (state);

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

-- Template-to-copy for concrete queue
CREATE TABLE queue (
    id VARCHAR(60) NOT NULL PRIMARY KEY,
    entered_time timestamp NOT NULL,
    entered_origin VARCHAR(60) NOT NULL,
    attempts INT NOT NULL DEFAULT 0,
    next_action_time timestamp,
    processed_time timestamp,
    processed_origin VARCHAR(60),
    last_attempt_time timestamp,
    last_attempt_origin VARCHAR(60),
    last_result TEXT,
    expiration_time timestamp
);

CREATE INDEX IDX_QUEUE_NEXT_ACTION_TIME ON queue (next_action_time);
CREATE INDEX IDX_QUEUE_EXPIRATION ON queue (expiration_time);

CREATE TABLE auth_code (
    code VARCHAR(255) NOT NULL,
    purpose VARCHAR(60) NOT NULL,
    user_id VARCHAR(60) NOT NULL,
    created timestamp NOT NULL,
    valid_to timestamp NOT NULL,
    used timestamp
);

ALTER TABLE auth_code ADD CONSTRAINT auth_code_pkey PRIMARY KEY (code, purpose, user_id);
CREATE INDEX idx_auth_code_user_id ON auth_code (user_id);

CREATE TABLE user_product (
    id VARCHAR(40) NOT NULL PRIMARY KEY,
	user_id VARCHAR(40) NOT NULL,
	product_id VARCHAR(40) NOT NULL,
	created timestamp NOT NULL
);

ALTER TABLE user_product ADD CONSTRAINT user_fk FOREIGN KEY (user_id) REFERENCES users (id);
ALTER TABLE user_product ADD CONSTRAINT product_fk FOREIGN KEY (product_id) REFERENCES products (id);

CREATE TABLE sequences (
    sequence_name VARCHAR(20) NOT NULL PRIMARY KEY,
    next_value BIGINT NOT NULL
);

CREATE TABLE news_subscription (
    id VARCHAR(40) NOT NULL PRIMARY KEY,
    email VARCHAR(64) NOT NULL,
    created timestamp NOT NULL,
    confirmed timestamp
);

CREATE INDEX idx_news_subscription_email ON news_subscription (email);
