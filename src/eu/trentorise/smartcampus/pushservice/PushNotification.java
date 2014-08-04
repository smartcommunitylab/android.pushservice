package eu.trentorise.smartcampus.pushservice;

import java.util.Date;

import android.content.ContentValues;

public class PushNotification {

	private Integer mId;
	private String mTitle;
	private String mDescription;
	private String mAgencyId;
	private String mRouteId;
	private String mRouteShortName;
	private String mTripId;
	private Integer mDelay;
	private Integer mFromTime;
	private String mStation;
	private Date mDate;
	private boolean mRead = false;

	public PushNotification(String title, String description, String agencyId,
			String routeId, String routeShortName, String tripId,
			Integer delay, Integer fromTime, String station, Date date,
			boolean read) {
		super();
		mTitle = title;
		mDescription = description;
		mAgencyId = agencyId;
		mRouteId = routeId;
		mRouteShortName = routeShortName;
		mTripId = tripId;
		mDelay = delay;
		mFromTime = fromTime;
		mStation = station;
		mDate = date;
		mRead = read;
	}

	public PushNotification(Integer id, String title, String description,
			String agencyId, String routeId, String routeShortName,
			String tripId, Integer delay, Integer fromTime, String station,
			Date date, boolean read) {
		super();
		mId = id;
		mTitle = title;
		mDescription = description;
		mAgencyId = agencyId;
		mRouteId = routeId;
		mRouteShortName = routeShortName;
		mTripId = tripId;
		mDelay = delay;
		mFromTime = fromTime;
		mStation = station;
		mDate = date;
		mRead = read;
	}

	public Integer getId() {
		return mId;
	}

	public void setId(Integer id) {
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

	public void setDescription(String description) {
		mDescription = description;
	}

	public String getAgencyId() {
		return mAgencyId;
	}

	public void setAgencyId(String agencyId) {
		mAgencyId = agencyId;
	}

	public String getRouteId() {
		return mRouteId;
	}

	public void setRouteId(String routeId) {
		mRouteId = routeId;
	}

	public String getRouteShortName() {
		return mRouteShortName;
	}

	public void setRouteShortName(String routeShortName) {
		mRouteShortName = routeShortName;
	}

	public String getTripId() {
		return mTripId;
	}

	public void setTripId(String tripId) {
		mTripId = tripId;
	}

	public Integer getDelay() {
		return mDelay;
	}

	public void setDelay(Integer delay) {
		mDelay = delay;
	}

	public String getStation() {
		return mStation;
	}

	public void setStation(String station) {
		mStation = station;
	}

	public Date getDate() {
		return mDate;
	}

	public void setDate(Date date) {
		mDate = date;
	}

	public boolean isRead() {
		return mRead;
	}

	public void setRead(boolean read) {
		mRead = read;
	}

	public ContentValues toContentValues() {
		ContentValues out = new ContentValues();

		if (mDescription == null && mId == null)
			throw new RuntimeException("Notification's empy");
		if (mDate != null)
			out.put(NotificationDBHelper.DATE_KEY, mDate.getTime());
		else
			out.put(NotificationDBHelper.DATE_KEY, new Date().getTime());
		if (mId != null)
			out.put(NotificationDBHelper.ID_KEY, mId);
		if (mStation != null)
			out.put(NotificationDBHelper.STATION_KEY, mStation);

		out.put(NotificationDBHelper.TITLE_KEY, mTitle);
		out.put(NotificationDBHelper.DESCRIPTION_KEY, mDescription);
		out.put(NotificationDBHelper.READ_KEY, mRead ? 1 : 0);
		out.put(NotificationDBHelper.AGENCYID_KEY, mAgencyId);
		out.put(NotificationDBHelper.DELAY_KEY, mDelay);
		out.put(NotificationDBHelper.ROUTEID_KEY, mRouteId);
		out.put(NotificationDBHelper.ROUTESHORTNAME_KEY, mRouteShortName);
		out.put(NotificationDBHelper.TRIPID_KEY, mTripId);

		return out;
	}

}
