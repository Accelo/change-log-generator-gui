<?xml version="1.0" encoding="UTF-8" standalone="no"?>
<databaseChangeLog xmlns="http://www.liquibase.org/xml/ns/dbchangelog" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://www.liquibase.org/xml/ns/dbchangelog http://www.liquibase.org/xml/ns/dbchangelog/dbchangelog-3.1.xsd">
  <changeSet author="${authorName}" id="${timestamp}" runOnChange="true">
    <createView schemaName="accelo" viewName="${metadata.name}" replaceIfExists="true">
select
<#list metadata.columnMetadata as column>
  `${config.schema}`.`${metadata.name}`.`${column.name}` AS `${column.name}`<#if column?has_next>,</#if>
</#list>
from `${config.schema}`.`${metadata.name}`
where (`${config.schema}`.`${metadata.name}`.`accelo_deployment` = (SELECT id FROM ${config.schema}.accelo_deployment WHERE username = (SELECT SUBSTRING_INDEX(USER(),'@',1))))</createView>
  </changeSet>
</databaseChangeLog>