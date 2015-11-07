package Main;

import Console.ProgramWindow;
import Utils.Action.ActionUtils;
import Utils.Status.MessageFormatter;
import Utils.Status.StatusHandler;
import Utils.TimelineCheckerObject;
import twitter4j.*;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;

import javax.swing.*;
import java.util.ArrayList;

public class MainTwitter {

	public static final ArrayList<TimelineCheckerObject> timelineCheckers = new ArrayList<TimelineCheckerObject>();
	public static Twitter twitter;

	public static void main( String[] args ) throws Exception {
		try {
			start();
		} catch (Exception e) {
			if (e instanceof TwitterException) {
				TwitterException exception = (TwitterException) e;

				if (exception.exceededRateLimitation()) {
					System.out.println("Program exceeded twitter rate limit.");
				}

			} else {
				e.printStackTrace();
			}
		}
	}

	//TODO Add more options to make it a more general app? (onFollow, onDirectMessage, onFavorite....?)
	public static void start() throws Exception {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception ex) {
		}

		ProgramWindow.createAndShowGui();
		TwitterAccess.getAccess();


		ConfigurationBuilder cb = new ConfigurationBuilder();
		cb.setDebugEnabled(true).setOAuthConsumerKey("AO0FNLxpCLdwqQhavavEts9IB").setOAuthConsumerSecret("RSswQRqpxxbQpV54YIG90Z6FYrqF7XLMjs2vv73UDNJ2Awn0DD").setOAuthAccessToken(null).setOAuthAccessTokenSecret(null);


		Configuration config = cb.build();

		TwitterFactory tf = new TwitterFactory(config);
		twitter = tf.getInstance();

		twitter.setOAuthAccessToken(TwitterAccess.loadAccessToken());
		System.out.println("Account loaded: @" + twitter.getScreenName());

		TwitterStream twitterStream = new TwitterStreamFactory(config).getInstance();
		twitterStream.setOAuthAccessToken(TwitterAccess.loadAccessToken());


		UserStreamListener listener = new UserStreamListener() {
			@Override
			public void onStatus( Status status ) {
				update();

				try {
					for (TimelineCheckerObject timelineChecker : timelineCheckers) {
						if (StatusHandler.validStatus(status, timelineChecker)) {
							if (timelineChecker.isNotifyUser())
								twitter.sendDirectMessage(timelineChecker.getIdToNotify(), MessageFormatter.notifyStringFormat(timelineChecker, status));

							ActionUtils.performActions(timelineChecker, status, twitter);

						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			@Override
			public void onDeletionNotice( StatusDeletionNotice statusDeletionNotice ) {
				update();
			}

			@Override
			public void onDeletionNotice( long directMessageId, long userId ) {
				update();
			}

			@Override
			public void onTrackLimitationNotice( int numberOfLimitedStatuses ) {
				update();
			}

			@Override
			public void onScrubGeo( long userId, long upToStatusId ) {
				update();
			}

			@Override
			public void onStallWarning( StallWarning warning ) {
				update();
			}

			@Override
			public void onFriendList( long[] friendIds ) {
				update();
			}

			@Override
			public void onFavorite( User source, User target, Status favoritedStatus ) {
				update();
			}

			@Override
			public void onUnfavorite( User source, User target, Status unfavoritedStatus ) {
				update();
			}

			@Override
			public void onFollow( User source, User followedUser ) {
				update();
			}

			@Override
			public void onUnfollow( User source, User followedUser ) {
				update();
			}

			@Override
			public void onDirectMessage( DirectMessage directMessage ) {
				System.out.println("direct message received: \nFrom: " + directMessage.getSenderScreenName() + "\n" + directMessage.getText());

				update();
			}

			@Override
			public void onUserListMemberAddition( User addedMember, User listOwner, UserList list ) {
				update();
			}

			@Override
			public void onUserListMemberDeletion( User deletedMember, User listOwner, UserList list ) {
				update();
			}

			@Override
			public void onUserListSubscription( User subscriber, User listOwner, UserList list ) {
				update();
			}

			@Override
			public void onUserListUnsubscription( User subscriber, User listOwner, UserList list ) {
				update();
			}

			@Override
			public void onUserListCreation( User listOwner, UserList list ) {
				update();
			}

			@Override
			public void onUserListUpdate( User listOwner, UserList list ) {
				update();
			}

			@Override
			public void onUserListDeletion( User listOwner, UserList list ) {
				update();
			}

			@Override
			public void onUserProfileUpdate( User updatedUser ) {
				update();
			}

			@Override
			public void onUserDeletion( long deletedUser ) {
				update();
			}

			@Override
			public void onUserSuspension( long suspendedUser ) {
				update();
			}

			@Override
			public void onBlock( User source, User blockedUser ) {
				update();
			}

			@Override
			public void onUnblock( User source, User unblockedUser ) {
				update();
			}

			@Override
			public void onRetweetedRetweet( User source, User target, Status retweetedStatus ) {
				update();
			}

			@Override
			public void onFavoritedRetweet( User source, User target, Status favoritedRetweet ) {
				update();
			}

			@Override
			public void onQuotedTweet( User source, User target, Status quotingTweet ) {
				update();
			}

			@Override
			public void onException( Exception ex ) {
				ex.printStackTrace();

				update();
			}
		};

		twitterStream.addListener(listener);
		twitterStream.user();
	}


	public static void update() {
		TwitterAccess.validateTokenStore();
	}

}
