package eu.trentorise.smartcampus.puschservice.util;

import java.util.Map;

public final class PushServiceCostant {
	
	//Fixed
	private static final Object SENDER_ID_KEY = "GCM_SENDER_ID";
	private static final Object APP_ID_KEY = "APP_ID";
		
	//Autowired	
	public static String SENDER_ID="499940284623";
	public static String APP_NAME="clientname";
	
	
	//Setted in configuration by developer
	 static  String USER_AUTH_TOKEN = "f7624300-847b-4dd9-8a8a-c2772877a52d";
	 static  String CLIENT_AUTH_TOKEN = "4a6614cd-d4b5-473b-9c1c-f953c75cd0f2";
	 static String SERVER_URL="";
	
	

	public static String getUSER_AUTH_TOKEN() {
		return USER_AUTH_TOKEN;
	}



	public static void setUSER_AUTH_TOKEN(String uSER_AUTH_TOKEN) {
		USER_AUTH_TOKEN = uSER_AUTH_TOKEN;
	}



	public static String getCLIENT_AUTH_TOKEN() {
		return CLIENT_AUTH_TOKEN;
	}



	public static void setCLIENT_AUTH_TOKEN(String cLIENT_AUTH_TOKEN) {
		CLIENT_AUTH_TOKEN = cLIENT_AUTH_TOKEN;
	}



	public static String getSERVER_URL() {
		return SERVER_URL;
	}



	public static void setSERVER_URL(String sERVER_URL) {
		SERVER_URL = sERVER_URL;
	}



	public static void setConfigurationMap(Map<String, Object> initMap) {
		PushServiceCostant.SENDER_ID=String.valueOf(initMap.get(PushServiceCostant.SENDER_ID_KEY));
		PushServiceCostant.APP_NAME=String.valueOf(initMap.get(PushServiceCostant.APP_ID_KEY));
		
	}
	


}
