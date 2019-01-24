<?xml version="1.0" encoding="UTF-8" standalone="no"?>
<databaseChangeLog xmlns="http://www.liquibase.org/xml/ns/dbchangelog"
				   xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
				   xsi:schemaLocation="http://www.liquibase.org/xml/ns/dbchangelog http://www.liquibase.org/xml/ns/dbchangelog/dbchangelog-3.1.xsd">
	<changeSet author="${config.authorName}" id="${generator.getId()}">
		<createTable schemaName="${config.schema}" tableName="${metadata.name}">
			<column name="accelo_deployment" type="INT UNSIGNED">
				<constraints nullable="false"/>
			</column>
			<#list metadata.columnMetadata as column>
			<#if column.primaryKey>
				<#assign primaryKey=column.name>
			</#if>
			<column<#if column.defaultValue?has_content> <#if column.defaultComputed>defaultValueComputed<#else>defaultValue</#if>="${column.defaultValue}"</#if><#if column.comment?has_content> remarks="${column.comment}"</#if> name="${column.name}" type="${column.type}">
				<#if !column.nullable || column.unique>
				<constraints<#if !column.nullable> nullable="${column.nullable?c}"</#if>/>
				</#if>
			</column>
			</#list>
		</createTable>
	</changeSet>
	<#list metadata.indexMetadata as indexInfo>
		<#if indexInfo.columns[0].name == primaryKey>
	<changeSet author="${config.authorName}" id="${generator.getId()}">
		<addPrimaryKey columnNames="accelo_deployment, ${primaryKey}" constraintName="PRIMARY" schemaName="${config.schema}" tableName="${metadata.name}"/>
	</changeSet>
	<#else>
	<changeSet author="${config.authorName}" id="${generator.getId()}">
		<createIndex indexName="${indexInfo.name}" schemaName="${config.schema}" tableName="${indexInfo.table}" <#if indexInfo.unique>unique="${indexInfo.unique?c}"</#if>>
			<column name="accelo_deployment"/>
			<#list indexInfo.columns as indexPart>
			<column name="${indexPart.name}" <#if indexPart.direction.toString() == 'DESC'>descending="true"</#if>/>
			</#list>
		</createIndex>
	</changeSet>
		</#if>
	</#list>
</databaseChangeLog>
