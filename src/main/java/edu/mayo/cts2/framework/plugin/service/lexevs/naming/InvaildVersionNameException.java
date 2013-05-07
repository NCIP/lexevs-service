package edu.mayo.cts2.framework.plugin.service.lexevs.naming;

public class InvaildVersionNameException extends RuntimeException {

	private static final long serialVersionUID = -765984153939066266L;
	
	protected InvaildVersionNameException(String name){
		super("Version name: " + name + " is not a valid CTS2 Version name.");
	}

}
