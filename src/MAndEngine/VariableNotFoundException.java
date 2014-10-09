package MAndEngine;



public class VariableNotFoundException extends Exception {
	@Override
	public String getMessage() {
		return "Variable not found";
	}
}
