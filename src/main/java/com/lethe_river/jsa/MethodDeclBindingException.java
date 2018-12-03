package com.lethe_river.jsa;

import org.eclipse.jdt.core.dom.MethodDeclaration;

public class MethodDeclBindingException extends BindingException {

	public MethodDeclBindingException(MethodDeclaration decl) {
		super("method: " +decl.getName().getIdentifier());
	}
}
