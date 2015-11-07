package Console;

import Main.MainTwitter;
import Utils.Action.Actions;
import Utils.Misc.SpringUtilities;
import Utils.Misc.WrapLayout;
import Utils.Status.StatusHandler;
import Utils.TimelineCheckerObject;

import javax.swing.*;
import javax.swing.text.DefaultCaret;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.PrintStream;

public class ProgramWindow extends JPanel {

	public static PrintStream stream = System.out;
	public static JFrame frame = new JFrame("Twitter Timeline Checker");
	private JTextPane textArea = new JTextPane();
	private ConsolePrintStream taOutputStream = new ConsolePrintStream(textArea);

	public ProgramWindow() {
		textArea.setEditable(false);
		textArea.setContentType("text/html");
		textArea.setEditorKit(JEditorPane.createEditorKitForContentType("text/html"));

		Dimension size = new Dimension(300, 200);

		textArea.setPreferredSize(size);
		textArea.setMaximumSize(size);
		textArea.setMinimumSize(size);
		setLayout(new BorderLayout());

		DefaultCaret caret = (DefaultCaret) textArea.getCaret();
		caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);

		add(new JScrollPane(textArea, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED));
		System.setOut(taOutputStream);


	}

	public static void createAndShowGui() {
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setPreferredSize(new Dimension(600, 500));
		frame.add(new ProgramWindow(), BorderLayout.NORTH);
		frame.add(new buttonArea().getScrollPane(), BorderLayout.WEST);

		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
		frame.setResizable(true);
	}

}

class buttonArea {

	public static JPanel panel = new JPanel();
	public static JScrollPane scrollPane = new JScrollPane(panel, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

	public static Dimension buttonSize = new Dimension(170, 40);

	public buttonArea() {
		scrollPane.setPreferredSize(new Dimension(200, 220));
		panel.setLayout(new WrapLayout());

		addButtons();
	}

	public JScrollPane getScrollPane() {
		return scrollPane;
	}

	public void addButton( TimelineCheckerObject object ) {
		JButton button = new JButton(object.getName());
		button.setPreferredSize(buttonSize);

		//Edit menu
		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed( ActionEvent e ) {
				JPanel panelX = new JPanel();
				panelX.setLayout(new SpringLayout());

				JTextField nameField = new JTextField(), userNameID = new JTextField(), textToCheck = new JTextField(), idToNotify = new JTextField();
				nameField.setPreferredSize(new Dimension(120, 20));
				idToNotify.setPreferredSize(new Dimension(120, 20));
				idToNotify.setEditable(false);

				JLabel lName = new JLabel("Name: ", JLabel.TRAILING);
				panelX.add(lName);
				lName.setLabelFor(nameField);
				panelX.add(nameField);

				JCheckBox specificUser = new JCheckBox();
				JLabel lSpecificUser = new JLabel("Specific user: ", JLabel.TRAILING);
				panelX.add(lSpecificUser);
				lSpecificUser.setLabelFor(specificUser);
				panelX.add(specificUser);

				userNameID.setEditable(false);
				specificUser.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed( ActionEvent e ) {
						userNameID.setEditable(specificUser.isSelected());
					}
				});

				JLabel lUserName = new JLabel("User id for timeline: @", JLabel.TRAILING);
				panelX.add(lUserName);
				lUserName.setLabelFor(userNameID);
				panelX.add(userNameID);

				JLabel lTextToCheck = new JLabel("Text to check for: ", JLabel.TRAILING);
				panelX.add(lTextToCheck);
				lTextToCheck.setLabelFor(textToCheck);
				panelX.add(textToCheck);


				JSpinner spinnerHours = new JSpinner(new SpinnerNumberModel(24, 0, Integer.MAX_VALUE, 1));
				spinnerHours.setMaximumSize(new Dimension(20, 20));

				JLabel lHours = new JLabel("Max hours: ", JLabel.TRAILING);
				panelX.add(lHours);
				lHours.setLabelFor(spinnerHours);
				panelX.add(spinnerHours);

				JCheckBox notifyUser = new JCheckBox();
				JLabel lNotifyUser = new JLabel("Notify user: ", JLabel.TRAILING);
				panelX.add(lNotifyUser);
				lNotifyUser.setLabelFor(notifyUser);
				panelX.add(notifyUser);

				notifyUser.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed( ActionEvent e ) {
						idToNotify.setEditable(notifyUser.isSelected());
					}
				});

				JLabel lIdToNotify = new JLabel("User to notify: @", JLabel.TRAILING);
				panelX.add(lIdToNotify);
				lIdToNotify.setLabelFor(idToNotify);
				panelX.add(idToNotify);

				JCheckBox retweet = new JCheckBox();
				JLabel lRetweet = new JLabel("Retweet: ", JLabel.TRAILING);
				panelX.add(lRetweet);
				lRetweet.setLabelFor(retweet);
				panelX.add(retweet);

				JCheckBox favorite = new JCheckBox();
				JLabel lFavorite = new JLabel("Favorit: ", JLabel.TRAILING);
				panelX.add(lFavorite);
				lFavorite.setLabelFor(favorite);
				panelX.add(favorite);

				JButton delete = new JButton("Delete");

				delete.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed( ActionEvent e ) {

						MainTwitter.timelineCheckers.remove(object);
						panel.remove(button);
						scrollPane.validate();

						Window w = SwingUtilities.getWindowAncestor(delete);
						if (w != null) {
							w.setVisible(false);
						}

						panel.repaint();
					}
				});

				JLabel lDelete = new JLabel("", JLabel.TRAILING);
				panelX.add(lDelete);
				lDelete.setLabelFor(delete);
				panelX.add(delete);


				JButton manualUpdate = new JButton("Manual check");

				manualUpdate.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed( ActionEvent e ) {
						StatusHandler.performManualCheck(object);
					}
				});

				JLabel lManualCheck = new JLabel("", JLabel.TRAILING);
				panelX.add(lManualCheck);
				lDelete.setLabelFor(manualUpdate);
				panelX.add(manualUpdate);


				SpringUtilities.makeCompactGrid(panelX, 13, 2, 6, 6, 6, 6);

				nameField.setText(object.getName());
				userNameID.setText(object.getUserTimeLineID());
				idToNotify.setText(object.getIdToNotify());

				textToCheck.setText(object.getTextToCheck());

				spinnerHours.setValue(object.getHoursLimit());
				notifyUser.setSelected(object.isNotifyUser());

				boolean r = false, f = false;

				for (Actions act : object.getActions()) {
					if (act == Actions.FAVORITE) f = true;

					if (act == Actions.RETWEET) r = true;
				}

				retweet.setSelected(r);
				favorite.setSelected(f);

				int result = JOptionPane.showConfirmDialog(null, panelX, "Edit rule", JOptionPane.OK_CANCEL_OPTION);
				if (result == JOptionPane.OK_OPTION) {
					String name = nameField.getText();
					String id = userNameID.getText();

					String textToChecked = textToCheck.getText();

					int Hours = (Integer) spinnerHours.getValue();

					boolean notfUser = notifyUser.isSelected(), retw = retweet.isSelected(), fav = favorite.isSelected();

					Actions[] act = new Actions[]{ retw ? Actions.RETWEET : null, fav ? Actions.FAVORITE : null };

					MainTwitter.timelineCheckers.remove(object);

					TimelineCheckerObject object = new TimelineCheckerObject(name, specificUser.isSelected(), id, textToChecked, Hours, notfUser, idToNotify.getText(), act);
					MainTwitter.timelineCheckers.add(object);

					addButton(object);
					panel.remove(button);
					scrollPane.validate();

				}
			}
		});

		StatusHandler.performManualCheck(object);

		panel.add(button);
		scrollPane.validate();
	}


	public void addButtons() {
		for (TimelineCheckerObject timelineChecker : MainTwitter.timelineCheckers) {
			addButton(timelineChecker);
		}

		JButton button = new JButton("Add new rule");
		button.setPreferredSize(buttonSize);

		//Add rule menu
		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed( ActionEvent e ) {
				JPanel panel = new JPanel();
				panel.setLayout(new SpringLayout());

				JTextField nameField = new JTextField(), userNameID = new JTextField(), textToCheck = new JTextField(), idToNotify = new JTextField();
				nameField.setPreferredSize(new Dimension(120, 20));
				idToNotify.setPreferredSize(new Dimension(120, 20));
				idToNotify.setEditable(false);

				JLabel lName = new JLabel("Name: ", JLabel.TRAILING);
				panel.add(lName);
				lName.setLabelFor(nameField);
				panel.add(nameField);

				JCheckBox specificUser = new JCheckBox();
				JLabel lSpecificUser = new JLabel("Specific user: ", JLabel.TRAILING);
				panel.add(lSpecificUser);
				lSpecificUser.setLabelFor(specificUser);
				panel.add(specificUser);

				userNameID.setEditable(false);
				specificUser.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed( ActionEvent e ) {
						userNameID.setEditable(specificUser.isSelected());
					}
				});

				JLabel lUserName = new JLabel("User id for timeline: @", JLabel.TRAILING);
				panel.add(lUserName);
				lUserName.setLabelFor(userNameID);
				panel.add(userNameID);

				JLabel lTextToCheck = new JLabel("Text to check for: ", JLabel.TRAILING);
				panel.add(lTextToCheck);
				lTextToCheck.setLabelFor(textToCheck);
				panel.add(textToCheck);

				JSpinner spinnerHours = new JSpinner(new SpinnerNumberModel(24, 0, Integer.MAX_VALUE, 1));
				spinnerHours.setMaximumSize(new Dimension(20, 20));

				JLabel lHours = new JLabel("Max hours: ", JLabel.TRAILING);
				panel.add(lHours);
				lHours.setLabelFor(spinnerHours);
				panel.add(spinnerHours);

				JCheckBox notifyUser = new JCheckBox();
				notifyUser.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed( ActionEvent e ) {
						idToNotify.setEditable(notifyUser.isSelected());
					}
				});

				JLabel lNotifyUser = new JLabel("Notify user: ", JLabel.TRAILING);
				panel.add(lNotifyUser);
				lNotifyUser.setLabelFor(notifyUser);
				panel.add(notifyUser);

				JLabel lIdToNotify = new JLabel("User to notify: @", JLabel.TRAILING);
				panel.add(lIdToNotify);
				lIdToNotify.setLabelFor(idToNotify);
				panel.add(idToNotify);

				JCheckBox retweet = new JCheckBox();
				JLabel lRetweet = new JLabel("Retweet: ", JLabel.TRAILING);
				panel.add(lRetweet);
				lRetweet.setLabelFor(retweet);
				panel.add(retweet);

				JCheckBox favorite = new JCheckBox();
				JLabel lFavorite = new JLabel("Favorit: ", JLabel.TRAILING);
				panel.add(lFavorite);
				lFavorite.setLabelFor(favorite);
				panel.add(favorite);


				SpringUtilities.makeCompactGrid(panel, 11, 2, 6, 6, 6, 6);

				int result = JOptionPane.showConfirmDialog(null, panel, "Add new rule", JOptionPane.OK_CANCEL_OPTION);
				if (result == JOptionPane.OK_OPTION) {
					String name = nameField.getText();
					String id = userNameID.getText();

					String textToChecked = textToCheck.getText();

					int Hours = (Integer) spinnerHours.getValue();

					boolean notfUser = notifyUser.isSelected(), retw = retweet.isSelected(), fav = favorite.isSelected();

					Actions[] act = new Actions[]{ retw ? Actions.RETWEET : null, fav ? Actions.FAVORITE : null };

					TimelineCheckerObject object = new TimelineCheckerObject(name, specificUser.isSelected(), id, textToChecked, Hours, notfUser, idToNotify.getText(), act);
					MainTwitter.timelineCheckers.add(object);
					addButton(object);

				}
			}
		});

		panel.add(button);
		scrollPane.validate();
	}


}