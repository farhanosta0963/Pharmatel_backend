-- pharma_admin account
INSERT INTO account (id, username, password)
VALUES (
    '3f2c1a7e-9b6d-4c8f-91a2-6d8f4c2e9b11',
    'pharma_admin',
    'changeme'
)
ON CONFLICT (id) DO NOTHING;

-- pharma_admin patient (linked to the account)
INSERT INTO patient (name, email, phone_number, account_id)
SELECT
    'pharma_admin',
    'pharma_admin@pharmatel.com',
    '+96170000000',
    '3f2c1a7e-9b6d-4c8f-91a2-6d8f4c2e9b11'
WHERE NOT EXISTS (
    SELECT 1
    FROM patient
    WHERE account_id = '3f2c1a7e-9b6d-4c8f-91a2-6d8f4c2e9b11'
);