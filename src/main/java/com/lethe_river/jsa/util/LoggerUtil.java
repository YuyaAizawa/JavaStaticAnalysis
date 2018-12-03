package com.lethe_river.jsa.util;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public final class LoggerUtil {

	static final String filePath = ""; // ファイルに書き出すときは指定

	private LoggerUtil() {}

	public static Logger getLogger(Class<?> clazz) {
		Logger logger = Logger.getLogger(clazz.getName());

		logger.setLevel(Level.FINE);

		if(!filePath.isEmpty()) {
			FileHandler fileHandler;
			try {
				fileHandler = new FileHandler(filePath, false);
				fileHandler.setFormatter(new SimpleFormatter());
				logger.addHandler(fileHandler);
			} catch (SecurityException | IOException e) {
				throw new RuntimeException(e);
			}
		}

		ConsoleHandler consoleHandler = new ConsoleHandler();
		try {
			consoleHandler.setEncoding("UTF-8");
		} catch (SecurityException | UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
		consoleHandler.setLevel(Level.WARNING);
		logger.addHandler(consoleHandler);

		logger.setUseParentHandlers(false);

		return logger;
	}
}
