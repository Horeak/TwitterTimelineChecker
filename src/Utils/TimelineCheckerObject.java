package Utils;

import Main.MainTwitter;
import Utils.Action.Actions;
import Utils.Status.MessageFormatter;
import Utils.Status.StatusHandler;
import twitter4j.Status;

public class TimelineCheckerObject {

	public TimelineCheckerObject(String name, String userTimeLineID, String textToCheck, int hoursLimit, boolean notifyUser, Actions[] actions)
	{
		this.name = name;
		this.userTimeLineID = userTimeLineID;
		this.textToCheck = textToCheck;

		this.hoursLimit = hoursLimit;

		this.notifyUser = notifyUser;

		this.actions = actions;
	}

	public TimelineCheckerObject(String name, String userTimeLineID, String textToCheck, int hoursLimit, boolean notifyUser, String idToNotify, Actions[] actions)
	{
		this(name, userTimeLineID, textToCheck, hoursLimit, notifyUser, actions);
		this.idToNotify = idToNotify;

		try {
			for (Status stat : StatusHandler.getTimelineFromUserAndDate(this, MainTwitter.twitter)) {
				if (StatusHandler.validStatus(stat, this)) {

					StatusHandler.performActions(this, stat, MainTwitter.twitter);

					if (isNotifyUser())
						MainTwitter.twitter.sendDirectMessage(getIdToNotify(), MessageFormatter.notifyStringFormat(this, stat));

				}
			}
		}catch (Exception e){
			e.printStackTrace();
		}

	}

	private String textToCheck;
	private String userTimeLineID;
	private String name;
	private String idToNotify;

	private int hoursLimit;

	private boolean notifyUser;

	private Actions[] actions;

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

}
