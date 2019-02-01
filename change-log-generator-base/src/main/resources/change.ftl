<?xml version="1.0" encoding="UTF-8" standalone="no"?>
<databaseChangeLog xmlns="http://www.liquibase.org/xml/ns/dbchangelog"
				   xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
				   xsi:schemaLocation="http://www.liquibase.org/xml/ns/dbchangelog http://www.liquibase.org/xml/ns/dbchangelog/dbchangelog-3.1.xsd">
	<#list config.changeConfigurationList as change>
		<#assign fallback_constraint_name=(change.table!'' + '_' + change.name!'')>
		<#if change.modificationType.name() == 'DR'>
			<changeSet author="${config.authorName}" id="${generator.getId()}">
				<preConditions onFail="MARK_RAN">
					<viewExists viewName="${change.table}"/>
				</preConditions>
				<dropView
					schemaName="accelo"
					viewName="${change.table}"/>
			</changeSet>
			<changeSet author="${config.authorName}" id="${generator.getId()}">
				<preConditions onFail="MARK_RAN">
					<tableExists tableName="${change.table}"/>
				</preConditions>
				<dropTable
					schemaName="accelo_shared"
					tableName="${change.table}"/>
			</changeSet>
		</#if>
		<#if change.modificationType.name() == 'R'>
	<changeSet author="${config.authorName}" id="${generator.getId()}">
		<renameColumn
			newColumnName="${change.newColumn}"
			oldColumnName="${change.name}"
			schemaName="${config.schema}"
			tableName="${change.table}"/>
	</changeSet>
		</#if>
		<#if change.modificationType.name() == 'D'>
	<changeSet author="${config.authorName}" id="${generator.getId()}">
		<dropColumn
			columnName="${change.name}"
			schemaName="${config.schema}"
			tableName="${change.table}"/>
	</changeSet>
		</#if>
		<#if change.modificationType.name() == 'A'>
	<changeSet author="${config.authorName}" id="${generator.getId()}">
		<addColumn tableName="${change.table}" schemaName="${config.schema}">
			<column name="${change.name}" type="${change.type}"<#if change.defaultValue?has_content> <#if change.computed!false>defaultValueComputed="${change.defaultValue}"<#else>defaultValue="${clean(change.defaultValue)}"</#if></#if><#if change.afterColumn?has_content> afterColumn="${change.afterColumn}"</#if><#if change.nullable!true && !change.unique!false>/</#if>>
				<#if !change.nullable!true || change.unique!false>
				<constraints<#if !change.nullable!true> nullable="false"</#if><#if change.unique!false> unique="true"</#if>/>
			</column>
				</#if>
		</addColumn>
	</changeSet>
		</#if>
		<#if change.modificationType.name() == 'M'>
			<#if change.type?has_content>
	<changeSet author="${config.authorName}" id="${generator.getId()}">
		<modifyDataType
			columnName="${change.name}"
			newDataType="${change.type}"
			schemaName="${config.schema}"
			tableName="${change.table}"/>
	</changeSet>
			</#if>
			<#if change.defaultValue?has_content>
	<changeSet author="${config.authorName}" id="${generator.getId()}">
		<addDefaultValue
			columnName="${change.name}"
			defaultValue="${clean(change.defaultValue)}"
			schemaName="${config.schema}"
			tableName="${change.table}"/>
	</changeSet>
			</#if>
			<#if change.nullable?has_content>
				<#if !change.nullable>
	<changeSet author="${config.authorName}" id="${generator.getId()}">
		<addNotNullConstraint
					<#if change.type?has_content>
			columnDataType="${change.type}"
					</#if>
		  	columnName="${change.name}"
		  	schemaName="${config.schema}"
		  	tableName="${change.table}"/>
	</changeSet>
				</#if>
				<#if change.nullable>
	<changeSet author="${config.authorName}" id="${generator.getId()}">
		<dropNotNullConstraint
					<#if change.type?has_content>
			columnDataType="${change.type}"
					</#if>
			columnName="${change.name}"
			schemaName="${config.schema}"
			tableName="${change.table}"/>
	</changeSet>
				</#if>
			</#if>
			<#if change.unique?has_content>
				<#if change.unique>
	<changeSet author="${config.authorName}" id="${generator.getId()}">
		<addUniqueConstraint
					<#if change.type?has_content>
			columnDataType="${change.type}"
					</#if>
			constraintName="${fallback(fallback_constraint_name, change.uniqueConstraintName)}"
			columnName="${change.name}"
			schemaName="${config.schema}"
			tableName="${change.table}"/>
	</changeSet>
				</#if>
				<#if !change.unique>
	<changeSet author="${config.authorName}" id="${generator.getId()}">
		<dropUniqueConstraint
			<#if change.type?has_content>
			columnDataType="${change.type}"
			</#if>
			constraintName="${fallback(fallback_constraint_name, change.uniqueConstraintName)}"
			columnName="${change.name}"
			schemaName="${config.schema}"
			tableName="${change.table}"/>
	</changeSet>
				</#if>
			</#if>
		</#if>
		<#if change.modificationType.name() == 'U'>
	<changeSet author="${config.authorName}" id="${generator.getId()}">
		<update
			schemaName="${config.schema}"
			tableName="${change.table}">
			<column name="${change.name}"<#if change.value?has_content> <#if change.computed!false>valueComputed="${change.value}"<#else>value="${clean(change.value)}"</#if></#if>/>
			<#if change.where?has_content>
			<where>${change.where}</where>
			</#if>
		</update>
	</changeSet>
		</#if>
		<#if change.modificationType.name() == 'S'>
	<changeSet author="${config.authorName}" id="${generator.getId()}">
		<sql>${change.sql}</sql>
	</changeSet>
		</#if>
	</#list>
</databaseChangeLog>
<#function clean str>
	<#if str?matches("^[\'\"].*[\'\"]$")>
		<#return str?substring(1, str?length - 1)>
	</#if>
	<#return str>
</#function>
<#function fallback fallback value = ''>
	<#if value?has_content>
		<#return value>
	<#else>
		<#return fallback>
	</#if>
</#function>
