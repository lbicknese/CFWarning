<cfcomponent>

	<cffunction name="funcOne">
	
		<cfset myVar = '' />
		<cfset myVar2 = '' />
		<cfset var myVar3 = '' />
		<!---
		<cfset myVar4 = '' />
		--->
	
	</cffunction>
	
	<cffunction name="funcTwo">
	
		<cfquery name="test" datasource="any">
			select * from table where column = 1
		</cfquery>
	
	</cffunction>
	
	<cffunction name="funcThree" output="false">
		<cfargument name="arg1" required="true">
		
		<cfscript>
			var myVar1 = '';
			myVar2 = '';
			//myVar3 = '';
		</cfscript>
		
		<cfset myVar1 = 1 />
		<cfset myVar2 = 2 />
		
		<cfreturn myVar2 />		
		
	</cffunction>
	
	<cffunction name="funcFour">
		
		<cfinvoke method="funcThree"
			arg1=" and a = b"
			returnvariable="myVar1">
		
		<cfinvoke method="funcThree"
			arg1=" and a = b"
			returnvariable="myVar2">
		</cfinvoke>
		
		
		<cfinvoke method="funcThree"
			arg1=" and a = b"
			returnvariable="myVar3">
		
		
	</cffunction>

</cfcomponent>