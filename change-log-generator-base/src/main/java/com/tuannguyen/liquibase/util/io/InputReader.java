package com.tuannguyen.liquibase.util.io;

import com.tuannguyen.liquibase.util.ObjectUtils;
import lombok.extern.log4j.Log4j;

import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

@Log4j
public class InputReader {
	private Scanner scanner;

	public InputReader(Scanner scanner) {
		this.scanner = scanner;
	}

	public String readString(String prompt, String defaultValue) {
		return readString(prompt, defaultValue, null);
	}

	public String readString(String prompt, String defaultValue, String helpText) {
		log.info(getStringValue(prompt, defaultValue, helpText));
		String value = scanner.nextLine().trim();
		return value.isEmpty() ? defaultValue : value;
	}

	public int readInt(String prompt) {
		log.info(prompt);
		return scanner.nextInt();
	}

	public String readChoice(String prompt, List<String> possibleOptions) {
		String option;
		do {
			String possibleValues = possibleOptions.stream().collect(Collectors.joining("|", "(", ")"));
			log.info(String.format("%s %s: ", prompt, possibleValues));
			option = scanner.nextLine();
		} while (!possibleOptions.contains(option.toLowerCase()));
		return option;
	}

	private String getStringValue(String prompt, String defaultValue, String helpText) {
		return String.format("%s%s%s: ",
				prompt,
				ObjectUtils.substituteIfNotNull(defaultValue, String.format(" (%s)", defaultValue)),
				ObjectUtils.substituteIfNotNull(helpText, String.format(" (%s)", helpText)));
	}
}
