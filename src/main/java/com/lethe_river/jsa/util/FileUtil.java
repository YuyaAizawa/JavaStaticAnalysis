package com.lethe_river.jsa.util;

import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileUtil {
	private FileUtil() {}

	public static String getPathOnCurrentEnvironment(String path) {
		ClassLoader classLoader = FileUtil.class.getClassLoader();
		if(classLoader == null) {
			System.err.println("ClassLoader is null!");
		}
		try {
			URL resource = classLoader.getResource(path);
			if(resource == null) {
				System.err.println("resource not found: "+path);
			}
			Path path2 = Paths.get(resource.toURI());
			if(path2 == null) {
				System.err.println("path is null!");
			}
			return path2.toString();
		} catch (URISyntaxException e) {
			throw new RuntimeException(e);
		}
	}
}
