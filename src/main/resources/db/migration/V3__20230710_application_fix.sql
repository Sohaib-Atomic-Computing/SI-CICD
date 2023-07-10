ALTER TABLE applications
DROP COLUMN IF EXISTS api_key;

ALTER TABLE applications
DROP COLUMN IF EXISTS secret_key;