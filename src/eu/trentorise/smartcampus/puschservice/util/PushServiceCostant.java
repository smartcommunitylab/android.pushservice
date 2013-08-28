package eu.trentorise.smartcampus.puschservice.util;

import java.util.Map;

public final class PushServiceCostant {

	// Fixed
	private static final Object SENDER_ID_KEY = "GCM_SENDER_ID";
	private static final Object APP_ID_KEY = "APP_ID";

	// Autowired
	public static String SENDER_ID = "";
	public static String APP_NAME = "";

	// Setted in configuration by developer

	public static String SERVER_URL = "";

	public static String getSERVER_URL() {
		return SERVER_URL;
	}

	public static void setSERVER_URL(String sERVER_URL) {
		SERVER_URL = sERVER_URL;
	}

	public static void setConfigurationMap(Map<String, Object> initMap) {
		PushServiceCostant.SENDER_ID = String.valueOf(initMap
				.get(PushServiceCostant.SENDER_ID_KEY));
		System.out.println(PushServiceCostant.SENDER_ID);

	}

}
