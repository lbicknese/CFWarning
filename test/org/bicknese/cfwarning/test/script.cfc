component {

	public void function func1(required string arg1, string arg2) {
		var myVar1 = "";
		myVar2 = myVar1;
		/* commentVar = ""; */
		// commentVar2 = "";
		/*
		* commentVar3 = "";
		*/
		
	}
	
	public string function func2() {
		for(i=1; i<10; i++) {
			myVar3 = i;	
		}	
		
		return myVar3;
	}
	
	public void function func3() {
		var arg = {};
		arg = {
			test = 1,
			more = 2	
		};	
	}

}