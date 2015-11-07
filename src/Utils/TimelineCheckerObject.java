package Utils;

import Utils.Action.Actions;

public class TimelineCheckerObject {

	private String textToCheck;
	private String userTimeLineID;
	private String name;
	private String idToNotify;
	private int hoursLimit;
	private boolean notifyUser;
	private boolean specificUser;
	private Actions[] actions;

	public TimelineCheckerObject( String name, boolean specificUser, String userTimeLineID, String textToCheck, int hoursLimit, boolean notifyUser, Actions[] actions ) {
		this.name = name;
		this.userTimeLineID = userTimeLineID;
		this.textToCheck = textToCheck;

		this.hoursLimit = hoursLimit;

		this.notifyUser = notifyUser;
		this.specificUser = specificUser;

		this.actions = actions;
	}

	public TimelineCheckerObject( String name, boolean specificUser, String userTimeLineID, String textToCheck, int hoursLimit, boolean notifyUser, String idToNotify, Actions[] actions ) {
		this(name, specificUser, userTimeLineID, textToCheck, hoursLimit, notifyUser, actions);
		this.idToNotify = idToNotify;
	}

	public String getName() {
		return name;
	}

	public String getTextToCheck() {
		return textToCheck;
	}

	public String getUserTimeLineID() {
		return userTimeLineID;
	}

	public int getHoursLimit() {
		return hoursLimit;
	}

	public boolean isNotifyUser() {
		return notifyUser;
	}

	public Actions[] getActions() {
		return actions;
	}

	public String getIdToNotify() {
		return idToNotify;
	}

	public boolean isSpecificUser() {
		return specificUser;
	}
}
