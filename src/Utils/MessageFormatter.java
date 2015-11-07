package Utils;

import Utils.Action.Actions;
import Utils.Status.StatusHandler;
import twitter4j.Status;

import java.util.Date;

public class MessageFormatter {

	public static String notifyStringFormat( TimelineCheckerObject checkerObject, Status status ) {
		StringBuilder builder = new StringBuilder();
		builder.append("\n");
		builder.append(status.getUser().getScreenName() + "\n-----------------\n");
		builder.append(status.getText() + "\n");
		builder.append("\n- " + new Date().toString());

		boolean t = true;
		for (Actions act : StatusHandler.getActions(checkerObject, status)) {
			if (act != null) {
				builder.append(" " + act.name() + (t ? ", " : ""));
				t = false;
			}
		}

		return builder.toString();
	}

}
