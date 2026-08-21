-- INSERT INTO account (id, username, password)
-- VALUES (
-- 	'9a3f1a6d-1d0e-4f59-a4c0-8c89a2258d4b',
-- 	'patient:user_sy',
-- 	'$2a$10$vImTCIqfRcmyp98exgQ7buFCiJOWSo1SaKktk8HIHmRcfmaX4X2cC'
-- );

-- INSERT INTO patient (name, email, phone_number, account_id)
-- VALUES (
-- 	'user_sy',
-- 	'user@example.com',
-- 	'1234567890',
-- 	'9a3f1a6d-1d0e-4f59-a4c0-8c89a2258d4b'
-- );


INSERT INTO account (id, username, password)
VALUES (
	'9a3f1a6d-1d0e-4f59-a4c0-8c89a2258d4b',
	'patient:user_sy',
	'$2a$10$vImTCIqfRcmyp98exgQ7buFCiJOWSo1SaKktk8HIHmRcfmaX4X2cC'
);

INSERT INTO patient (
	medical_record_number,
	name,
	email,
	phone_number,
	date_of_birth,
	gender,
	height_cm,
	weight_kg,
	diagnosis,
	allergies,
	account_id
)
VALUES (
	'MRN-0001',
	'user_sy',
	'user@example.com',
	'1234567890',
	'1990-05-14',
	'MALE',
	175.50,
	70.10,
	'Type 2 Diabetes',
	'Penicillin',
	'9a3f1a6d-1d0e-4f59-a4c0-8c89a2258d4b'
);