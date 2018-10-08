package com.tuannguyen.liquibase.lib;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.Map;

public class TemplateHelper {
	private Configuration configuration;

	public TemplateHelper() {
		configuration = new Configuration(Configuration.VERSION_2_3_23);
		configuration.setDefaultEncoding("UTF-8");
		configuration.setClassForTemplateLoading(this.getClass(), "/");
		configuration.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
	}

	public void write(File file, String templateFile, Map<String, Object> data) throws IOException, TemplateException {
		try (Writer writer = Files.newBufferedWriter(file.toPath(), Charset.defaultCharset())) {
			Template template = configuration.getTemplate(templateFile);
			template.process(data, writer);
		}
	}
}
