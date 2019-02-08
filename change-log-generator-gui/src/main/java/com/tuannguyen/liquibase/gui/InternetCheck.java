package com.tuannguyen.liquibase.gui;

import java.net.Socket;

public class InternetCheck
{
	private static String GOOGLE = "www.google.com";

	public static boolean hasInternet()
	{
		try (Socket socket = new Socket(GOOGLE, 80)) {
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return false;
	}
}