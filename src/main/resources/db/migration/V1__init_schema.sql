CREATE EXTENSION IF NOT EXISTS postgis;

-- ACCOUNT
CREATE TABLE account (
    id UUID PRIMARY KEY,
    username TEXT,
    password TEXT
);

-- PATIENT
CREATE TABLE patient (
    id SERIAL PRIMARY KEY,
    name TEXT,
    email TEXT,
    phone_number TEXT,
    account_id UUID,
    CONSTRAINT fk_patient_account
        FOREIGN KEY (account_id) REFERENCES account(id)
);

-- PHARMACY
CREATE TABLE pharmacy (
    id SERIAL PRIMARY KEY,
    name TEXT,
    location GEOGRAPHY,
    pharmacist_name TEXT,
    account_id UUID,
    CONSTRAINT fk_pharmacy_account
        FOREIGN KEY (account_id) REFERENCES account(id)
);

-- TODO delete factory table from navicat 

-- MEDICINE
CREATE TABLE medicine (
    id SERIAL PRIMARY KEY,
    name TEXT,
    buy_price TEXT,
    sell_price TEXT,
    pharmaceutical_form TEXT,
    box TEXT,
    capacity TEXT,
    factory TEXT,
    drug_composition TEXT, -- TODO we need to fix this relationship in navicat diagram
    by_pharmacist BOOLEAN,
    account_id UUID,
    CONSTRAINT fk_medicine_account
        FOREIGN KEY (account_id) REFERENCES account(id)
    
);

-- -- CHEMICAL
-- CREATE TABLE chemical (
--     id SERIAL PRIMARY KEY,
--     formal_name TEXT,
--     commercial_name TEXT
-- );

-- -- COMPOUNDING
-- CREATE TABLE compounding (
--     id SERIAL PRIMARY KEY,
--     medicine_id INTEGER,
--     chemical_id INTEGER,
--     quantity FLOAT8,
--     CONSTRAINT fk_compounding_medicine
--         FOREIGN KEY (medicine_id) REFERENCES medicine(id),
--     CONSTRAINT fk_compounding_chemical
--         FOREIGN KEY (chemical_id) REFERENCES chemical(id)
-- );

-- PHARMACY_MEDICINES
CREATE TABLE pharmacy_medicines (
    id SERIAL PRIMARY KEY,
    pharmacy_id INTEGER,
    medicine_id INTEGER,
    quantity INTEGER,
    CONSTRAINT fk_pm_pharmacy
        FOREIGN KEY (pharmacy_id) REFERENCES pharmacy(id),
    CONSTRAINT fk_pm_medicine
        FOREIGN KEY (medicine_id) REFERENCES medicine(id)
);

-- PRESCRIPTION
CREATE TABLE prescription (
    id UUID PRIMARY KEY,
    patient_id INTEGER,
    medicine_id INTEGER,
    dose TEXT,
    frequency TEXT,
    start_date TIMESTAMP,
    end_date TIMESTAMP,
    issued_at TIMESTAMP,
    by_pharmacist BOOLEAN,
    pharmacy_id INTEGER,
    food_requirement VARCHAR(255),
    deleted BOOLEAN,
    CONSTRAINT fk_prescription_patient
        FOREIGN KEY (patient_id) REFERENCES patient(id),
    CONSTRAINT fk_prescription_medicine
        FOREIGN KEY (medicine_id) REFERENCES medicine(id),
    CONSTRAINT fk_prescription_pharmacy
        FOREIGN KEY (pharmacy_id) REFERENCES pharmacy(id)
);

-- DOSE_SCHEDULE
CREATE TABLE dose_schedule (
    id SERIAL PRIMARY KEY,
    prescription_id UUID,
    take_at TIMESTAMP,
    taken BOOLEAN,
    taken_at TIMESTAMP,
    patient_personal_note TEXT,
    created_at TIMESTAMP,
    deleted BOOLEAN,
    CONSTRAINT fk_dose_prescription
        FOREIGN KEY (prescription_id) REFERENCES prescription(id)
);

-- OBSERVATION_SESSION
CREATE TABLE observation_session (
    id UUID PRIMARY KEY,
    patient_id INTEGER,
    dose_schedule_id INTEGER,
    created_at TIMESTAMP,
    CONSTRAINT fk_obs_session_patient
        FOREIGN KEY (patient_id) REFERENCES patient(id),
    CONSTRAINT fk_obs_session_dose
        FOREIGN KEY (dose_schedule_id) REFERENCES dose_schedule(id)
);

-- -- MEASUREMENT_DEFINITION
-- CREATE TABLE measurement_definition (
--     id UUID PRIMARY KEY,
--     name TEXT,
--     unit TEXT
-- );

-- -- SYMTOM_DEFINITION (typo kept as in diagram)
-- CREATE TABLE symtom_definition (
--     id UUID PRIMARY KEY,
--     description VARCHAR(255),
--     name TEXT,
--     icon TEXT
-- );

-- -- SYMTOM_MEASUREMENT
-- CREATE TABLE symtom_measurement (
--     id UUID PRIMARY KEY,
--     symptom_id UUID,
--     measurement_id UUID,
--     max_value VARCHAR(255),
--     min_value VARCHAR(255),
--     mean_value VARCHAR(255),
--     CONSTRAINT fk_sm_symptom
--         FOREIGN KEY (symptom_id) REFERENCES symtom_definition(id),
--     CONSTRAINT fk_sm_measurement
--         FOREIGN KEY (measurement_id) REFERENCES measurement_definition(id)
-- );

-- OBSERVATION
CREATE TABLE observation (
    id UUID PRIMARY KEY,
    observation_session_id UUID,
    symptom_type TEXT,
    measurement_unit TEXT,
    value_boolean BOOLEAN,
    value_numeric FLOAT8,
    value_text TEXT,
    symptom_measurement_id UUID,
    CONSTRAINT fk_observation_session
        FOREIGN KEY (observation_session_id) REFERENCES observation_session(id)
    
);
