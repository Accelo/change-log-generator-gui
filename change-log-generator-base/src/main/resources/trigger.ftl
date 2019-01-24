<?xml version="1.0" encoding="UTF-8" standalone="no"?>
<databaseChangeLog xmlns="http://www.liquibase.org/xml/ns/dbchangelog"
				   xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
				   xsi:schemaLocation="http://www.liquibase.org/xml/ns/dbchangelog http://www.liquibase.org/xml/ns/dbchangelog/dbchangelog-3.1.xsd">
	<changeSet author="${config.authorName}" id="${timestamp}" runOnChange="true">
		<sql>DROP TRIGGER IF EXISTS ${trigger};</sql>
		<sql endDelimiter="\n;">CREATE TRIGGER ${trigger}
			BEFORE INSERT ON ${config.schema}.${metadata.name}
			FOR EACH ROW
				BEGIN
					DECLARE vDep varchar(50);
					DECLARE vSeq varchar(50);
					SELECT accelo.getDeploymentId() INTO vDep;
					IF NEW.accelo_deployment IS NULL OR NEW.accelo_deployment = '' THEN
						SET NEW.accelo_deployment = vDep;
					END IF;
						IF NEW.id IS NULL OR NEW.id &lt; 1 THEN
					SET NEW.id = accelo_shared.getNextId(NEW.accelo_deployment, '${metadata.name}');
				END IF;
			END;
			;
		</sql>
	</changeSet>
</databaseChangeLog>