--
-- PostgreSQL database dump
--

-- Dumped from database version 11.2-YB-2.15.2.1-b0
-- Dumped by pg_dump version 11.17

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

DROP DATABASE cs5424_ysql;
--
-- Name: cs5424_ysql; Type: DATABASE; Schema: -; Owner: yugabyte
--

CREATE DATABASE cs5424_ysql WITH TEMPLATE = template0 ENCODING = 'UTF8' LC_COLLATE = 'C' LC_CTYPE = 'en_US.UTF-8';


ALTER DATABASE cs5424_ysql OWNER TO yugabyte;

\connect cs5424_ysql

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

--
-- Name: wholesale; Type: SCHEMA; Schema: -; Owner: postgres
--

CREATE SCHEMA wholesale;


ALTER SCHEMA wholesale OWNER TO postgres;

--
-- Name: update_balance_func(); Type: FUNCTION; Schema: wholesale; Owner: postgres
--

CREATE FUNCTION wholesale.update_balance_func() RETURNS trigger
    LANGUAGE plpgsql
    AS $$begin
	update wholesale.balance 
	set b_c_balance = new.c_balance 
	where b_c_w_id = new.c_w_id and b_c_d_id = new.c_d_id and b_c_id = new.c_id;
	return null;
end;	$$;


ALTER FUNCTION wholesale.update_balance_func() OWNER TO postgres;

SET default_tablespace = '';

SET default_with_oids = false;

--
-- Name: balance; Type: TABLE; Schema: wholesale; Owner: postgres
--

CREATE TABLE wholesale.balance (
    b_c_id integer NOT NULL,
    b_c_w_id integer NOT NULL,
    b_c_d_id integer NOT NULL,
    b_c_balance numeric(12,2) NOT NULL,
    b_c_first character varying(16) NOT NULL,
    b_c_middle character(2) NOT NULL,
    b_c_last character varying(16) NOT NULL
);


ALTER TABLE wholesale.balance OWNER TO postgres;

--
-- Name: customer; Type: TABLE; Schema: wholesale; Owner: postgres
--

CREATE TABLE wholesale.customer (
    c_w_id integer NOT NULL,
    c_d_id integer NOT NULL,
    c_id integer NOT NULL,
    c_first character varying(16) NOT NULL,
    c_middle character(2) NOT NULL,
    c_last character varying(16) NOT NULL,
    c_street_1 character varying(20) NOT NULL,
    c_street_2 character varying(20) NOT NULL,
    c_city character varying(20) NOT NULL,
    c_state character(2) NOT NULL,
    c_zip character(9) NOT NULL,
    c_phone character(16) NOT NULL,
    c_since timestamp without time zone NOT NULL,
    c_credit character(2) NOT NULL,
    c_credit_lim numeric(12,2) NOT NULL,
    c_discount numeric(5,4) NOT NULL,
    c_balance numeric(12,2) NOT NULL,
    c_ytd_payment real NOT NULL,
    c_payment_cnt integer NOT NULL,
    c_delivery integer NOT NULL,
    c_data character varying(500) NOT NULL
);


ALTER TABLE wholesale.customer OWNER TO postgres;

--
-- Name: customer_order_items; Type: TABLE; Schema: wholesale; Owner: postgres
--

CREATE TABLE wholesale.customer_order_items (
    coi_w_id integer NOT NULL,
    coi_d_id integer NOT NULL,
    coi_c_id integer NOT NULL,
    coi_o_id integer NOT NULL,
    coi_i_id integer NOT NULL
);


ALTER TABLE wholesale.customer_order_items OWNER TO postgres;

--
-- Name: district; Type: TABLE; Schema: wholesale; Owner: postgres
--

CREATE TABLE wholesale.district (
    d_w_id integer NOT NULL,
    d_id integer NOT NULL,
    d_name character varying(10) NOT NULL,
    d_street_1 character varying(20) NOT NULL,
    d_street_2 character varying(20) NOT NULL,
    d_city character varying(20) NOT NULL,
    d_state character(2) NOT NULL,
    d_zip character(9) NOT NULL,
    d_tax numeric(4,4) NOT NULL,
    d_ytd numeric(12,2) NOT NULL,
    d_next_oid numeric(12,2) NOT NULL
);


ALTER TABLE wholesale.district OWNER TO postgres;

--
-- Name: item; Type: TABLE; Schema: wholesale; Owner: postgres
--

CREATE TABLE wholesale.item (
    i_id integer NOT NULL,
    i_name character varying(24) NOT NULL,
    i_price numeric(5,2) NOT NULL,
    i_im_id integer NOT NULL,
    i_data character varying(50) NOT NULL
);


ALTER TABLE wholesale.item OWNER TO postgres;

--
-- Name: order; Type: TABLE; Schema: wholesale; Owner: postgres
--

CREATE TABLE wholesale."order" (
    o_w_id integer NOT NULL,
    o_d_id integer NOT NULL,
    o_id integer NOT NULL,
    o_c_id integer NOT NULL,
    o_carrier_id integer,
    o_ol_cnt numeric(2,0) NOT NULL,
    o_all_local numeric(1,0) NOT NULL,
    o_entry_d timestamp without time zone NOT NULL
);


ALTER TABLE wholesale."order" OWNER TO postgres;

--
-- Name: order_line; Type: TABLE; Schema: wholesale; Owner: postgres
--

CREATE TABLE wholesale.order_line (
    ol_w_id integer NOT NULL,
    ol_d_id integer NOT NULL,
    ol_o_id integer NOT NULL,
    ol_number integer NOT NULL,
    ol_i_id integer NOT NULL,
    ol_delivery_d timestamp without time zone,
    ol_amount numeric(7,2) NOT NULL,
    ol_supply_w_id integer NOT NULL,
    ol_quantity numeric(2,0) NOT NULL,
    ol_dist_info character(24) NOT NULL
);


ALTER TABLE wholesale.order_line OWNER TO postgres;

--
-- Name: stock; Type: TABLE; Schema: wholesale; Owner: postgres
--

CREATE TABLE wholesale.stock (
    s_w_id integer NOT NULL,
    s_i_id integer NOT NULL,
    s_quantity numeric(4,0) NOT NULL,
    s_ytd numeric(8,2) NOT NULL,
    s_ordercnt integer NOT NULL,
    s_remote_cnt integer NOT NULL,
    s_dist_01 character(24) NOT NULL,
    s_dist_02 character(24) NOT NULL,
    s_dist_03 character(24) NOT NULL,
    s_dist_04 character(24) NOT NULL,
    s_dist_05 character(24) NOT NULL,
    s_dist_06 character(24) NOT NULL,
    s_dist_07 character(24) NOT NULL,
    s_dist_08 character(24) NOT NULL,
    s_dist_09 character(24) NOT NULL,
    s_dist_10 character(24) NOT NULL,
    s_data character varying(50) NOT NULL
);


ALTER TABLE wholesale.stock OWNER TO postgres;

--
-- Name: warehouse; Type: TABLE; Schema: wholesale; Owner: postgres
--

CREATE TABLE wholesale.warehouse (
    w_id smallint NOT NULL,
    w_name character varying(10) NOT NULL,
    w_street_1 character varying(20) NOT NULL,
    w_street_2 character varying(20) NOT NULL,
    w_city character varying(20) NOT NULL,
    w_state character(2) NOT NULL,
    w_zip character(9) NOT NULL,
    w_tax numeric(4,4) NOT NULL,
    w_ytd numeric(12,2) NOT NULL
);


ALTER TABLE wholesale.warehouse OWNER TO postgres;

--
-- Name: balance balance_pkey; Type: CONSTRAINT; Schema: wholesale; Owner: postgres
--

ALTER TABLE ONLY wholesale.balance
    ADD CONSTRAINT balance_pkey PRIMARY KEY (b_c_id, b_c_w_id, b_c_d_id);


--
-- Name: customer_order_items customer_order_items_pkey; Type: CONSTRAINT; Schema: wholesale; Owner: postgres
--

ALTER TABLE ONLY wholesale.customer_order_items
    ADD CONSTRAINT customer_order_items_pkey PRIMARY KEY (coi_w_id, coi_d_id, coi_i_id, coi_c_id, coi_o_id);


--
-- Name: customer customer_pkey; Type: CONSTRAINT; Schema: wholesale; Owner: postgres
--

ALTER TABLE ONLY wholesale.customer
    ADD CONSTRAINT customer_pkey PRIMARY KEY (c_w_id, c_d_id, c_id);


--
-- Name: district district_pkey; Type: CONSTRAINT; Schema: wholesale; Owner: postgres
--

ALTER TABLE ONLY wholesale.district
    ADD CONSTRAINT district_pkey PRIMARY KEY (d_w_id, d_id);


--
-- Name: item item_pkey; Type: CONSTRAINT; Schema: wholesale; Owner: postgres
--

ALTER TABLE ONLY wholesale.item
    ADD CONSTRAINT item_pkey PRIMARY KEY (i_id);


--
-- Name: order_line order_line_pkey; Type: CONSTRAINT; Schema: wholesale; Owner: postgres
--

ALTER TABLE ONLY wholesale.order_line
    ADD CONSTRAINT order_line_pkey PRIMARY KEY (ol_w_id, ol_d_id, ol_o_id, ol_number);


--
-- Name: order order_pkey; Type: CONSTRAINT; Schema: wholesale; Owner: postgres
--

ALTER TABLE ONLY wholesale."order"
    ADD CONSTRAINT order_pkey PRIMARY KEY (o_w_id, o_d_id, o_id);


--
-- Name: stock stock_pkey; Type: CONSTRAINT; Schema: wholesale; Owner: postgres
--

ALTER TABLE ONLY wholesale.stock
    ADD CONSTRAINT stock_pkey PRIMARY KEY (s_w_id, s_i_id);


--
-- Name: warehouse warehouse_pkey; Type: CONSTRAINT; Schema: wholesale; Owner: postgres
--

ALTER TABLE ONLY wholesale.warehouse
    ADD CONSTRAINT warehouse_pkey PRIMARY KEY (w_id);


--
-- Name: balance_index; Type: INDEX; Schema: wholesale; Owner: postgres
--

CREATE INDEX balance_index ON wholesale.balance USING lsm (b_c_balance DESC NULLS LAST);


--
-- Name: order_fk; Type: INDEX; Schema: wholesale; Owner: postgres
--

CREATE INDEX order_fk ON wholesale."order" USING lsm (o_w_id HASH, o_d_id ASC, o_c_id ASC);


--
-- Name: customer tri_after_update_t1; Type: TRIGGER; Schema: wholesale; Owner: postgres
--

CREATE TRIGGER tri_after_update_t1 AFTER UPDATE OF c_balance ON wholesale.customer FOR EACH ROW EXECUTE PROCEDURE wholesale.update_balance_func();


--
-- Name: customer customer_c_w_id_fkey; Type: FK CONSTRAINT; Schema: wholesale; Owner: postgres
--

ALTER TABLE ONLY wholesale.customer
    ADD CONSTRAINT customer_c_w_id_fkey FOREIGN KEY (c_w_id, c_d_id) REFERENCES wholesale.district(d_w_id, d_id) NOT VALID;


--
-- Name: district district_d_w_id_fkey; Type: FK CONSTRAINT; Schema: wholesale; Owner: postgres
--

ALTER TABLE ONLY wholesale.district
    ADD CONSTRAINT district_d_w_id_fkey FOREIGN KEY (d_w_id) REFERENCES wholesale.warehouse(w_id) NOT VALID;


--
-- Name: order_line order_line_ol_i_id_fkey; Type: FK CONSTRAINT; Schema: wholesale; Owner: postgres
--

ALTER TABLE ONLY wholesale.order_line
    ADD CONSTRAINT order_line_ol_i_id_fkey FOREIGN KEY (ol_i_id) REFERENCES wholesale.item(i_id);


--
-- Name: order_line order_line_ol_w_id_fkey; Type: FK CONSTRAINT; Schema: wholesale; Owner: postgres
--

ALTER TABLE ONLY wholesale.order_line
    ADD CONSTRAINT order_line_ol_w_id_fkey FOREIGN KEY (ol_w_id, ol_o_id, ol_d_id) REFERENCES wholesale."order"(o_w_id, o_id, o_d_id);


--
-- Name: order order_o_w_id_fkey; Type: FK CONSTRAINT; Schema: wholesale; Owner: postgres
--

ALTER TABLE ONLY wholesale."order"
    ADD CONSTRAINT order_o_w_id_fkey FOREIGN KEY (o_w_id, o_d_id, o_c_id) REFERENCES wholesale.customer(c_w_id, c_d_id, c_id) NOT VALID;


--
-- Name: stock stock_s_i_id_fkey; Type: FK CONSTRAINT; Schema: wholesale; Owner: postgres
--

ALTER TABLE ONLY wholesale.stock
    ADD CONSTRAINT stock_s_i_id_fkey FOREIGN KEY (s_i_id) REFERENCES wholesale.item(i_id);


--
-- Name: stock stock_s_w_id_fkey; Type: FK CONSTRAINT; Schema: wholesale; Owner: postgres
--

ALTER TABLE ONLY wholesale.stock
    ADD CONSTRAINT stock_s_w_id_fkey FOREIGN KEY (s_w_id) REFERENCES wholesale.warehouse(w_id);


--
-- PostgreSQL database dump complete
--

