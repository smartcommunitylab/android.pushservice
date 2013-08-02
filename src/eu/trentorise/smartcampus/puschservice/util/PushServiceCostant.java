package eu.trentorise.smartcampus.puschservice.util;

import java.util.Map;

public final class PushServiceCostant {
	
	//Fixed
	private static final Object SENDER_ID_KEY = "GCM_SENDER_ID";
	private static final Object APP_ID_KEY = "APP_ID";
		
	//Autowired	
	public static String SENDER_ID="";
	public static String APP_NAME="generic";
	
	
	//Setted in configuration by developer
	public static final String USER_AUTH_TOKEN = "ef7ba89b-6be1-4f63-bdc5-a8d798e9452e";
	public static final String CLIENT_AUTH_TOKEN = "1061811b-b60b-4407-9e84-7be2917baf9d";
	public static String SERVER_URL="";
	
	

	public static void setConfigurationMap(Map<String, Object> initMap) {
		PushServiceCostant.SENDER_ID=String.valueOf(initMap.get(PushServiceCostant.SENDER_ID_KEY));
		PushServiceCostant.APP_NAME=String.valueOf(initMap.get(PushServiceCostant.APP_ID_KEY));
		
	}
	


}
