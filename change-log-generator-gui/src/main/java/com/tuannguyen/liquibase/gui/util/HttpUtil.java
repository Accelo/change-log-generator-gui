package com.tuannguyen.liquibase.gui.util;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.async.Callback;
import com.mashape.unirest.http.exceptions.UnirestException;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class HttpUtil {
	public static String get(String url) throws UnirestException {
		return Unirest.get(url)
		              .asString()
		              .getBody();
	}

	public static void save(String url, Path targetPath, Runnable callback) {
		Unirest.get(url).asBinaryAsync(new Callback<InputStream>() {
			@Override
			public void completed(HttpResponse<InputStream> httpResponse) {
				try {
					InputStream inputStream = httpResponse.getBody();
					Files.copy(inputStream, targetPath, StandardCopyOption.REPLACE_EXISTING);
					callback.run();
				} catch (Exception e) {
					throw new RuntimeException("Failed to download the update", e);
				}
			}

			@Override
			public void failed(UnirestException e) {
				throw new RuntimeException("Failed to update the application", e);
			}

			@Override
			public void cancelled() {
				throw new RuntimeException("Request was cancelled");
			}
		});

	}
}
