CREATE EXTENSION IF NOT EXISTS "uuid-ossp"
INSERT INTO public.role (id, createddate, updateddate, isdeleted, name) VALUES (uuid_generate_v4(), current_timestamp, current_timestamp, false, 'ADMIN');
INSERT INTO public.role (id, createddate, updateddate, isdeleted, name) VALUES (uuid_generate_v4(), current_timestamp, current_timestamp, false, 'USER');