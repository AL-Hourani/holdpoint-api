ALTER TABLE resources
ADD COLUMN name VARCHAR(150);

UPDATE resources
SET name = resource_key
WHERE name IS NULL;

ALTER TABLE resources
ALTER COLUMN name SET NOT NULL;