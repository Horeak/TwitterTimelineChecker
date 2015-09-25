package Main;

import Console.ProgramWindow;
import Utils.Status.MessageFormatter;
import Utils.Status.StatusHandler;
import Utils.TimelineCheckerObject;
import twitter4j.*;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;

import javax.swing.*;
import java.util.ArrayList;

public class MainTwitter {

	public static int sleepDelay = 15;
	public static int logHours = 24;

	public static final ArrayList<TimelineCheckerObject> timelineCheckers = new ArrayList<TimelineCheckerObject>();
	public static Twitter twitter;

	public static void main(String[] args) throws Exception {
		try{
			start();
		}catch (Exception e){
			if(e instanceof TwitterException){
				TwitterException exception = (TwitterException)e;

				if(exception.exceededRateLimitation()){
					System.out.println("Program exceeded twitter rate limit, program is forced to sleep for 15min to regain access to twitter.");
				}

			}else {
				e.printStackTrace();
			}
		}
	}

	//TODO Remove timed check and maybe replace it with a manual check button.
	//TODO Add more options to make it a more general app? (onFollow, onDirectMessage, onFavorite....?)
	public static void start() throws Exception {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception ex) {
		}

		ProgramWindow.createAndShowGui();
		TwitterAccess.getAccess();


		ConfigurationBuilder cb = new ConfigurationBuilder();
		cb.setDebugEnabled(true)
				.setOAuthConsumerKey("AO0FNLxpCLdwqQhavavEts9IB")
				.setOAuthConsumerSecret("RSswQRqpxxbQpV54YIG90Z6FYrqF7XLMjs2vv73UDNJ2Awn0DD")
				.setOAuthAccessToken(null)
				.setOAuthAccessTokenSecret(null);


		Configuration config = cb.build();

		TwitterFactory tf = new TwitterFactory(config);
		twitter = tf.getInstance();

		twitter.setOAuthAccessToken(TwitterAccess.loadAccessToken());
		System.out.println("Account loaded: @" + twitter.getScreenName());

		TwitterStream twitterStream = new TwitterStreamFactory(config).getInstance();
		twitterStream.setOAuthAccessToken(TwitterAccess.loadAccessToken());


		UserStreamListener listener = new UserStreamListener() {
			@Override
			public void onStatus(Status status) {
				try {
					for (TimelineCheckerObject timelineChecker : timelineCheckers) {
						if(status.getUser().getScreenName().equalsIgnoreCase(timelineChecker.getUserTimeLineID())) {
							if (StatusHandler.validStatus(status, timelineChecker)) {
								StatusHandler.performActions(timelineChecker, status, twitter);

								if (timelineChecker.isNotifyUser())
									twitter.sendDirectMessage(timelineChecker.getIdToNotify(), MessageFormatter.notifyStringFormat(timelineChecker, status));

							}
						}
					}
				}catch (Exception e){
					e.printStackTrace();
				}
			}

			@Override
			public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice) {
			}

			@Override
			public void onDeletionNotice(long directMessageId, long userId) {
			}

			@Override
			public void onTrackLimitationNotice(int numberOfLimitedStatuses) {
			}

			@Override
			public void onScrubGeo(long userId, long upToStatusId) {
			}

			@Override
			public void onStallWarning(StallWarning warning) {
			}

			@Override
			public void onFriendList(long[] friendIds) {
			}

			@Override
			public void onFavorite(User source, User target, Status favoritedStatus) {
			}

			@Override
			public void onUnfavorite(User source, User target, Status unfavoritedStatus) {
			}

			@Override
			public void onFollow(User source, User followedUser) {
			}

			@Override
			public void onUnfollow(User source, User followedUser) {
			}

			@Override
			public void onDirectMessage(DirectMessage directMessage) {
				System.out.println("direct message received: \nFrom: " + directMessage.getSenderScreenName() + "\n" +  directMessage.getText());
			}

			@Override
			public void onUserListMemberAddition(User addedMember, User listOwner, UserList list) {
			}

			@Override
			public void onUserListMemberDeletion(User deletedMember, User listOwner, UserList list) {
			}

			@Override
			public void onUserListSubscription(User subscriber, User listOwner, UserList list) {
			}

			@Override
			public void onUserListUnsubscription(User subscriber, User listOwner, UserList list) {
			}

			@Override
			public void onUserListCreation(User listOwner, UserList list) {
			}

			@Override
			public void onUserListUpdate(User listOwner, UserList list) {
			}

			@Override
			public void onUserListDeletion(User listOwner, UserList list) {
			}

			@Override
			public void onUserProfileUpdate(User updatedUser) {
			}

			@Override
			public void onUserDeletion(long deletedUser) {
			}

			@Override
			public void onUserSuspension(long suspendedUser) {
			}

			@Override
			public void onBlock(User source, User blockedUser) {
			}

			@Override
			public void onUnblock(User source, User unblockedUser) {
			}

			@Override
			public void onRetweetedRetweet(User source, User target, Status retweetedStatus) {
			}

			@Override
			public void onFavoritedRetweet(User source, User target, Status favoritedRetweet) {
			}

			@Override
			public void onQuotedTweet(User source, User target, Status quotingTweet) {
			}

			@Override
			public void onException(Exception ex) {
				ex.printStackTrace();
			}
		};

		twitterStream.addListener(listener);
		twitterStream.user();

	}

}
