ALTER TABLE test2
ADD COLUMN new INT(10) NOT NULL DEFAULT 6;

ALTER TABLE test
ADD COLUMN new1 varchar(20) NOT NULL DEFAULT 'a' AFTER old,
MODIFY COLUMN new2 BOOLEAN DEFAULT TRUE NOT NULL,
ALTER COLUMN new2 SET DEFAULT '1997/07/23';

ALTER TABLE test
ADD CONSTRAINT test UNIQUE (new1),
ADD CONSTRAINT test_new2 UNIQUE (new2);

CREATE INDEX test_index ON index_table (a DESC, b ASC, c desc, d asc);

CREATE UNIQUE INDEX test_index ON index_table (a DESC, b ASC, c desc, d asc);

ALTER TABLE job_settings
MODIFY COLUMN editing_level enum('editable', 'not_editable') DEFAULT 'editable',
ADD COLUMN test_date DATE DEFAULT NOW() ON UPDATE CURRENT TIMESTAMP,
RENAME COLUMN updated_date TO new_updated_date;

ALTER TABLE unique_test
ADD CONSTRAINT unique_constraint UNIQUE (now_unique),
DROP CONSTRAINT unique_test_not_unique;

DROP TABLE IF EXISTS blah;

ALTER TABLE update_table
MODIFY COLUMN new_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP;

ALTER TABLE bye
DROP COLUMN delete_column;

UPDATE test SET value = 7;

UPDATE update_table
SET update_column = 'a'
WHERE b IS NOT NULL;

UPDATE update_table
SET update_all_column = 'b';

UPDATE job_settings
SET updated_date = NOW()
WHERE id = 7;

UPDATE job_settings
SET new_date = '12'
WHERE id = 7;

UPDATE job_settings
SET new_date = NULL
WHERE id = 7;

UPDATE expense
SET created = IF(incurred IS NULL, NULL, CONCAT(incurred, '12:00:00'));
