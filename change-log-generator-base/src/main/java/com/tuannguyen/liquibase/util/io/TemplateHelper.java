package com.tuannguyen.liquibase.util.io;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

public class TemplateHelper {
	private Configuration configuration;

	public TemplateHelper() {
		configuration = new Configuration(Configuration.VERSION_2_3_23);
		configuration.setDefaultEncoding("UTF-8");
		configuration.setClassForTemplateLoading(this.getClass(), "/");
		configuration.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
	}

	public void write(File file, String templateFile, Map<String, Object> data) {
		try (Writer writer = Files.newBufferedWriter(file.toPath(), Charset.defaultCharset())) {
			write(writer, templateFile, data);
		} catch (IOException e) {
			throw new ResultException("Failed to process file " + templateFile, e);
		}
	}

	public void write(Writer writer, String templateFile, Map<String, Object> data) {
		try {
			Template template = configuration.getTemplate(templateFile);
			template.process(data, writer);
		} catch (IOException e) {
			throw new ResultException("Failed to process file " + templateFile, e);
		} catch (TemplateException e) {
			throw new ResultException("Failed to process template " + templateFile, e);
		}
	}

	public File prepareDir(String dirName, String... more) {
		Path path    = Paths.get(dirName, more);
		File dirFile = path.toFile();
		if (!dirFile.exists()) {
			boolean success = dirFile.mkdirs();
			if (!success) {
				throw new ResultException("Cannot create directory " + dirFile.getAbsolutePath());
			}
		}
		return dirFile;
	}
}
