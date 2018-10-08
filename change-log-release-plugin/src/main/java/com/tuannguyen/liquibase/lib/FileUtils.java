package com.tuannguyen.liquibase.lib;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class FileUtils {
	public static void emptyDirectory(Path path) throws IOException {
		File directory = path.toFile();
		if (!directory.exists() || !directory.isDirectory()) {
			throw new IOException("Directory " + directory.getAbsolutePath() + " does not exists");
		}
		File[] files = directory.listFiles();
		if (files != null) {
			for (File file : files) {
				if (file.isDirectory()) {
					emptyDirectory(file.toPath());
				} else {
					file.delete();
				}
			}
		}
	}

	public static void newDir(Path path) throws IOException {
		if (!Files.exists(path)) {
			Files.createDirectory(path);
		}
	}

	public static void zip(File source, File target) throws IOException {
		if (!source.exists()) {
			throw new IOException("File " + source.getAbsolutePath() + " not exists");
		}
		if (source.isDirectory()) {
			zipDirectory(source, target);
		} else {
			zipSingleFile(source, target);
		}
	}

	private static List<File> populateFilesList(File file) throws IOException {
		List<File> fileList = new ArrayList<>();
		if (file.isDirectory()) {
			File[] dirEntries = file.listFiles();
			if (dirEntries != null) {
				for (File dirEntry : dirEntries) {
					if (dirEntry.isDirectory()) {
						fileList.addAll(populateFilesList(dirEntry));
					} else {
						fileList.add(dirEntry);
					}
				}
			}
		} else {
			fileList.add(file);
		}
		return fileList;
	}

	private static void zipDirectory(File source, File target) {
		try (FileOutputStream fos = new FileOutputStream(target); ZipOutputStream zos = new ZipOutputStream(fos);) {
			List<File> filesListInDir = populateFilesList(source);
			for (File file : filesListInDir) {
				try (FileInputStream fis = new FileInputStream(file)) {
					String absoluteFilePath = file.getAbsolutePath();
					String entryName = absoluteFilePath
							.substring(
									source.getAbsolutePath()
									      .length() + 1,
									absoluteFilePath.length()
							);
					//for ZipEntry we need to keep only relative file path, so we used substring on absolute path
					ZipEntry ze = new ZipEntry(entryName);
					zos.putNextEntry(ze);
					byte[] buffer = new byte[1024];
					int    len;
					while ((len = fis.read(buffer)) > 0) {
						zos.write(buffer, 0, len);
					}
					zos.closeEntry();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void zipSingleFile(File source, File target) throws IOException {
		try (
				FileOutputStream fos = new FileOutputStream(target);
				FileInputStream fis = new FileInputStream(source);
				ZipOutputStream zos = new ZipOutputStream(fos);
		) {
			ZipEntry ze = new ZipEntry(source.getName());
			zos.putNextEntry(ze);
			byte[] buffer = new byte[1024];
			int    len;
			while ((len = fis.read(buffer)) > 0) {
				zos.write(buffer, 0, len);
			}
			zos.closeEntry();
		}
	}
}
