-- pharma_admin account
INSERT INTO account (id, username, password)
VALUES (
    '3f2c1a7e-9b6d-4c8f-91a2-6d8f4c2e9b11',
    'pharmacy:alAmal',
    '$2a$10$vImTCIqfRcmyp98exgQ7buFCiJOWSo1SaKktk8HIHmRcfmaX4X2cC' 
    -- the password for this admin is '123'
)
ON CONFLICT (id) DO NOTHING;

-- pharma_admin pharmacy (linked to the account)
INSERT INTO pharmacy (name,location,pharmacist_name, account_id)
SELECT
    'Al Amal Pharmacy',
    '0101000020E610000089690910235B4240AD72346DCE5C4140',
    'Dr. Abdullah alatassi',
    '3f2c1a7e-9b6d-4c8f-91a2-6d8f4c2e9b11'
WHERE NOT EXISTS (
    SELECT 1
    FROM patient
    WHERE account_id = '3f2c1a7e-9b6d-4c8f-91a2-6d8f4c2e9b11'
);