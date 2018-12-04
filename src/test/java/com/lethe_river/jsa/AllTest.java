package com.lethe_river.jsa;

import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

import com.lethe_river.jsa.util.FileUtil;

public class AllTest {
	@Test
	public void jhotdraw8LibraryLoadTest() {

		SourceProcessor src = new SourceProcessor(
				List.of("./jhotdraw8/src/main/java").stream()
						.map(FileUtil::getPathOnCurrentEnvironment)
						.collect(Collectors.toList()),
				List.<String>of("./jhotdraw8/lib").stream()
						.map(FileUtil::getPathOnCurrentEnvironment)
						.collect(Collectors.toList())
				);

		src.processSources(new MethodDeclVisitor());
	}
}
