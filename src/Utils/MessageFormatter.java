package Utils;

import Utils.Action.Actions;
import Utils.Status.StatusHandler;
import twitter4j.Status;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MessageFormatter {

	public static String notifyStringFormat(TimelineCheckerObject checkerObject, Status status) {
		StringBuilder builder = new StringBuilder();
		builder.append("#####\n");
		builder.append(status.getUser().getScreenName() + "\n-----------------\n");
		builder.append(status.getText() + "\n");
		builder.append("\n- " + new Date().toString());

		boolean t = false;
		for(Actions act : StatusHandler.getActions(checkerObject, status)){
			if(act != null) {
				builder.append(act.name() + (t ? ", " : ""));
				t = true;
			}
		}

		return builder.toString();
	}

	public static String formatOutgoingLogMessage(String text){
		DateFormat dateFormat = new SimpleDateFormat("[HH:mm:ss]");


		return dateFormat.format(new Date()) + text;
	}
}
