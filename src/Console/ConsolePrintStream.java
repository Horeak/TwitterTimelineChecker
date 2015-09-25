package Console;

import Utils.Status.MessageFormatter;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import java.io.PrintStream;

public class ConsolePrintStream extends PrintStream {

	public JEditorPane textArea;

	public ConsolePrintStream(JEditorPane textArea) {
	super(new ConsoleOutputStream(), true);

		this.textArea = textArea;
	}

	@Override
	public void print(String s) {
		super.print(s);
			try {
				final String text = s + "\n";

				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						try {

							if (textArea != null) {
								textArea.getDocument().insertString(textArea.getDocument().getLength(), MessageFormatter.formatOutgoingLogMessage(text), null);
							}

						} catch (BadLocationException e) {
							e.printStackTrace();
						}
					}
				});

			} catch (Exception e) {
				e.printStackTrace();
			}

	}
}