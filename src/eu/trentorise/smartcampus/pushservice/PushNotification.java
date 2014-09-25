package eu.trentorise.smartcampus.pushservice;


public class PushNotification {
	private String mId;
	private String mTitle;
	private String mDescription;

	public PushNotification(String mId, String mTitle, String mDescription) {
		super();
		this.mId = mId;
		this.mTitle = mTitle;
		this.mDescription = mDescription;
	}

	public String getId() {
		return mId;
	}

	public void setId(String id) {
		mId = id;
	}

	public String getTitle() {
		return mTitle;
	}

	public void setTitle(String title) {
		mTitle = title;
	}

	public String getDescription() {
		return mDescription;
	}

}
