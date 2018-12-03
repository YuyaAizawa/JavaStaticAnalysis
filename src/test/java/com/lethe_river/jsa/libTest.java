package com.lethe_river.jsa;

import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

public class libTest {
	@Test
	public void jhotdraw8LibraryLoadTest() {

		SourceProcessor src = new SourceProcessor(
				List.of("./jhotdraw8/src/main/java").stream()
						.map(this::getPathOnCurrentEnvironment)
						.collect(Collectors.toList()),
				List.<String>of("./jhotdraw8/lib").stream()
						.map(this::getPathOnCurrentEnvironment)
						.collect(Collectors.toList())
				);

		src.processSources(new MethodDeclVisitor());
	}

	private String getPathOnCurrentEnvironment(String path) {
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		try {
			return Paths.get(classLoader.getResource(path).toURI()).toString();
		} catch (URISyntaxException e) {
			throw new RuntimeException(e);
		}
	}
}
