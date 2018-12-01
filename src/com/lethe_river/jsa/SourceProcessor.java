package com.lethe_river.jsa;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.FileASTRequestor;

import com.lethe_river.jsa.util.LoggerUtil;

public class SourceProcessor {

	static final Logger logger = LoggerUtil.getLogger(SourceProcessor.class);

	private final List<Path> sourceDirectories;
	private final List<Path> libraryDirectories;

	public SourceProcessor(List<String> sourceDirectories, List<String> libraryDirectories) {
		this.sourceDirectories = sourceDirectories
				.stream()
				.map(Paths::get)
				.collect(Collectors.toList());
		this.libraryDirectories = libraryDirectories
				.stream()
				.map(Paths::get)
				.collect(Collectors.toList());
	}

	public void processSources(ASTVisitor visitor) {
		ASTParser parser = ASTParser.newParser(AST.JLS10);
		parser.setResolveBindings(true);
		parser.setBindingsRecovery(true);

		String[] libraries = libraryDirectories
				.stream()
				.flatMap(base -> {
					try {
						return Files.find(base, 100, (p,a) -> !a.isDirectory());
					} catch (IOException e) {
						logger.warning(e.toString());
						return Stream.empty();
					}
				})
				.filter(path -> path.toFile().getName().endsWith(".jar"))
				.map(Path::toString)
				.toArray(String[]::new);

		String[] sources = sourceDirectories
				.stream()
				.flatMap(base -> {
					try {
						return Files.find(base, 100, (p,a) -> !a.isDirectory());
					} catch (IOException e) {
						logger.warning(e.toString());
						return Stream.empty();
					}
				})
				.filter(path -> path.toFile().getName().endsWith(".java"))
				.map(Path::toString)
				.toArray(String[]::new);

		parser.setEnvironment(
				libraries,
				sourceDirectories
						.stream()
						.map(Path::toString)
						.toArray(String[]::new),
				null,
				true);

		parser.createASTs(sources, null, new String[0], new FileASTRequestor() {
			@Override
			public void acceptAST(String sourceFilePath, CompilationUnit ast) {
				logger.fine("start: "+sourceFilePath);
				ast.accept(visitor);
				logger.fine("end: "+sourceFilePath);
			}
		}, new NullProgressMonitor());
	}
}
