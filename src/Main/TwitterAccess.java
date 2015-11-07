package Main;

import File.PasswordBasedEncryption.SecureEncryption;
import Utils.Misc.SpringUtilities;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import twitter4j.conf.ConfigurationBuilder;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.URI;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class TwitterAccess {
	private static File file = new File("tokens.store");
	private static DateFormat dateFormat = new SimpleDateFormat("HH.mm.ss dd:MM:yyyy");

	public static void getAccess() throws IOException, TwitterException {

		if (!hasToken()) {
			ConfigurationBuilder cb = new ConfigurationBuilder();
			cb.setDebugEnabled(true).setOAuthConsumerKey("AO0FNLxpCLdwqQhavavEts9IB").setOAuthConsumerSecret("RSswQRqpxxbQpV54YIG90Z6FYrqF7XLMjs2vv73UDNJ2Awn0DD").setOAuthAccessToken(null).setOAuthAccessTokenSecret(null);

			TwitterFactory tf = new TwitterFactory(cb.build());
			Twitter twitter = tf.getInstance();

			AccessToken accessToken = null;
			RequestToken requestToken = twitter.getOAuthRequestToken();

			while (null == accessToken) {

				Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
				if (desktop != null && desktop.isSupported(Desktop.Action.BROWSE)) {
					try {
						desktop.browse(new URI(requestToken.getAuthorizationURL()));
					} catch (Exception e) {
						e.printStackTrace();
					}
				}

				String pin = openAutorizeWindow();

				try {
					if (pin.length() > 0) {
						accessToken = twitter.getOAuthAccessToken(requestToken, pin);
					} else {
						accessToken = twitter.getOAuthAccessToken();
					}
				} catch (TwitterException te) {
					if (401 == te.getStatusCode()) {
						System.out.println("Unable to get the access token.");
					} else {
						te.printStackTrace();
					}
				}
			}

			System.out.println("Access was granted to account: " + accessToken.getScreenName());
			writeAccessToken(accessToken);
		}
	}

	private static String openAutorizeWindow() {
		JPanel panel = new JPanel();
		panel.setLayout(new SpringLayout());


		JTextField pinInput = new JTextField();
		pinInput.setPreferredSize(new Dimension(120, 20));

		JLabel text = new JLabel("Pin: ", JLabel.TRAILING);
		panel.add(text);
		text.setLabelFor(pinInput);
		panel.add(pinInput);

		SpringUtilities.makeCompactGrid(panel, 1, 2, 6, 6, 6, 6);

		int result = JOptionPane.showConfirmDialog(null, panel, "Authorize twitter access", JOptionPane.CLOSED_OPTION);

		if (result == JOptionPane.OK_OPTION) {
			return pinInput.getText();
		}

		return null;
	}

	public static AccessToken loadAccessToken() {
		String line, AT = null, AS = null;

		try {
			if (hasToken()) {
				FileReader fileReader = new FileReader(file);
				BufferedReader bufferedReader = new BufferedReader(fileReader);

				while ((line = bufferedReader.readLine()) != null) {
					String[] xx = line.split("\\-");

					boolean checkAT = false, checkAS = false;
					for (String x : xx) {
						String tmp = SecureEncryption.staticReference.DecryptObject(x);

						if (tmp.equalsIgnoreCase("AT") && !checkAT) {
							checkAT = true;
							continue;
						} else if (checkAT) {
							AT = SecureEncryption.staticReference.DecryptObject(x);
							checkAT = false;
						}


						if (tmp.equalsIgnoreCase("AS") && !checkAS) {
							checkAS = true;
							continue;
						} else if (checkAS) {
							AS = SecureEncryption.staticReference.DecryptObject(x);
							checkAS = false;
						}
					}

				}

				bufferedReader.close();

				if (AS != null && AT != null) return new AccessToken(AT, AS);

			} else {
				file.createNewFile();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	public static boolean isFileEmpty() {
		try {
			FileReader fileReader = new FileReader(file);
			BufferedReader bufferedReader = new BufferedReader(fileReader);

			String line = null;

			while ((line = bufferedReader.readLine()) != null) {
				if (line != null && !line.isEmpty()) {
					return false;
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}


		return true;

	}

	public static boolean hasFile() {

		try {
			if (file.exists() && !isFileEmpty()) {
				FileReader fileReader = new FileReader(file);
				BufferedReader bufferedReader = new BufferedReader(fileReader);

				String line = null;

				while ((line = bufferedReader.readLine()) != null) {
					String[] tgg = line.split("\\-");

					for (String x : tgg) {
						String tmp = SecureEncryption.staticReference.DecryptObject(x);

						if (tmp.equalsIgnoreCase("DD")) {
							return true;
						}
					}
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return false;
	}

	public static void validateTokenStore() {
		if (!hasToken() && hasFile()) {
			invalidateTokenStore();
		}
	}

	public static void invalidateTokenStore() {
		System.out.println("Invalidating token store!");

		if (file.exists()) {
			try {

				PrintWriter writer = new PrintWriter(file);
				writer.print("");
				writer.close();

			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static boolean hasToken() {
		String line = null;
		boolean tg = false;

		if (!file.exists()) try {
			file.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}

		try {
			FileReader fileReader = new FileReader(file);
			BufferedReader bufferedReader = new BufferedReader(fileReader);

			while ((line = bufferedReader.readLine()) != null) {

				String[] tgg = line.split("\\-");
				boolean check = false;

				for (String x : tgg) {
					String tmp = SecureEncryption.staticReference.DecryptObject(x);

					if (tmp.equalsIgnoreCase("DD") && !check) {
						check = true;
					} else if (check) {
						Date created = dateFormat.parse(SecureEncryption.staticReference.DecryptObject(x));
						Date now = new Date();

						long diff = (now.getTime() - created.getTime());
						long hours = TimeUnit.MILLISECONDS.toHours(diff);
						long days = TimeUnit.MILLISECONDS.toDays(diff);

						tg = hours <= 24 && days <= 1;
					}
				}


			}

			bufferedReader.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return tg;
	}

	private static void writeAccessToken( AccessToken token ) {
		try {
			file.createNewFile();
			String tmp = getEncryptionRow();

			FileWriter fileWriter = new FileWriter(file);
			BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
			Date date = new Date();

			for (String t : tmp.split("")) {
				if (t.equalsIgnoreCase("1")) {
					bufferedWriter.write(SecureEncryption.staticReference.EncryptObject("DD") + "-" + SecureEncryption.staticReference.EncryptObject(dateFormat.format(date)));
					bufferedWriter.newLine();

				} else if (t.equalsIgnoreCase("2")) {
					bufferedWriter.write(SecureEncryption.staticReference.EncryptObject("AT") + "-" + SecureEncryption.staticReference.EncryptObject(token.getToken()));
					bufferedWriter.newLine();

				} else if (t.equalsIgnoreCase("3")) {
					bufferedWriter.write(SecureEncryption.staticReference.EncryptObject("AS") + "-" + SecureEncryption.staticReference.EncryptObject(token.getTokenSecret()));
					bufferedWriter.newLine();
				}
			}

			bufferedWriter.close();

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private static String getEncryptionRow() {
		String temp = "";

		ArrayList<Integer> ints = new ArrayList<>();

		for (int i = 1; i < 4; i++)
			ints.add(i);

		Collections.shuffle(ints);

		for (Integer in : ints)
			temp += Integer.toString(in);

		return temp;
	}

}
