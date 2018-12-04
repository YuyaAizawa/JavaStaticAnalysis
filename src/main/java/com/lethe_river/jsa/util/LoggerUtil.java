package com.lethe_river.jsa.util;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public final class LoggerUtil {

	static final String dirPath = "/../log"; // ファイルに書き出すときは指定

	private LoggerUtil() {}

	public static Logger getLogger(Class<?> clazz) {
		Logger logger = Logger.getLogger(clazz.getName());

		logger.setLevel(Level.WARNING);

		if(!dirPath.isEmpty()) {
			FileHandler fileHandler;
			try {
				fileHandler = new FileHandler(getPattern(clazz), 8000, 100, false);
				fileHandler.setFormatter(new SimpleFormatter());
				fileHandler.setEncoding("UTF-8");
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
		consoleHandler.setFormatter(new SimpleFormatter());
		consoleHandler.setLevel(Level.WARNING);
		logger.addHandler(consoleHandler);

		logger.setUseParentHandlers(false);

		return logger;
	}

	private static String getPattern(Class<?> clazz) {
		String time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
		return FileUtil.getPathOnCurrentEnvironment(".")+dirPath+"/"+clazz.getName()+time+".log";
	}
}
