package com.tuannguyen.liquibase.gui.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class ZipUtil
{
	public static Path unzip(Path zipFile) throws IOException
	{
		byte[] buffer = new byte[1024];
		Path outputDir = null;
		try (ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFile.toFile()));) {
			ZipEntry zipEntry = zis.getNextEntry();
			String zipFileName = zipFile.toFile()
					.getName();
			String baseName = zipFileName
					.substring(0, zipFileName.lastIndexOf('.'));
			outputDir = zipFile.getParent()
					.resolve(baseName);
			while (zipEntry != null) {
				String fileName = zipEntry.getName();
				if (!zipEntry.isDirectory()) {
					File newFile = outputDir.resolve(fileName)
							.toFile();
					newFile.getParentFile()
							.mkdirs();
					try (FileOutputStream fos = new FileOutputStream(newFile)) {
						int len;
						while ((len = zis.read(buffer)) > 0) {
							fos.write(buffer, 0, len);
						}
					}
				} else {
					File newDir = outputDir.resolve(fileName)
							.toFile();
					newDir.mkdirs();
				}
				zipEntry = zis.getNextEntry();
			}
			zis.closeEntry();
		}
		return outputDir;
	}
}
