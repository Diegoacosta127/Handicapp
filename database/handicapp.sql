--
-- PostgreSQL database dump
--

-- Dumped from database version 14.17 (Ubuntu 14.17-0ubuntu0.22.04.1)
-- Dumped by pg_dump version 14.17 (Ubuntu 14.17-0ubuntu0.22.04.1)

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

ALTER TABLE IF EXISTS ONLY public.team_has_season DROP CONSTRAINT IF EXISTS team_has_season_team_id_fkey;
ALTER TABLE IF EXISTS ONLY public.team_has_season DROP CONSTRAINT IF EXISTS team_has_season_season_id_fkey;
ALTER TABLE IF EXISTS ONLY public.team_has_player DROP CONSTRAINT IF EXISTS team_has_player_team_id_fkey;
ALTER TABLE IF EXISTS ONLY public.team_has_player DROP CONSTRAINT IF EXISTS team_has_player_player_id_fkey;
ALTER TABLE IF EXISTS ONLY public.season DROP CONSTRAINT IF EXISTS season_country_id_fkey;
ALTER TABLE IF EXISTS ONLY public.player DROP CONSTRAINT IF EXISTS player_country_id_fkey;
ALTER TABLE IF EXISTS ONLY public.match DROP CONSTRAINT IF EXISTS match_team_home_fkey;
ALTER TABLE IF EXISTS ONLY public.match DROP CONSTRAINT IF EXISTS match_team_away_fkey;
ALTER TABLE IF EXISTS ONLY public.match_has_player DROP CONSTRAINT IF EXISTS match_has_player_player_id_fkey;
ALTER TABLE IF EXISTS ONLY public.match_has_player DROP CONSTRAINT IF EXISTS match_has_player_match_id_fkey;
ALTER TABLE IF EXISTS ONLY public.team DROP CONSTRAINT IF EXISTS team_pkey;
ALTER TABLE IF EXISTS ONLY public.team_has_season DROP CONSTRAINT IF EXISTS team_has_season_pkey;
ALTER TABLE IF EXISTS ONLY public.season DROP CONSTRAINT IF EXISTS season_pkey;
ALTER TABLE IF EXISTS ONLY public.player DROP CONSTRAINT IF EXISTS player_pkey;
ALTER TABLE IF EXISTS ONLY public.match DROP CONSTRAINT IF EXISTS match_pkey;
ALTER TABLE IF EXISTS ONLY public.country DROP CONSTRAINT IF EXISTS country_pkey;
ALTER TABLE IF EXISTS public.team_has_season ALTER COLUMN team_season DROP DEFAULT;
ALTER TABLE IF EXISTS public.team ALTER COLUMN team_id DROP DEFAULT;
ALTER TABLE IF EXISTS public.season ALTER COLUMN season_id DROP DEFAULT;
ALTER TABLE IF EXISTS public.player ALTER COLUMN player_id DROP DEFAULT;
ALTER TABLE IF EXISTS public.match ALTER COLUMN match_id DROP DEFAULT;
ALTER TABLE IF EXISTS public.country ALTER COLUMN country_id DROP DEFAULT;
DROP SEQUENCE IF EXISTS public.team_team_id_seq;
DROP SEQUENCE IF EXISTS public.team_has_season_team_season_seq;
DROP TABLE IF EXISTS public.team_has_season;
DROP TABLE IF EXISTS public.team_has_player;
DROP TABLE IF EXISTS public.team;
DROP SEQUENCE IF EXISTS public.season_season_id_seq;
DROP TABLE IF EXISTS public.season;
DROP SEQUENCE IF EXISTS public.player_player_id_seq;
DROP TABLE IF EXISTS public.player;
DROP SEQUENCE IF EXISTS public.match_match_id_seq;
DROP TABLE IF EXISTS public.match_has_player;
DROP TABLE IF EXISTS public.match;
DROP SEQUENCE IF EXISTS public.country_country_id_seq;
DROP TABLE IF EXISTS public.country;
SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- Name: country; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.country (
    country_id integer NOT NULL,
    name character varying(25) NOT NULL
);


--
-- Name: country_country_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.country_country_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: country_country_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.country_country_id_seq OWNED BY public.country.country_id;


--
-- Name: match; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.match (
    match_id integer NOT NULL,
    team_home integer NOT NULL,
    team_away integer NOT NULL,
    score_home integer NOT NULL,
    score_away integer NOT NULL
);


--
-- Name: match_has_player; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.match_has_player (
    match_id integer NOT NULL,
    player_id integer NOT NULL,
    score integer NOT NULL
);


--
-- Name: match_match_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.match_match_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: match_match_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.match_match_id_seq OWNED BY public.match.match_id;


--
-- Name: player; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.player (
    player_id integer NOT NULL,
    first_name character varying(25) NOT NULL,
    last_name character varying(25) NOT NULL,
    country_id integer NOT NULL,
    mid_name character varying(5)
);


--
-- Name: player_player_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.player_player_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: player_player_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.player_player_id_seq OWNED BY public.player.player_id;


--
-- Name: season; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.season (
    season_id integer NOT NULL,
    country_id integer NOT NULL,
    year integer NOT NULL,
    description character varying(50) NOT NULL,
    is_over boolean DEFAULT false
);


--
-- Name: season_season_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.season_season_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: season_season_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.season_season_id_seq OWNED BY public.season.season_id;


--
-- Name: team; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.team (
    team_id integer NOT NULL,
    name character varying(40) NOT NULL
);


--
-- Name: team_has_player; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.team_has_player (
    team_id integer NOT NULL,
    player_id integer NOT NULL,
    "position" smallint NOT NULL,
    handicap smallint NOT NULL
);


--
-- Name: team_has_season; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.team_has_season (
    team_season integer NOT NULL,
    team_id integer NOT NULL,
    season_id integer NOT NULL
);


--
-- Name: team_has_season_team_season_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.team_has_season_team_season_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: team_has_season_team_season_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.team_has_season_team_season_seq OWNED BY public.team_has_season.team_season;


--
-- Name: team_team_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.team_team_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: team_team_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.team_team_id_seq OWNED BY public.team.team_id;


--
-- Name: country country_id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.country ALTER COLUMN country_id SET DEFAULT nextval('public.country_country_id_seq'::regclass);


--
-- Name: match match_id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.match ALTER COLUMN match_id SET DEFAULT nextval('public.match_match_id_seq'::regclass);


--
-- Name: player player_id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.player ALTER COLUMN player_id SET DEFAULT nextval('public.player_player_id_seq'::regclass);


--
-- Name: season season_id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.season ALTER COLUMN season_id SET DEFAULT nextval('public.season_season_id_seq'::regclass);


--
-- Name: team team_id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.team ALTER COLUMN team_id SET DEFAULT nextval('public.team_team_id_seq'::regclass);


--
-- Name: team_has_season team_season; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.team_has_season ALTER COLUMN team_season SET DEFAULT nextval('public.team_has_season_team_season_seq'::regclass);


--
-- Data for Name: country; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.country (country_id, name) FROM stdin;
2	Chile
3	Brazil
4	United States
5	South Africa
6	Uruguay
1	Argentina
\.


--
-- Data for Name: match; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.match (match_id, team_home, team_away, score_home, score_away) FROM stdin;
\.


--
-- Data for Name: match_has_player; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.match_has_player (match_id, player_id, score) FROM stdin;
\.


--
-- Data for Name: player; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.player (player_id, first_name, last_name, country_id, mid_name) FROM stdin;
1	Facundo	Pieres	1	
2	Camilo	Castagnola	1	
3	Pablo	Mac Donough	1	
4	Bartolomé	Castagnola	1	Jr
5	Antonio	Heguy	1	
6	Victorino	Ruíz Jorba	1	
7	Cruz	Heguy	1	
8	Teodoro	Lacau	1	
9	Benjamín	Panelo	1	
10	Carlos María	Ulloa	1	
11	Facundo	Sola	1	
12	Joaquín	Pittaluga	1	
13	Facundo	Cruz Llosa	1	
14	Felipe	Vercellino	2	
15	Pedro	Zacharías	3	
16	Pedro	Falabella	1	
17	Pablo	Pieres	4	
18	Hilario	Ulloa	1	
19	Tomás	Panelo	1	
20	Francisco	Elizalde	1	
21	Lucas	Monteverde	1	Jr
22	Gonzalo	Pieres	1	Jr
23	Guillermo	Caset	1	
24	Ignatius	Du Plessis	5	
25	Juán	Britos	1	
26	Alfredo	Bigatti	1	
27	Juan Martín	Zubía	1	
28	Jerónimo	del Carril	1	
29	Adolfo	Cambiaso	1	Jr
30	David	Stirling	6	
31	Adolfo	Cambiaso	1	III
32	Juan Martín	Nero	1	
33	Beltrán	Laulhé	1	
34	Lorenzo	Chavanne	1	
35	Nicolás	Pieres	1	
36	Matías	Torres Zavaleta	1	
37	Rufino	Bensadón	1	
38	Bautista	Bayugar	1	
39	Diego	Cavanagh	1	
40	Alejo	Taranco	6	
\.


--
-- Data for Name: season; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.season (season_id, country_id, year, description, is_over) FROM stdin;
1	1	2024	Argentino Abierto	f
\.


--
-- Data for Name: team; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.team (team_id, name) FROM stdin;
1	La Natividad
2	Indios Chapaleufú
3	La Hache Cría & Polo
4	La Aguada
5	La Hache
6	Ellerstina
7	La Ensenada
8	La Dolfina
9	La Zeta
10	Cría La Dolfina
\.


--
-- Data for Name: team_has_player; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.team_has_player (team_id, player_id, "position", handicap) FROM stdin;
1	1	0	10
1	2	1	10
1	3	2	10
1	4	3	10
2	5	0	7
2	6	1	8
2	7	2	8
2	8	3	8
3	9	0	7
3	10	1	8
3	11	2	8
3	12	3	8
4	13	0	7
4	14	1	7
4	15	2	8
4	16	3	7
5	17	0	9
5	18	1	9
5	19	2	9
5	20	3	9
6	21	0	8
6	22	1	9
6	23	2	9
6	24	3	9
7	25	0	9
7	26	1	9
7	27	2	9
7	28	3	9
8	29	0	10
8	30	1	10
8	31	2	10
8	32	3	10
9	33	0	6
9	34	1	6
9	35	2	8
9	36	3	8
10	37	0	8
10	38	1	8
10	39	2	8
10	40	3	8
\.


--
-- Data for Name: team_has_season; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.team_has_season (team_season, team_id, season_id) FROM stdin;
1	1	1
2	2	1
3	3	1
4	4	1
5	5	1
6	6	1
7	7	1
8	8	1
9	9	1
10	10	1
\.


--
-- Name: country_country_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.country_country_id_seq', 6, true);


--
-- Name: match_match_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.match_match_id_seq', 1, false);


--
-- Name: player_player_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.player_player_id_seq', 40, true);


--
-- Name: season_season_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.season_season_id_seq', 1, true);


--
-- Name: team_has_season_team_season_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.team_has_season_team_season_seq', 10, true);


--
-- Name: team_team_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.team_team_id_seq', 10, true);


--
-- Name: country country_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.country
    ADD CONSTRAINT country_pkey PRIMARY KEY (country_id);


--
-- Name: match match_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.match
    ADD CONSTRAINT match_pkey PRIMARY KEY (match_id);


--
-- Name: player player_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.player
    ADD CONSTRAINT player_pkey PRIMARY KEY (player_id);


--
-- Name: season season_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.season
    ADD CONSTRAINT season_pkey PRIMARY KEY (season_id);


--
-- Name: team_has_season team_has_season_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.team_has_season
    ADD CONSTRAINT team_has_season_pkey PRIMARY KEY (team_season);


--
-- Name: team team_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.team
    ADD CONSTRAINT team_pkey PRIMARY KEY (team_id);


--
-- Name: match_has_player match_has_player_match_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.match_has_player
    ADD CONSTRAINT match_has_player_match_id_fkey FOREIGN KEY (match_id) REFERENCES public.match(match_id);


--
-- Name: match_has_player match_has_player_player_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.match_has_player
    ADD CONSTRAINT match_has_player_player_id_fkey FOREIGN KEY (player_id) REFERENCES public.player(player_id);


--
-- Name: match match_team_away_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.match
    ADD CONSTRAINT match_team_away_fkey FOREIGN KEY (team_away) REFERENCES public.team_has_season(team_season);


--
-- Name: match match_team_home_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.match
    ADD CONSTRAINT match_team_home_fkey FOREIGN KEY (team_home) REFERENCES public.team_has_season(team_season);


--
-- Name: player player_country_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.player
    ADD CONSTRAINT player_country_id_fkey FOREIGN KEY (country_id) REFERENCES public.country(country_id);


--
-- Name: season season_country_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.season
    ADD CONSTRAINT season_country_id_fkey FOREIGN KEY (country_id) REFERENCES public.country(country_id);


--
-- Name: team_has_player team_has_player_player_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.team_has_player
    ADD CONSTRAINT team_has_player_player_id_fkey FOREIGN KEY (player_id) REFERENCES public.player(player_id);


--
-- Name: team_has_player team_has_player_team_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.team_has_player
    ADD CONSTRAINT team_has_player_team_id_fkey FOREIGN KEY (team_id) REFERENCES public.team_has_season(team_season);


--
-- Name: team_has_season team_has_season_season_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.team_has_season
    ADD CONSTRAINT team_has_season_season_id_fkey FOREIGN KEY (season_id) REFERENCES public.season(season_id);


--
-- Name: team_has_season team_has_season_team_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.team_has_season
    ADD CONSTRAINT team_has_season_team_id_fkey FOREIGN KEY (team_id) REFERENCES public.team(team_id);


--
-- PostgreSQL database dump complete
--

