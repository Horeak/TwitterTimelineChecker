package Utils.Status;

import Utils.Action.Actions;
import Utils.TimelineCheckerObject;
import twitter4j.Status;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MessageFormatter {

	public static String notifyStringFormat( TimelineCheckerObject checkerObject, Status status ) {
		StringBuilder builder = new StringBuilder();
		builder.append("\n");
		builder.append(status.getUser().getScreenName() + "\n-----------------\n");
		builder.append(status.getText() + "\n");
		builder.append("\n- " + new Date().toString() + ": ");

		builder.append((StatusHandler.hasFavorite(checkerObject, status) ? Actions.FAVORITE.name() : "") + (StatusHandler.hasFavorite(checkerObject, status) && StatusHandler.hasRetweet(checkerObject, status) ? ", " : "") + (StatusHandler.hasRetweet(checkerObject, status) ? Actions.RETWEET.name() : ""));

		return builder.toString();
	}

	public static String formatOutgoingLogMessage( String text ) {
		DateFormat dateFormat = new SimpleDateFormat("[HH:mm:ss]");

		return dateFormat.format(new Date()) + text;
	}
}
