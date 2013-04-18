package eu.trentorise.smartcampus.puschservice.util;

public final class PushServiceCostant {
	
	public static String SERVER_URL="";
	public static String SENDER_ID="";
	public static String APP_NAME="generic";
	public static String mToken;
	
	



	public PushServiceCostant(String senderid,String serverurl,String AppName,String mToken){
		PushServiceCostant.SENDER_ID=senderid;
		PushServiceCostant.SERVER_URL=serverurl;
		PushServiceCostant.APP_NAME=AppName;
		PushServiceCostant.mToken=mToken;
		
	}
	


}
