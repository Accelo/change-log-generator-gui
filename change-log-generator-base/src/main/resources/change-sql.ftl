<#list table_changes as table, changes>
	<#list filterType(changes, ['A', 'D', 'M', 'R'], false) as change>
		<#if change?is_first>
			<#lt>ALTER table ${table}
		</#if>
		<#if change.modificationType.name() == 'A'>
			<#lt>ADD COLUMN ${change.name} ${change.type} <#if !change.nullable!true>NOT NULL </#if><#if change.defaultValue?has_content>DEFAULT ${change.defaultValue}</#if><#if change.afterColumn?has_content> AFTER ${change.afterColumn}</#if><#rt>
		</#if>
		<#if change.modificationType.name() == 'R'>
			<#lt>RENAME COLUMN ${change.name} TO ${change.newColumn}<#rt>
		</#if>
		<#if change.modificationType.name() == 'D'>
			<#lt>DROP COLUMN ${change.name}<#rt>
		</#if>
		<#if change.modificationType.name() == 'M'>
			<#if change.type?has_content || change.defaultValue?has_content || change.nullable?has_content>
				<#lt>MODIFY COLUMN ${change.name}<#if change.type?has_content> ${change.type}</#if><#if change.defaultValue?has_content> DEFAULT ${change.defaultValue}</#if><#if change.nullable?has_content><#if !change.nullable> NOT</#if> NULL</#if><#rt>
			</#if>
		</#if>
		<#lt><#if change?has_next>,<#else>;${'\n'}</#if>
	</#list>
	<#list filterType(changes, ['A', 'M'], true) as change>
		<#assign fall_back_constraint=table + "_" + change.name/>
		<#if change?is_first>
			<#lt>ALTER table ${table}
		</#if>
		<#if change.modificationType.name() == 'A'>
			<#lt>ADD CONSTRAINT ${fallback(fall_back_constraint, change.uniqueConstraintName)} UNIQUE (${change.name})<#rt>
		</#if>
		<#if change.modificationType.name() == 'M'>
			<#if change.unique>
				<#lt>ADD CONSTRAINT ${fallback(fall_back_constraint, change.uniqueConstraintName)} UNIQUE (${change.name})<#rt>
			<#else>
				<#lt>DROP CONSTRAINT ${fallback(fall_back_constraint, change.uniqueConstraintName)}<#rt>
			</#if>
		</#if>
		<#lt><#if change?has_next>,<#else>;${'\n'}</#if>
	</#list>
</#list>
<#list filterType(changes, ['S'], false) as change>
	<#lt>${change.sql}<#if !change.sql?ends_with(";")>;</#if>${'\n'}
</#list>
<#list filterType(changes, ['U'], false) as change>
	<#lt>UPDATE ${change.table}
	<#lt>SET ${change.name} = ${change.value!"NULL"}<#if !change.where?has_content>;</#if>
	<#lt><#if change.where?has_content>WHERE ${change.where};</#if>
	${'\n'}
</#list>
<#function filterType array, types, uniqueFilter>
	<#local result = []>
	<#list array as item>
		<#if types?seq_contains(item.modificationType.name())>
			<#if uniqueFilter>
				<#if item.modificationType.name() == 'A' && item.unique!false>
					<#local result = result + [item]>
				</#if>
				<#if item.modificationType.name() == 'M' && item.unique??>
					<#local result = result + [item]>
				</#if>
			<#else>
				<#if !(['M']?seq_contains(item.modificationType.name()) && !(item.type?? || item.defaultValue?? || item.nullable??))>
					<#local result = result + [item]>
				</#if>
			</#if>
		</#if>
	</#list>
	<#return result>
</#function>
<#function fallback fallback value = ''>
	<#if value?has_content>
		<#return value>
	<#else>
		<#return fallback>
	</#if>
</#function>
