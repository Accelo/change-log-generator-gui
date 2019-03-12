<#list table_changes as table, changes>
	<#list filterType(changes, ['DR'], false) as change>
		<#lt>DROP TABLE IF EXISTS ${table};	${'\n'}
	</#list>
	<#list filterType(changes, ['I'], false) as change>
		<#lt>CREATE<#if change.unique!false> UNIQUE</#if> INDEX ${change.name} ON ${change.table} (${change.value}); ${'\n'}
	</#list>
	<#list filterType(changes, ['A', 'D', 'M', 'R'], false) as change>
		<#if change?is_first>
			<#lt>ALTER TABLE ${table}
		</#if>
		<#if change.modificationType.name() == 'A'>
			<#lt>ADD COLUMN ${change.name} ${change.type}<#if !(change.nullable)!true> NOT NULL</#if><#if (change.nullable!true) && (change.defaultValue!'')?matches("NULL", "i")> NULL</#if><#if change.defaultValue?has_content> DEFAULT ${wrapValue(change, change.defaultValue)}</#if><#if change.afterColumn?has_content> AFTER ${change.afterColumn}</#if><#if change.extra?has_content> ${change.extra}</#if><#rt>
		</#if>
		<#if change.modificationType.name() == 'R'>
			<#lt>RENAME COLUMN ${change.name} TO ${change.newColumn}<#rt>
		</#if>
		<#if change.modificationType.name() == 'D'>
			<#lt>DROP COLUMN ${change.name}<#rt>
		</#if>
		<#if change.modificationType.name() == 'M'>
			<#if change.type?has_content>
				<#lt>MODIFY COLUMN ${change.name} ${change.type}<#if change.nullable?has_content><#if !change.nullable> NOT</#if> NULL</#if><#if change.defaultValue?has_content> DEFAULT ${wrapValue(change, change.defaultValue)}</#if><#if change.extra?has_content> ${change.extra}</#if><#rt>
			<#else>
				<#lt>ALTER COLUMN ${change.name} SET DEFAULT <#if change.defaultValue?has_content>${wrapValue(change, change.defaultValue)}</#if><#rt>
			</#if>
		</#if>
		<#lt><#if change?has_next>,<#else>;${'\n'}</#if>
	</#list>
	<#list filterType(changes, ['A', 'M'], true) as change>
		<#assign fall_back_constraint=table + "_" + change.name/>
		<#if change?is_first>
			<#lt>ALTER TABLE ${table}
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
	<#lt>SET ${change.name} = ${wrapValue(change, change.value)}<#if !change.where?has_content>;</#if>
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
<#function wrapValue change value>
	<#if ['STRING', 'DATE']?seq_contains(change.valueType.name())>
		<#return "'${value}'">
	<#else>
		<#return value>
	</#if>
</#function>
