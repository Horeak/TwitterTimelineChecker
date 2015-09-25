package Utils.Status;

import Utils.Action.Actions;
import Utils.TimelineCheckerObject;
import org.apache.commons.lang3.StringUtils;
import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.Twitter;

import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class StatusHandler {

	public static boolean hasFavorite(TimelineCheckerObject checkerObject, Status status){
		for(Actions act : getActions(checkerObject, status)){
			if(act == Actions.FAVORITE)
				return true;
		}

		return false;
	}

	public static boolean hasRetweet(TimelineCheckerObject checkerObject, Status status){
		for(Actions act : getActions(checkerObject, status)){
			if(act == Actions.RETWEET)
				return true;
		}

		return false;
	}


	public static Actions[] getActions(TimelineCheckerObject checkerObject, Status status){

		Actions[] actionses;
		boolean fav = false, re = false;
		for (Actions act : checkerObject.getActions()) {
			if (act != null) {
				if (act.name().equalsIgnoreCase(Actions.FAVORITE.name()) && !status.isFavorited()) {
					fav = true;

				} else if (act.name().equalsIgnoreCase(Actions.RETWEET.name()) && !status.isRetweeted()) {
					re = true;

				}
			}

		}
			actionses = new Actions[]{fav ? Actions.FAVORITE : null, re ? Actions.RETWEET : null};

			return actionses;
	}

	public static void performActions(TimelineCheckerObject timelineCheckerObject, Status status, Twitter twitter) throws  Exception{
		for (Actions act : timelineCheckerObject.getActions()) {
			if (act != null) {
				if (act.name().equalsIgnoreCase(Actions.FAVORITE.name())) {

					if (!status.isFavorited())
						twitter.createFavorite(status.getId());

				} else if (act.name().equalsIgnoreCase(Actions.RETWEET.name())) {

					if (!status.isRetweeted())
						twitter.retweetStatus(status.getId());

				}
			}
		}
	}


	public static ArrayList<Status> getTimelineFromUserAndDate(TimelineCheckerObject object, Twitter twitter) throws Exception{
		ArrayList<Status> list = new ArrayList<>();
		ResponseList<Status> tempList = twitter.getUserTimeline(object.getUserTimeLineID());

		for(Status stat : tempList){
			Date start = stat.getCreatedAt();
			Date now = new Date();

			long diff = (now.getTime() - start.getTime());
			long hours = TimeUnit.MILLISECONDS.toHours(diff);

			boolean valid = false;

			boolean validHours = hours <= object.getHoursLimit();

			if (validHours) {
				valid = true;
			}

			if(valid)
				list.add(stat);
		}

		return list;
	}

	public static boolean validStatus(Status stat, TimelineCheckerObject timelineChecker){
		boolean bool = true;

		if(hasFavorite(timelineChecker, stat) || hasRetweet(timelineChecker, stat)) {
			if (!stat.isRetweet()) {
				boolean fav = false, re = false;

				for (Actions act : timelineChecker.getActions()) {
					if (act == Actions.RETWEET) re = true;
					if (act == Actions.FAVORITE) fav = true;
				}

				if (fav || re) {
					if (fav && !stat.isFavorited() || re && !stat.isRetweeted()) {
						String text = timelineChecker.getTextToCheck();

						if (text.contains("||")) {
							String[] tt = text.split("\\|\\|");
							for (String tg : tt) {

								if (tg.contains("&&")) {
									String[] ttt = tg.split("\\&\\&");

									for (String tgg : ttt) {
										bool = StringUtils.containsIgnoreCase(stat.getText(), tgg);
									}

									if (bool) {
										return true;
									} else {
										continue;
									}

								} else {
									bool = StringUtils.containsIgnoreCase(stat.getText(), tg);
									if (bool) {
										return true;
									}
								}

							}
						} else {
							if (text.contains("&&")) {
								String[] ttt = text.split("\\&\\&");

								for (String tgg : ttt) {
									bool = StringUtils.containsIgnoreCase(stat.getText(), tgg);
								}

								if (bool) {
									return true;
								}

							} else {
								return StringUtils.containsIgnoreCase(stat.getText(), text);
							}
						}
					}

					return bool;
				}
			}
		}

		return false;
	}

}
