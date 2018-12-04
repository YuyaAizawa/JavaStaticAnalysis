package com.lethe_river.jsa;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.AbstractTypeDeclaration;
import org.eclipse.jdt.core.dom.EnumDeclaration;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclaration;

import com.lethe_river.jsa.util.LoggerUtil;

public class MethodDeclVisitor extends ASTVisitor {
	private static Logger logger = LoggerUtil.getLogger(MethodDeclVisitor.class);

	private Deque<String> typeStack = new ArrayDeque<>();

	@Override
	public boolean visit(MethodDeclaration node) {
		try {
			System.out.println(getMethodName(node));
		} catch(MethodDeclBindingException e) {
			logger.warning("binding cannot be resolved. " + typeStack.peek() + "#" + node.getName());
		}
		return super.visit(node);
	}

	private void visit(AbstractTypeDeclaration node) {
		// ClassまたはEnumを見つけたとき
		try {
			typeStack.push(getQualifiedName(node));
		} catch(TypeDeclBindingException e) {
			typeStack.push("UNRESOLVED");
			logger.warning("binding cannot be resolved. "+e.getMessage());
		}
	}

	private void endVisit(AbstractTypeDeclaration node) {
		typeStack.pop();
	}

	@Override
	public boolean visit(TypeDeclaration node) {
		visit((AbstractTypeDeclaration) node);
		return super.visit(node);
	}
	@Override
	public void endVisit(TypeDeclaration node) {
		endVisit((AbstractTypeDeclaration) node);
	}

	@Override
	public boolean visit(EnumDeclaration node) {
		visit((AbstractTypeDeclaration) node);
		return super.visit(node);
	}
	@Override
	public void endVisit(EnumDeclaration node) {
		endVisit((AbstractTypeDeclaration) node);
	}

	private String getQualifiedName(AbstractTypeDeclaration decl) {
		ITypeBinding type = decl.resolveBinding();

		if(type == null) {
			throw new TypeDeclBindingException(decl);
		}

		String name = type.getQualifiedName();
		return name;
	}

	private String getMethodName(MethodDeclaration decl) {
		IMethodBinding method = decl.resolveBinding();

		if(method == null) {
			throw new MethodDeclBindingException(decl);
		}

		String name = method.isConstructor() ? "<init>" : method.getName();

		List<String> paramTys =
				Arrays.stream(method.getParameterTypes())
						.map(this::getBinaryName)
						.collect(Collectors.toList());

		String retTy = getBinaryName(method.getReturnType());

		return name + "(" + paramTys.stream().collect(Collectors.joining("")) + ")" + retTy;
	}

	// メソッドの引数と戻り値はこうしないと変換できない
	private String getBinaryName(ITypeBinding ty) {
		String tyName = ty.getBinaryName();
		return ty.isPrimitive() ? tyName : "L" + tyName + ";";
	}
}
