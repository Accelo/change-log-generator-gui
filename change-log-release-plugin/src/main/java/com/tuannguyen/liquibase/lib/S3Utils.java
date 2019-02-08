package com.tuannguyen.liquibase.lib;

import java.nio.file.Path;

import com.amazonaws.event.ProgressListener;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.PutObjectRequest;

public class S3Utils
{
	private static final AmazonS3 s3 = AmazonS3ClientBuilder.defaultClient();

	public static void upload(String bucket, Path file, ProgressListener progressListener)
	{
		PutObjectRequest putObjectRequest = new PutObjectRequest(
				bucket,
				file.toFile()
						.getName(),
				file.toFile()
		);
		putObjectRequest.setGeneralProgressListener(progressListener);
		s3.putObject(putObjectRequest);
	}

	public static void upload(String bucket, Path file)
	{
		s3.putObject(
				bucket,
				file.toFile()
						.getName(),
				file.toFile()
		);
	}
}
