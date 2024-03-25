import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

public class cryptoquote implements ActionListener {

	private JFrame frame;
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					cryptoquote window = new cryptoquote();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public cryptoquote() {
		initialize();
	}

	JTextArea encrypted;
	JTextArea decrypted;
	JTextField countLabel;
	JTextField letterLabel;
	JTextField subLabel;
	JTextField cryptTF;
	JTextField plainTF;
	JButton updateButton;
	JButton resetButton;
	JButton undoButton;

	Stack<Character> undoStack = new Stack<Character>();

	private void initialize() {
		frame = new JFrame();
		frame.setTitle("CrytoQuote Helper");
		frame.setBounds(100, 100, 900, 420);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		
		encrypted = new JTextArea();
		encrypted.setText("Enter encrypted quote");
		encrypted.setWrapStyleWord(true);
		encrypted.setRows(4);
		encrypted.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 24));
		encrypted.setColumns(60);
		encrypted.setLineWrap(true);
		frame.getContentPane().add(encrypted);
		
		decrypted = new JTextArea();
		decrypted.setForeground(Color.RED);
		decrypted.setText("");
		decrypted.setWrapStyleWord(true);
		decrypted.setEditable(false);
		decrypted.setRows(4);
		decrypted.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 24));
		decrypted.setLineWrap(true);
		decrypted.setColumns(60);
		frame.getContentPane().add(decrypted);
		
		JPanel statsPanel = new JPanel();
		statsPanel.setBackground(Color.WHITE);
		frame.getContentPane().add(statsPanel);
		statsPanel.setLayout(new GridLayout(0, 1, 0, 0));
		
		countLabel = new JTextField("                                                                              ");
		countLabel.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 18));
		countLabel.setEditable(false);
		statsPanel.add(countLabel);
		
		letterLabel = new JTextField(" A  B  C  D  E  F  G  H  I  J  K  L  M  N  O  P  Q  R  S  T  U  V  W  X  Y  Z ");
		letterLabel.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 18));
		letterLabel.setEditable(false);
		statsPanel.add(letterLabel);
		
		subLabel = new JTextField(" -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  - ");
		subLabel.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 18));
		subLabel.setForeground(Color.RED);
		subLabel.setEditable(false);
		statsPanel.add(subLabel);
		
		JPanel bottomPanel = new JPanel();
		bottomPanel.setLayout(new BorderLayout());
		frame.getContentPane().add(bottomPanel);
		
		JPanel subPanel = new JPanel();
		subPanel.setBackground(Color.WHITE);
		
		cryptTF = new JTextField("   ");
		subPanel.add(cryptTF);
		JLabel equalsLabel = new JLabel("EQUALS");
		subPanel.add(equalsLabel);
		plainTF = new JTextField("   ");
		plainTF.addActionListener(this);
		subPanel.add(plainTF);
		bottomPanel.add(subPanel, BorderLayout.WEST);
		
		JLabel spacer = new JLabel("                         ");
		bottomPanel.add(spacer, BorderLayout.CENTER);
		
		JPanel buttonPanel = new JPanel();
		buttonPanel.setBackground(Color.WHITE);
		updateButton = new JButton("Update");
		updateButton.addActionListener(this);
		buttonPanel.add(updateButton);
		undoButton = new JButton("Undo");
		undoButton.addActionListener(this);
		undoButton.setEnabled(false);
		buttonPanel.add(undoButton);
		resetButton = new JButton("Reset Subs");
		resetButton.addActionListener(this);
		buttonPanel.add(resetButton);
		bottomPanel.add(buttonPanel, BorderLayout.EAST);

	}
	

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == resetButton) {
			countLabel.setText("                                                                              ");
			letterLabel.setText(" A  B  C  D  E  F  G  H  I  J  K  L  M  N  O  P  Q  R  S  T  U  V  W  X  Y  Z ");
			subLabel.setText(" -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  - ");
			cryptTF.setText("   ");
			plainTF.setText("   ");
			updateCounts();
			updateDecrypt();
			undoStack.removeAllElements();
			undoButton.setEnabled(false);
			cryptTF.requestFocusInWindow();
		} else if (e.getSource() == undoButton) {
			if (!undoStack.isEmpty()) {
				char plain = undoStack.pop();
				char crypt = undoStack.pop();
				int j = "ABCDEFGHIJKLMNOPQRSTUVWXYZ".indexOf(crypt);
				String subs = subLabel.getText();
				subLabel.setText(subs.substring(0, j * 3 + 1) + plain + subs.substring(j * 3 + 2));
			}
			updateCounts();
			updateDecrypt();
			if (undoStack.isEmpty()) {
				undoButton.setEnabled(false);
			}
			cryptTF.requestFocusInWindow();
		} else {
			updateCounts();
			updateSubs();
			updateDecrypt();
			cryptTF.setText("   ");
			plainTF.setText("   ");
			cryptTF.requestFocusInWindow();
			undoButton.setEnabled(true);
		}
	}

	void updateCounts() {
		int[] letterCounts = new int[26];
		for (int i = 0; i < 26; ++i) {
			letterCounts[i] = 0;
		}

		String countString = "";

		for (int i = 0; i < 26; ++i) {
			String n = String.valueOf(letterCounts[i]);
			if (n.equals("0")) {
				countString = countString + "   ";
			} else if (n.length() == 1) {
				countString = countString + " " + n + " ";
			} else {
				countString = countString + n + " ";
			}
		}
		countLabel.setText(countString);
	}

	void updateSubs() {
		String crypt = cryptTF.getText().trim().toUpperCase();
		String plain = plainTF.getText().trim().toUpperCase();
		String subs = subLabel.getText();
		int j = -1;
		int k = -1;

		if (crypt.length() == 1 && plain.length() == 1) {
			j = "ABCDEFGHIJKLMNOPQRSTUVWXYZ-".indexOf(crypt);
			k = "ABCDEFGHIJKLMNOPQRSTUVWXYZ-".indexOf(plain);
		}

		if (j >= 0 && k >= 0) {
			undoStack.push(subs.charAt(j * 3 + 1));
			undoStack.push(crypt.charAt(0));
			subLabel.setText(subs.substring(0, j * 3 + 1) + plain + subs.substring(j * 3 + 2));
		}
	}

	void updateDecrypt() {
		StringBuilder decryptBuilder = new StringBuilder();
		String subs = subLabel.getText();
		String encrypt = encrypted.getText().toUpperCase();
		Map<Character, Character> substitutionMap = new HashMap<>();

		for (int i = 0; i < 26; i++) {
			char encryptedChar = (char) ('A' + i);
			char decryptedChar = subs.charAt(i * 3 + 1);
			substitutionMap.put(encryptedChar, decryptedChar);
		}

		for (int i = 0; i < encrypt.length(); ++i) {
			char c = encrypt.charAt(i);
			if (Character.isLetter(c)) {
				char decryptedChar = substitutionMap.getOrDefault(c, c);
				decryptBuilder.append(decryptedChar);
			} else {
				decryptBuilder.append(c);
			}
		}
	
		decrypted.setText(decryptBuilder.toString());
	}
}