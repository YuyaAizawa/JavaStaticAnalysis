package com.lethe_river.jsa;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.MethodDeclaration;

import com.lethe_river.jsa.util.LoggerUtil;

public class MethodDeclVisitor extends ASTVisitor {
	private static Logger logger = LoggerUtil.getLogger(MethodDeclVisitor.class);

	@Override
	public boolean visit(MethodDeclaration node) {
		try {
			System.out.println(getMethodName(node));
		} catch(MethodDeclBindingException e) {
			logger.warning("binding cannot beresolved " + e.getMessage());
		}
		return super.visit(node);
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
