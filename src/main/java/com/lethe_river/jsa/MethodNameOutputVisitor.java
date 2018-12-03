package com.lethe_river.jsa;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.MethodDeclaration;

public class MethodNameOutputVisitor extends ASTVisitor {
	@Override
	public boolean visit(MethodDeclaration node) {

		System.out.println(node.getName());

		return super.visit(node);
	}
}
