--
-- PostgreSQL database dump
--

-- Dumped from database version 11.17
-- Dumped by pg_dump version 11.17

-- Started on 2022-10-30 14:03:55

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
-- TOC entry 6 (class 2615 OID 16394)
-- Name: wholesale; Type: SCHEMA; Schema: -; Owner: postgres
--

CREATE SCHEMA wholesale;


ALTER SCHEMA wholesale OWNER TO postgres;

SET default_tablespace = '';

SET default_with_oids = false;

--
-- TOC entry 199 (class 1259 OID 16407)
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
-- TOC entry 198 (class 1259 OID 16400)
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
-- TOC entry 201 (class 1259 OID 16435)
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
-- TOC entry 200 (class 1259 OID 16415)
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
-- TOC entry 202 (class 1259 OID 16500)
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
-- TOC entry 203 (class 1259 OID 16536)
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
-- TOC entry 197 (class 1259 OID 16395)
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
-- TOC entry 2715 (class 2606 OID 16414)
-- Name: customer customer_pkey; Type: CONSTRAINT; Schema: wholesale; Owner: postgres
--

ALTER TABLE ONLY wholesale.customer
    ADD CONSTRAINT customer_pkey PRIMARY KEY (c_w_id, c_d_id, c_id);


--
-- TOC entry 2712 (class 2606 OID 16406)
-- Name: district district_pkey; Type: CONSTRAINT; Schema: wholesale; Owner: postgres
--

ALTER TABLE ONLY wholesale.district
    ADD CONSTRAINT district_pkey PRIMARY KEY (d_w_id, d_id);


--
-- TOC entry 2719 (class 2606 OID 16439)
-- Name: item item_pkey; Type: CONSTRAINT; Schema: wholesale; Owner: postgres
--

ALTER TABLE ONLY wholesale.item
    ADD CONSTRAINT item_pkey PRIMARY KEY (i_id);


--
-- TOC entry 2721 (class 2606 OID 16504)
-- Name: order_line order_line_pkey; Type: CONSTRAINT; Schema: wholesale; Owner: postgres
--

ALTER TABLE ONLY wholesale.order_line
    ADD CONSTRAINT order_line_pkey PRIMARY KEY (ol_w_id, ol_d_id, ol_o_id, ol_number);


--
-- TOC entry 2717 (class 2606 OID 16419)
-- Name: order order_pkey; Type: CONSTRAINT; Schema: wholesale; Owner: postgres
--

ALTER TABLE ONLY wholesale."order"
    ADD CONSTRAINT order_pkey PRIMARY KEY (o_w_id, o_d_id, o_id);


--
-- TOC entry 2723 (class 2606 OID 16540)
-- Name: stock stock_pkey; Type: CONSTRAINT; Schema: wholesale; Owner: postgres
--

ALTER TABLE ONLY wholesale.stock
    ADD CONSTRAINT stock_pkey PRIMARY KEY (s_w_id, s_i_id);


--
-- TOC entry 2710 (class 2606 OID 16399)
-- Name: warehouse warehouse_pkey; Type: CONSTRAINT; Schema: wholesale; Owner: postgres
--

ALTER TABLE ONLY wholesale.warehouse
    ADD CONSTRAINT warehouse_pkey PRIMARY KEY (w_id);


--
-- TOC entry 2713 (class 1259 OID 16577)
-- Name: customer_balance; Type: INDEX; Schema: wholesale; Owner: postgres
--

CREATE INDEX customer_balance ON wholesale.customer USING btree (c_balance DESC NULLS LAST);


--
-- TOC entry 2725 (class 2606 OID 16430)
-- Name: customer customer_c_w_id_fkey; Type: FK CONSTRAINT; Schema: wholesale; Owner: postgres
--

ALTER TABLE ONLY wholesale.customer
    ADD CONSTRAINT customer_c_w_id_fkey FOREIGN KEY (c_w_id, c_d_id) REFERENCES wholesale.district(d_w_id, d_id) NOT VALID;


--
-- TOC entry 2724 (class 2606 OID 16425)
-- Name: district district_d_w_id_fkey; Type: FK CONSTRAINT; Schema: wholesale; Owner: postgres
--

ALTER TABLE ONLY wholesale.district
    ADD CONSTRAINT district_d_w_id_fkey FOREIGN KEY (d_w_id) REFERENCES wholesale.warehouse(w_id) NOT VALID;


--
-- TOC entry 2727 (class 2606 OID 16505)
-- Name: order_line order_line_ol_i_id_fkey; Type: FK CONSTRAINT; Schema: wholesale; Owner: postgres
--

ALTER TABLE ONLY wholesale.order_line
    ADD CONSTRAINT order_line_ol_i_id_fkey FOREIGN KEY (ol_i_id) REFERENCES wholesale.item(i_id);


--
-- TOC entry 2728 (class 2606 OID 16510)
-- Name: order_line order_line_ol_w_id_fkey; Type: FK CONSTRAINT; Schema: wholesale; Owner: postgres
--

ALTER TABLE ONLY wholesale.order_line
    ADD CONSTRAINT order_line_ol_w_id_fkey FOREIGN KEY (ol_w_id, ol_o_id, ol_d_id) REFERENCES wholesale."order"(o_w_id, o_id, o_d_id);


--
-- TOC entry 2726 (class 2606 OID 16420)
-- Name: order order_o_w_id_fkey; Type: FK CONSTRAINT; Schema: wholesale; Owner: postgres
--

ALTER TABLE ONLY wholesale."order"
    ADD CONSTRAINT order_o_w_id_fkey FOREIGN KEY (o_w_id, o_d_id, o_c_id) REFERENCES wholesale.customer(c_w_id, c_d_id, c_id) NOT VALID;


--
-- TOC entry 2729 (class 2606 OID 16541)
-- Name: stock stock_s_i_id_fkey; Type: FK CONSTRAINT; Schema: wholesale; Owner: postgres
--

ALTER TABLE ONLY wholesale.stock
    ADD CONSTRAINT stock_s_i_id_fkey FOREIGN KEY (s_i_id) REFERENCES wholesale.item(i_id);


--
-- TOC entry 2730 (class 2606 OID 16546)
-- Name: stock stock_s_w_id_fkey; Type: FK CONSTRAINT; Schema: wholesale; Owner: postgres
--

ALTER TABLE ONLY wholesale.stock
    ADD CONSTRAINT stock_s_w_id_fkey FOREIGN KEY (s_w_id) REFERENCES wholesale.warehouse(w_id);


-- Completed on 2022-10-30 14:03:55

--
-- PostgreSQL database dump complete
--

