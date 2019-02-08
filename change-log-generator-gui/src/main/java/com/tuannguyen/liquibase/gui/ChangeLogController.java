package com.tuannguyen.liquibase.gui;

import com.mashape.unirest.http.exceptions.UnirestException;
import com.tuannguyen.liquibase.config.model.ChangeConfiguration;
import com.tuannguyen.liquibase.config.model.GenerateChangeConfiguration;
import com.tuannguyen.liquibase.gui.model.BasicInformation;
import com.tuannguyen.liquibase.gui.model.ChangeInformation;
import com.tuannguyen.liquibase.gui.ui.ProgressBarAlert;
import com.tuannguyen.liquibase.gui.util.*;
import com.tuannguyen.liquibase.util.container.BeanFactory;
import com.tuannguyen.liquibase.util.io.columns.ChangeWriter;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Accordion;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.stage.Stage;

import java.awt.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.function.Consumer;
import java.util.stream.Collectors;


public class ChangeLogController {

	private static final String APP_NAME      = "ChangeLogGenerator";
	public static final  String PROPERTY_FILE = "application.properties";
	private              Stage  stage;
	private static final String S3_BUCKET_URL = "https://s3-ap-southeast-2.amazonaws.com/change-log-generator/";
	private              Path   currentDir;

	{
		currentDir = Paths.get(".")
		                  .toAbsolutePath()
		                  .normalize();
		if ("test".equals(System.getProperty("profile"))) {
			currentDir = currentDir.resolve("Java");
		}
	}

	@FXML
	private Accordion accordion;

	@FXML
	private BasicController basicController;

	@FXML
	private ChangeController changeController;

	private BeanFactory beanFactory;

	@FXML
	private void exit() {
		System.exit(0);
	}

	@FXML
	private void checkForUpdates() {
		try {
			Optional<String> newVersion = getNewVersion();
			if (newVersion.isPresent()) {
				AlertUtil.showConfirmation(
						String.format("Update available: %s", newVersion.get()),
						"Do you want to update the app?",
						this::update
				);
			} else {
				AlertUtil.showInformation(APP_NAME, "No updates available");
			}
		} catch (Exception e) {
			AlertUtil.showError(e);
		}
	}

	private Optional<String> getNewVersion() throws IOException, UnirestException {
		String latestVersion  = HttpUtil.get(S3_BUCKET_URL + "version");
		String currentVersion = getVersion();

		if (!latestVersion.equals(currentVersion)) {
			return Optional.of(latestVersion);
		}
		return Optional.empty();
	}

	private void update() {
		final ProgressBarAlert progressBarAlert = AlertUtil.showProgressAlert("Updating...", APP_NAME);
		progressBarAlert.setCloseListener(this::exit);
		Consumer<Exception> showError = (exception) -> {
			Platform.runLater(() -> {
				progressBarAlert.setResult(ButtonType.CLOSE);
				progressBarAlert.hide();
				AlertUtil.showError(exception);
			});
		};

		try {
			Path   tempDir        = Files.createTempDirectory("tmp");
			String appFile        = "app.zip";
			Path   downloadedFile = tempDir.resolve(appFile);

			HttpUtil.save(S3_BUCKET_URL + appFile, downloadedFile, () -> {
				try {
					Path unzippedFile = ZipUtil.unzip(downloadedFile);
					updateConfigFile(Paths.get(unzippedFile.toString(), "ChangeLogGenerator.cfg"));
					if (OsCheck.getOperatingSystemType() == OsCheck.OSType.Windows) {
						Platform.runLater(() -> {
							try {
								progressBarAlert.setHeaderText("Update downloaded.");
								progressBarAlert.setDoneText(
										"Update libraries have been downloaded. Please close the application and move the patch to the app folder");
								Desktop.getDesktop()
								       .open(unzippedFile.toFile());
							} catch (IOException e) {
								showError.accept(e);
							}
						});
					} else {
						directUpdate(unzippedFile);
						Platform.runLater(() -> {
							progressBarAlert.setHeaderText("Update completes.");
							progressBarAlert.setDoneText("Please restart the app.");
						});
					}
				} catch (IOException e) {
					showError.accept(e);
				}
			});
		} catch (Exception e) {
			showError.accept(e);
		}
	}

	private void directUpdate(Path unzippedFile) throws IOException {
		Path backupDir = null;
		try {
			backupDir = Files.createTempDirectory("bak")
			                 .resolve("app");
			backupDir.toFile()
			         .deleteOnExit();
			Files.move(currentDir, backupDir, StandardCopyOption.REPLACE_EXISTING);
			Files.move(unzippedFile, currentDir, StandardCopyOption.REPLACE_EXISTING);
		} catch (Exception updateException) {
			if (backupDir != null) {
				FileUtils.emptyDirectory(currentDir);
				Files.move(backupDir, currentDir, StandardCopyOption.REPLACE_EXISTING);
			}
			throw updateException;
		}
	}

	private void updateConfigFile(Path path) {
		try {
			String content = Files.lines(path)
			                      .collect(Collectors.joining(System.lineSeparator()));
			String appRuntime;
			if (OsCheck.getOperatingSystemType() == OsCheck.OSType.MacOS) {
				appRuntime = "$APPDIR/PlugIns/Java.runtime";
			} else {
				appRuntime = "$APPDIR/runtime";
			}
			String newContent = content.replace("{app.runtime}", appRuntime);
			Files.write(path, newContent.getBytes());
		} catch (IOException e) {
			AlertUtil.showError(e);
		}
	}

	@FXML
	private void checkVersion() throws IOException {
		String version = getVersion();
		AlertUtil.showInformation(APP_NAME, "Current Version: " + version);
	}

	public void init() throws IOException {
		accordion.setExpandedPane(accordion
				                          .getPanes()
				                          .get(0));
		basicController.initialise(stage);
		changeController.initialise(stage);
		Path versionFile = Paths.get(".")
		                        .resolve("version");
		if (!versionFile.toFile()
		                .exists()) {
			try {
				createVersionFile(versionFile);
			} catch (Exception e) {
				AlertUtil.showError(e);
			}
		}

		if (InternetCheck.hasInternet()) {
			try {
				Optional<String> newVersion = getNewVersion();
				Platform.runLater(() -> {
					newVersion.ifPresent(s -> AlertUtil.showConfirmation(
							String.format("Update available: %s", s),
							"Do you want to update the app?",
							this::update
					));
				});
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private void createVersionFile(Path versionFile) throws IOException {
		Properties properties = PropertiesUtil.loadProperties(PROPERTY_FILE);
		String     version    = normaliseVersion(properties.getProperty("version"));
		Files.write(versionFile, version.getBytes("utf-8"));
	}

	public void setBeanFactory(BeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}

	@FXML
	public void previewClicked() {
		boolean validate = validate();
		if (!validate) {
			return;
		}

		Dialog<Void> dialog = new Dialog<>();
		dialog.setTitle("Preview");
		dialog.setHeaderText(null);

		PreviewPane previewPane = new PreviewPane(beanFactory.getChangeWriter());
		previewPane.setGenerateChangeConfiguration(getGenerateChangeConfiguration());
		dialog.getDialogPane()
		      .setContent(previewPane);
		dialog.getDialogPane()
		      .getButtonTypes()
		      .add(ButtonType.OK);
		dialog.getDialogPane()
		      .getButtonTypes()
		      .add(ButtonType.CANCEL);
		dialog.setResultConverter(param -> {
			if (param == ButtonType.OK) {
				writeFiles();
			}
			return null;
		});

		dialog.showAndWait();
	}

	private GenerateChangeConfiguration getGenerateChangeConfiguration() {
		BasicInformation        basicInformation      = basicController.getBasicInformation();
		List<ChangeInformation> changeInformationList = changeController.getChangeList();

		List<ChangeConfiguration> changeConfigurations
				= changeInformationList.stream()
				                       .map(changeInformation -> ChangeConfiguration
						                       .builder()
						                       .modificationType(changeInformation.getModificationType())
						                       .table(changeInformation.getTable())
						                       .type(changeInformation.getType())
						                       .name(changeInformation.getColumn())
						                       .nullable(changeInformation.getNullable()
						                                                  .getValue())
						                       .unique(changeInformation.getUnique()
						                                                .getValue())
						                       .uniqueConstraintName(changeInformation.getConstraintName())
						                       .defaultValue(
								                       changeInformation.isQuoted() ? getQuoted(
										                       changeInformation.getDefaultValue()) :
										                       changeInformation.getDefaultValue()
						                       )
						                       .where(changeInformation.getWhere())
						                       .value(
								                       changeInformation.isQuoted() ? getQuoted(
										                       changeInformation.getValue()) :
										                       changeInformation.getValue()
						                       )
						                       .newColumn(changeInformation.getNewColumn())
						                       .sql(changeInformation.getSql())
						                       .afterColumn(changeInformation.getAfterColumn())
						                       .build())
				                       .collect(
						                       Collectors.toList());

		GenerateChangeConfiguration generateChangeConfiguration
				= GenerateChangeConfiguration
				.builder()
				.authorName(
						basicInformation.getAuthor())
				.schema(basicInformation.getSchema())
				.jiraNumber(
						basicInformation.getJira())
				.outputFileName(
						basicInformation.getOutputFileName())
				.baseProjectDir(
						basicInformation.getProjectDir())
				.changeConfigurationList(
						changeConfigurations)
				.build();

		generateChangeConfiguration.afterPropertiesSet();
		return generateChangeConfiguration;
	}

	@FXML
	public void save() {
		boolean validate = validate();
		if (!validate) {
			return;
		}
		writeFiles();
	}

	private void writeFiles() {
		try {
			ChangeWriter                changeWriter                = beanFactory.getChangeWriter();
			GenerateChangeConfiguration generateChangeConfiguration = getGenerateChangeConfiguration();
			changeWriter.writeSingleTenantChange(generateChangeConfiguration);
			changeWriter.writeMultitenantChange(generateChangeConfiguration);
			AlertUtil.showAlert(Alert.AlertType.CONFIRMATION, "Successfully created db update");
		} catch (Exception e) {
			e.printStackTrace();
			AlertUtil.showError(e);
		}
	}

	private String getQuoted(String value) {
		return String.format("'%s'", value);
	}

	private boolean validate() {
		boolean error = false;
		if (!changeController.validate()) {
			expandPane(1);
			error = true;
		}
		if (!basicController.validate()) {
			expandPane(0);
			error = true;
		}
		return !error;
	}

	private void expandPane(int index) {
		accordion.setExpandedPane(accordion
				                          .getPanes()
				                          .get(index));
	}

	public void setStage(Stage stage) {
		this.stage = stage;
	}

	private String normaliseVersion(String version) {
		return version.replace("-SNAPSHOT", "");
	}

	private String getVersion() throws IOException {
		Path versionFile = currentDir.resolve("version");
		return new String(Files.readAllBytes(versionFile), "utf-8");
	}
}

