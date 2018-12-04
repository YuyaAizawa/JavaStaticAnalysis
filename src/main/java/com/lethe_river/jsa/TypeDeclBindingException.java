package com.lethe_river.jsa;

import org.eclipse.jdt.core.dom.AbstractTypeDeclaration;

public class TypeDeclBindingException extends BindingException {

	public TypeDeclBindingException(AbstractTypeDeclaration decl) {
		super("type: "+decl.getName().getIdentifier());
	}
}
