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
			columnDataType="${change.type}"
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
			<column name="${change.name}" type="${change.type}"<#if change.defaultValue?has_content> ${change.valueType.liquibaseDefaultValueName}="${clean(change.defaultValue)}"</#if><#if change.afterColumn?has_content> afterColumn="${change.afterColumn}"</#if><#if change.nullable!true && !change.unique!false>/</#if>>
				<#if !change.nullable!true || change.unique!false>
				<constraints<#if !change.nullable!true> nullable="false"</#if><#if change.unique!false> unique="true"</#if>/>
			</column>
				</#if>
		</addColumn>
		<#if change.extra?has_content>
			<modifySql dbms="mysql">
				<append value=" ${change.extra}"/>
			</modifySql>
		</#if>
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
		<#if change.nullable?has_content || change.defaultValue?has_content || change.extra?has_content>
		<modifySql dbms="mysql">
			<append value="<#if change.nullable?has_content><#if !change.nullable> NOT</#if> NULL</#if><#if change.defaultValue?has_content> DEFAULT ${wrapValue(change, clean(change.defaultValue))}</#if><#if change.extra?has_content> ${change.extra}</#if>"/>
		</modifySql>
		</#if>
	</changeSet>
			<#else>
				<#if change.defaultValue?has_content>
					<changeSet author="${config.authorName}" id="${generator.getId()}">
						<addDefaultValue
							columnName="${change.name}"
							${change.valueType.liquibaseDefaultValueName}="${clean(change.defaultValue)}"
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
			<column name="${change.name}"<#if change.value?has_content> ${change.valueType.liquibaseValueName}="${clean(change.value)}"</#if>/>
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
		<#if change.modificationType.name() == 'I'>
			<changeSet author="${config.authorName}" id="${generator.getId()}">
				<createIndex indexName="${change.name}" schemaName="${config.schema}" tableName="${change.table}">
					<column name="accelo_deployment"/>
					<#list getIndexColumns(change.value) as column>
						<column name="${column.name}" <#if column.descend!false>descend="true"</#if>/>
					</#list>
				</createIndex>
			</changeSet>
		</#if>
	</#list>
</databaseChangeLog>
<#function getIndexColumns value>
	<#local indexColumns = []>
	<#local indexParts = value?split(',')>
	<#list indexParts as part>
		<#local descend=false>
		<#local name=part?trim>
		<#if part?upper_case?ends_with('ASC')>
			<#local name=part?replace('asc$', '', 'ri')>
		</#if>
		<#if part?upper_case?ends_with('DESC')>
			<#local name=part?replace('desc$', '', 'ri')>
			<#local descend=true>
		</#if>
		<#local indexColumn={
			"descend": descend,
			"name": name?trim
		}>
		<#local indexColumns = indexColumns + [ indexColumn ] />
	</#list>
	<#return indexColumns>
</#function>
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
<#function wrapValue change value>
	<#if ['STRING', 'DATE']?seq_contains(change.valueType.name())>
		<#return "'${value}'">
	<#else>
		<#return value>
	</#if>
</#function>
