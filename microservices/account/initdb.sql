-- public.tbl_users definition

-- Drop table

-- DROP TABLE public.tbl_users;

CREATE TABLE public.tbl_users (
	id bigserial NOT NULL,
	created_date timestamp NULL,
	date_of_birth varchar(255) NULL,
	email varchar(255) NULL,
	first_name varchar(255) NULL,
	gender int4 NULL,
	last_name varchar(255) NULL,
	"password" varchar(255) NULL,
	phone varchar(255) NULL,
	updated_date timestamp NULL,
	username varchar(255) NULL,
	CONSTRAINT tbl_users_pkey PRIMARY KEY (id)
);

-- public.tbl_address definition

-- Drop table

-- DROP TABLE public.tbl_address;

CREATE TABLE public.tbl_address (
	id bigserial NOT NULL,
	user_id int8 NULL,
	address_type int4 NULL,
	apartment_number varchar(255) NULL,
	building varchar(255) NULL,
	city varchar(255) NULL,
	country varchar(255) NULL,
	floor varchar(255) NULL,
	street varchar(255) NULL,
	street_number varchar(255) NULL,
	updated_at timestamp NULL,
	created_at timestamp NULL,
	CONSTRAINT tbl_address_pkey PRIMARY KEY (id)
);


-- public.tbl_address foreign keys

ALTER TABLE public.tbl_address ADD CONSTRAINT fk_address_user FOREIGN KEY (user_id) REFERENCES public.tbl_users(id);