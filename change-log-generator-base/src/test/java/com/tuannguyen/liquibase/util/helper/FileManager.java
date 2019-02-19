package com.tuannguyen.liquibase.util.helper;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class FileManager
{
	private Map<File, String> originalFileMapping;

	public FileManager()
	{
		originalFileMapping = new HashMap<>();
	}

	public Optional<String> addNewFile(File file)
	{
		try {
			String content = getFileContent(file).orElse("");
			originalFileMapping.put(file, content);
			return Optional.of(content);
		} catch (Exception e) {
			return Optional.empty();
		}
	}

	public Optional<String> getFileContent(File file)
	{
		try {
			return Optional.of(new String(Files.readAllBytes(file.toPath())));
		} catch (IOException e) {
			return Optional.empty();
		}
	}

	public Optional<String> getOriginalContent(File file)
	{
		return Optional.ofNullable(originalFileMapping.get(file));
	}

	public void clean()
	{
		for (Map.Entry<File, String> entry : originalFileMapping.entrySet()) {
			if (entry.getKey().exists()) {
				try {
					Files.write(entry.getKey().toPath(), entry.getValue().getBytes());
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
