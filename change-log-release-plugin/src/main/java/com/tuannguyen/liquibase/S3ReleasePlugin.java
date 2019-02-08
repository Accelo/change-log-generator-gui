package com.tuannguyen.liquibase;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.resolver.ArtifactResolutionRequest;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.ProjectDependenciesResolver;
import org.apache.maven.repository.RepositorySystem;
import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.resolution.ArtifactResolutionException;

import com.tuannguyen.liquibase.lib.FileUtils;
import com.tuannguyen.liquibase.lib.S3Utils;
import com.tuannguyen.liquibase.lib.TemplateHelper;

import freemarker.template.TemplateException;
import me.tongfei.progressbar.ProgressBar;

@Mojo(name = "s3-release", requiresDependencyResolution = ResolutionScope.COMPILE)
public class S3ReleasePlugin extends AbstractMojo
{
	private static final String LIB_PATH = "lib";

	private static final String APP_ZIP = "app";

	private static final String VERSION_FILE = "version";

	@Component
	private MavenProject mavenProject;

	@Component
	private ProjectDependenciesResolver projectDependenciesResolver;

	@Parameter
	private String bucket;

	@Parameter(defaultValue = "artifacts")
	private String artifactPaths;

	@Parameter
	private String mainAppName;

	@Parameter
	private String mainClass;

	@Parameter(defaultValue = "${repositorySystemSession}")
	private RepositorySystemSession repoSession;

	@Component
	private RepositorySystem repositorySystem;

	private TemplateHelper templateHelper = new TemplateHelper();

	public void execute()
			throws MojoExecutionException
	{
		try {
			List<Artifact> dependencies = getDependencies(mavenProject);

			Path targetArtifactsPath = Paths.get(artifactPaths)
					.toAbsolutePath();
			prepareTargetDir(targetArtifactsPath);

			Path libPath = targetArtifactsPath.resolve(LIB_PATH);
			copyDependencies(dependencies, libPath);

			Artifact artifact = getAppArtifact(mavenProject);
			mavenProject.setArtifact(artifact);
			copyMainApp(artifact, targetArtifactsPath);

			createConfigFile(mavenProject, targetArtifactsPath);
			Path versionPath = createVersionFile(mavenProject, targetArtifactsPath);

			Path tempDir = Files.createTempDirectory("change-log-generator");
			String zipFileName = APP_ZIP + ".zip";
			Path zipPath = tempDir.resolve(zipFileName);
			zipFolder(targetArtifactsPath, zipPath);
			zipPath = Files.move(zipPath, targetArtifactsPath.resolve(zipFileName));

			uploadToS3WithProgress(bucket, zipPath);
			uploadToS3(bucket, versionPath);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void uploadToS3(String bucket, Path filePath)
	{
		S3Utils.upload(bucket, filePath);
	}

	private void uploadToS3WithProgress(String bucket, Path filePath)
	{
		File file = filePath.toFile();
		try (ProgressBar pb = new ProgressBar("Uploading", file.length())) {
			pb.setExtraMessage("Done");
			S3Utils.upload(bucket, filePath, progressEvent ->
					pb.stepBy(progressEvent.getBytesTransferred())
			);
		}
	}

	private Path createVersionFile(MavenProject mavenProject, Path targetPath) throws IOException
	{
		String version = mavenProject.getVersion()
				.replace("-SNAPSHOT", "");
		Path path = targetPath.resolve(VERSION_FILE);
		Files.write(path, version.getBytes("utf-8"));
		return path;
	}

	private void zipFolder(Path sourcePath, Path destPath) throws IOException
	{
		FileUtils.zip(sourcePath.toFile(), destPath.toFile());
	}

	private void createConfigFile(MavenProject mavenProject, Path targetPath) throws IOException, TemplateException
	{
		List<Artifact> dependencies = getDependencies(mavenProject);
		Map<String, Object> data = new HashMap<>();
		data.put("appName", mainAppName);
		data.put(
				"appVersion",
				mavenProject.getVersion()
						.replace("-SNAPSHOT", "")
		);
		data.put("mainClass", mainClass);
		data.put(
				"appId",
				mavenProject.getGroupId()
						.replace(".", "/")
		);
		data.put("appGroup", mavenProject.getGroupId());
		String classPath = dependencies.stream()
				.filter(artifact -> artifact.getFile() != null)
				.map(artifact -> Paths.get(
						"lib",
						artifact.getFile()
								.getName()
				))
				.map(Path::toString)
				.collect(Collectors.joining(File.pathSeparator));
		data.put("classPath", classPath);
		File configFile = targetPath.resolve(mainAppName + ".cfg")
				.toFile();
		templateHelper.write(configFile, "app-config.ftl", data);
	}

	private void copyMainApp(Artifact artifact, Path targetArtifactsPath) throws IOException
	{
		File libFile = artifact.getFile();
		if (libFile != null) {
			Files.copy(
					libFile.toPath(),
					targetArtifactsPath.resolve(mainAppName + "-jfx.jar"),
					StandardCopyOption.REPLACE_EXISTING
			);
		}
	}

	private Artifact getAppArtifact(MavenProject mavenProject) throws ArtifactResolutionException
	{
		ArtifactResolutionRequest artifactRequest = new ArtifactResolutionRequest();
		artifactRequest.setArtifact(mavenProject.getArtifact());
		return repositorySystem.resolve(artifactRequest)
				.getArtifacts()
				.iterator()
				.next();
	}

	private void copyDependencies(List<Artifact> dependencies, Path targetPath) throws IOException
	{
		for (Artifact dependency : dependencies) {
			File libFile = dependency.getFile();
			if (libFile != null) {
				Files.copy(
						libFile.toPath(),
						targetPath.resolve(libFile.getName()),
						StandardCopyOption.REPLACE_EXISTING
				);
			}
		}
	}

	private List<Artifact> getDependencies(MavenProject mavenProject)
	{
		return mavenProject.getArtifacts()
				.stream()
				.filter(artifact -> Artifact.SCOPE_COMPILE.equals(artifact.getScope()))
				.collect(Collectors.toList());
	}

	private void prepareTargetDir(Path path) throws IOException
	{
		FileUtils.newDir(path);
		FileUtils.emptyDirectory(path);
		FileUtils.newDir(path.resolve(LIB_PATH));
	}
}

