package eu.trentorise.smartcampus.pushservice;

import java.util.Date;

import android.content.ContentValues;

public class PushNotification {
	private Integer mId;
	private String mText;
	private Date mDate;
	private boolean mRead = false;
	
	public PushNotification(String text){
		this.mText = text;
	}
	public PushNotification(int id){
		this.mId=id;
	}
	
	public PushNotification(int id,String text, Date date, boolean read) {
		this.mId=id;
		this.mText = text;
		this.mDate = date;
		this.mRead = read;
	}
	
	/**
	 * @return the mText
	 */
	public String getText() {
		return mText;
	}
	
	//TODO return actual data
	public String getContent() {
		return null;
	}
	public String getTitle() {
		return mText;
	}
	public String getDescription() {
		return mText;
	}
	/**
	 * @param mText the mText to set
	 */
	public void setText(String mText) {
		this.mText = mText;
	}
	/**
	 * @return the mDate
	 */
	public Date getDate() {
		return mDate;
	}
	/**
	 * @param mDate the mDate to set
	 */
	public void setDate(Date mDate) {
		this.mDate = mDate;
	}
	/**
	 * @return the mRead
	 */
	public boolean isRead() {
		return mRead;
	}
	/**
	 * @param mRead the mRead to set
	 */
	public void setRead(boolean mRead) {
		this.mRead = mRead;
	}
	
	public ContentValues toContentValues(){
		ContentValues out = new ContentValues();
		if(mText==null && mId==null)
			throw new RuntimeException("Notification's empy");
		if(mText!=null)
			out.put(NotificationDBHelper.TEXT_KEY, mText);
		out.put(NotificationDBHelper.READ_KEY, mRead?1:0);
		if(mDate!=null)
			out.put(NotificationDBHelper.DATE_KEY, mDate.getTime());
		else
			out.put(NotificationDBHelper.DATE_KEY, new Date().getTime());
		if(mId!=null)
			out.put(NotificationDBHelper.ID_KEY, mId);
		return out;
	}

}
