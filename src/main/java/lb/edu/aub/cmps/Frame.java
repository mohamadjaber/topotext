package lb.edu.aub.cmps;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Highlighter;
import javax.swing.text.JTextComponent;
import java.awt.Color;
import java.awt.Desktop;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JTextField;
import javax.swing.JTextArea;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.awt.Font;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import javax.swing.border.TitledBorder;

public class Frame extends JFrame {
	/**
	 * 
	 */
	private String filePath;
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTextField cloudSizeField;
	JLabel posLabel;
	JComboBox<String> comboBox;
	private String chosenLocation;
	private int size = 0;
	private int currentLocation = 0;
	private LinkedList<Integer> wordIndeces;

	private ReadNovelInterface readNovel;
	private GenerateWordCloudInteface generateCloud;
	private PartsOfSpeechInterface pos;
	private CountFrequency freq;

	private LinkedList<Integer> indicesForScroll;
	private int indexForScroll;

	final JCheckBox chckbxNoun;
	final JCheckBox chckbxVerb;
	final JCheckBox chckbxAdverb;
	final JCheckBox chckbxAdjective;

	final JLabel lblMostFrequentWord;
	private Set<String> locations;

	/**
	 * Launch the application.
	 */

	/**
	 * Create the frame.
	 */
	public Frame() {
		setTitle("Topotext Digital Tool\r\n");
		pos = new PartsOfSpeechImp();
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 1200, 470);
		contentPane = new JPanel();
		contentPane.setForeground(new Color(240, 230, 140));
		contentPane.setBackground(new Color(176, 224, 230));
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		final JTextArea textArea = new JTextArea();
		textArea.setWrapStyleWord(true);
		textArea.setLineWrap(true);
		textArea.setColumns(5);
		textArea.setFont(new Font("Times New Roman", Font.PLAIN, 16));
		textArea.setEditable(false);

		final JComboBox<String> comboBox = new JComboBox<String>();
		comboBox.setEnabled(false);
		comboBox.setBounds(857, 88, 278, 20);
		contentPane.add(comboBox);

		final JButton btnHighlight = new JButton("Highlight Location");
		btnHighlight.setEnabled(false);
		btnHighlight.setFont(new Font("Franklin Gothic Medium Cond",
				Font.PLAIN, 14));
		btnHighlight.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {

				if (comboBox == null || comboBox.getSelectedItem() == null
						|| comboBox.getSelectedItem().equals("Locations")) {
					JOptionPane
							.showMessageDialog(null,
									"Please select a location before",
									"Received Message",
									JOptionPane.INFORMATION_MESSAGE);
				} else {
					chosenLocation = "" + comboBox.getSelectedItem();
					if (cloudSizeField.getText().equals(""))
						size = 0;
					else
						size = Integer.parseInt(cloudSizeField.getText());
					wordIndeces = readNovel.getLocationsWithIndeces().get(
							chosenLocation);
					currentLocation = wordIndeces.get(0);
					highlightLocation(textArea, chosenLocation);
					textArea.setCaretPosition(currentLocation);
					highlightWithDistance(textArea, chosenLocation,
							currentLocation, size, wordIndeces);
					indexForScroll = 0;
					textArea.setCaretPosition(indicesForScroll
							.get(indexForScroll));
				}
			}
		});

		final JScrollPane scrollArea = new JScrollPane();
		scrollArea.setViewportBorder(new TitledBorder(null, "",
				TitledBorder.CENTER, TitledBorder.TOP, null, null));

		scrollArea.setBounds(21, 85, 821, 295);
		contentPane.add(scrollArea);

		textArea.setRows(10);
		scrollArea.setViewportView(textArea);
		btnHighlight.setBounds(857, 234, 278, 25);
		contentPane.add(btnHighlight);

		final JLabel lblCloudSize = new JLabel("Distance");
		lblCloudSize.setForeground(new Color(72, 61, 139));
		lblCloudSize.setFont(new Font("Franklin Gothic Medium Cond",
				Font.PLAIN, 18));
		lblCloudSize.setBounds(857, 164, 146, 20);
		contentPane.add(lblCloudSize);

		cloudSizeField = new JTextField();
		cloudSizeField.setBounds(1009, 167, 126, 20);
		contentPane.add(cloudSizeField);
		cloudSizeField.setColumns(10);

		final JButton btnPrevious = new JButton("Previous");
		btnPrevious.setEnabled(false);
		btnPrevious.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (comboBox == null || comboBox.getSelectedItem() == null
						|| comboBox.getSelectedItem().equals("Locations")) {
					JOptionPane
							.showMessageDialog(null,
									"Please select a location before",
									"Received Message",
									JOptionPane.INFORMATION_MESSAGE);
				}

				else {
					previousLocation(textArea, chosenLocation, currentLocation,
							wordIndeces);
				}
			}
		});
		btnPrevious.setFont(new Font("Franklin Gothic Medium Cond", Font.PLAIN,
				14));
		btnPrevious.setBounds(857, 195, 126, 23);
		contentPane.add(btnPrevious);

		final JButton nextBtn = new JButton("Next");
		nextBtn.setEnabled(false);
		nextBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (comboBox == null || comboBox.getSelectedItem() == null
						|| comboBox.getSelectedItem().equals("Locations")) {
					JOptionPane
							.showMessageDialog(null,
									"Please select a location before",
									"Received Message",
									JOptionPane.INFORMATION_MESSAGE);
				} else {
					chosenLocation = "" + comboBox.getSelectedItem();
					nextLocation(textArea, chosenLocation, currentLocation,
							wordIndeces);
				}
			}
		});
		nextBtn.setFont(new Font("Franklin Gothic Medium Cond", Font.PLAIN, 14));
		nextBtn.setBounds(1009, 195, 126, 23);
		contentPane.add(nextBtn);

		JLabel lblLocationDetector = new JLabel("Location Detector");
		lblLocationDetector.setForeground(new Color(75, 0, 130));
		lblLocationDetector.setFont(new Font("Adobe Caslon Pro Bold",
				Font.BOLD, 45));
		lblLocationDetector.setBounds(22, 11, 540, 76);
		contentPane.add(lblLocationDetector);

		chckbxNoun = new JCheckBox("Noun");
		chckbxNoun.setEnabled(false);
		chckbxNoun.setBackground(new Color(176, 224, 230));
		chckbxNoun.setBounds(977, 115, 70, 23);
		contentPane.add(chckbxNoun);

		chckbxVerb = new JCheckBox("Verb");
		chckbxVerb.setEnabled(false);
		chckbxVerb.setBackground(new Color(176, 224, 230));
		chckbxVerb.setBounds(977, 134, 70, 23);
		contentPane.add(chckbxVerb);

		chckbxAdjective = new JCheckBox("Adjective");
		chckbxAdjective.setEnabled(false);
		chckbxAdjective.setBackground(new Color(176, 224, 230));
		chckbxAdjective.setBounds(1058, 115, 170, 23);
		contentPane.add(chckbxAdjective);

		chckbxAdverb = new JCheckBox("Adverb");
		chckbxAdverb.setEnabled(false);
		chckbxAdverb.setBackground(new Color(176, 224, 230));
		chckbxAdverb.setBounds(1058, 134, 170, 23);
		contentPane.add(chckbxAdverb);

		final JButton btnCloud = new JButton("Generate word cloud");
		btnCloud.setEnabled(false);
		btnCloud.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (comboBox == null || comboBox.getSelectedItem() == null
						|| comboBox.getSelectedItem().equals("Locations")) {
					JOptionPane
							.showMessageDialog(null,
									"Please select a location before",
									"Received Message",
									JOptionPane.INFORMATION_MESSAGE);
				} else if (cloudSizeField.getText().equals("0")
						|| cloudSizeField.getText().equals(" "))
					JOptionPane
							.showMessageDialog(
									null,
									"Please provide a distance for the cloud before",
									"Received Message",
									JOptionPane.INFORMATION_MESSAGE);

				else {
					try {
						size = Integer.parseInt(cloudSizeField.getText());
						LinkedList<String> words = returnWords(textArea,
								currentLocation,
								Integer.parseInt(cloudSizeField.getText()));

						LinkedList<String> partsOfSpeech = new LinkedList<String>();

						int countParts = 0;
						if (chckbxNoun.isSelected()) {
							partsOfSpeech.add("noun");
							countParts++;
						}
						if (chckbxVerb.isSelected()) {
							partsOfSpeech.add("verb");
							countParts++;

						}
						if (chckbxAdverb.isSelected()) {
							partsOfSpeech.add("adverb");
							countParts++;

						}
						if (chckbxAdjective.isSelected()) {
							partsOfSpeech.add("adjective");
							countParts++;

						}
						LinkedList<String> neededWords = returnWords(textArea,
								currentLocation,
								Integer.parseInt(cloudSizeField.getText()));

						if (countParts == 0) {
							LinkedList<String> fullParts = new LinkedList<String>();
							fullParts.add("verb");
							fullParts.add("noun");
							fullParts.add("adverb");
							fullParts.add("adjective");
							neededWords = pos.promptedPartsOfSpeech(words,
									fullParts);

						} else
							neededWords = pos.promptedPartsOfSpeech(words,
									partsOfSpeech);

						String toCloud = toCloud(neededWords);

						generateCloud = new GenerateWordCloud(toCloud);
						generateCloud.generateWordCloud(new File(
								"Files/cloud.html"));
					} catch (NumberFormatException e) {
						JOptionPane
								.showMessageDialog(
										null,
										"Please provide a valid non-negative distance for the cloud before",
										"Received Message",
										JOptionPane.INFORMATION_MESSAGE);

					}
				}
			}
		});
		btnCloud.setFont(new Font("Franklin Gothic Medium Cond", Font.PLAIN, 14));
		btnCloud.setBounds(857, 270, 278, 23);
		contentPane.add(btnCloud);

		final JComboBox<String> countries_comboBox = new JComboBox<String>();
		countries_comboBox.setEnabled(false);
		countries_comboBox.setBounds(857, 334, 126, 25);
		contentPane.add(countries_comboBox);
		countries_comboBox.setModel(new DefaultComboBoxModel<String>(
				WorldCountries.countries()));

		lblMostFrequentWord = new JLabel("Most Frequent Word:");
		lblMostFrequentWord.setForeground(new Color(72, 61, 139));
		lblMostFrequentWord.setFont(new Font("Franklin Gothic Medium Cond",
				Font.PLAIN, 18));
		lblMostFrequentWord.setBounds(857, 374, 278, 24);
		contentPane.add(lblMostFrequentWord);

		lblMostFrequentWord.setEnabled(false);

		final JButton btnMap = new JButton("Show Map");
		btnMap.setEnabled(false);
		btnMap.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				DrawMapInterface drawMap = new DrawMap();
				if ((countries_comboBox.getSelectedItem())
						.equals("Select Country")) {
					boolean done = drawMap.drawMap(locations);
					if (!done)
						JOptionPane.showMessageDialog(null,
								"Error occured while drawing the map",
								"Received Message",
								JOptionPane.INFORMATION_MESSAGE);

				} else {
					boolean done = drawMap.drawMapInCountry(locations,
							(String) countries_comboBox.getSelectedItem());
					if (!done)
						JOptionPane
								.showMessageDialog(
										null,
										"Error occured while drawing the Map\nThe country you selected has no locations in this text",
										"Received Message",
										JOptionPane.INFORMATION_MESSAGE);

				}
			}
		});
		btnMap.setFont(new Font("Franklin Gothic Medium Cond", Font.PLAIN, 14));
		btnMap.setBounds(1013, 334, 122, 23);
		contentPane.add(btnMap);

		JButton btnBrowse = new JButton("Browse");
		btnBrowse.setFont(new Font("Franklin Gothic Medium Cond", Font.PLAIN,
				14));

		final JLabel progressLabel = new JLabel("");
		progressLabel.setBounds(857, 41, 266, 36);
		contentPane.add(progressLabel);

		final JButton btnGenerateGlobalWord = new JButton(
				"Generate Global Word Cloud");
		btnGenerateGlobalWord.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String toCloud = generateGlobalCloud(textArea, wordIndeces,
						Integer.parseInt(cloudSizeField.getText()));
				generateCloud = new GenerateWordCloud(toCloud);
				generateCloud.generateWordCloud(new File("Files/cloud.html"));
			}
		});
		btnGenerateGlobalWord.setEnabled(false);
		btnGenerateGlobalWord.setFont(new Font("Franklin Gothic Medium Cond",
				Font.PLAIN, 14));
		btnGenerateGlobalWord.setBounds(857, 304, 278, 23);
		contentPane.add(btnGenerateGlobalWord);

		final JLabel pathLabel = new JLabel("");
		pathLabel.setBounds(216, 391, 295, 23);
		contentPane.add(pathLabel);
		btnBrowse.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				progressLabel.setText("Loading to get the locations...");
				JFileChooser chooser = new JFileChooser();

				// we add the filter to force the user to add only properties
				// file
				FileNameExtensionFilter filter = new FileNameExtensionFilter(
						"Text File", "txt");
				chooser.setFileFilter(filter);
				chooser.addChoosableFileFilter(filter);
				int returnVal = chooser.showOpenDialog(null);
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					lblMostFrequentWord.setEnabled(true);

					comboBox.setEnabled(true);
					countries_comboBox.setEnabled(true);
					btnPrevious.setEnabled(true);
					nextBtn.setEnabled(true);
					btnHighlight.setEnabled(true);
					btnMap.setEnabled(true);
					btnCloud.setEnabled(true);
					btnGenerateGlobalWord.setEnabled(true);
					chckbxAdverb.setEnabled(true);
					chckbxVerb.setEnabled(true);
					chckbxAdjective.setEnabled(true);
					chckbxNoun.setEnabled(true);
					filePath = chooser.getSelectedFile().getPath();
					pathLabel.setText(filePath.toString());
					File file = new File(filePath);

					try {
						Scanner sc = new Scanner(file);
						String text = " ";

						while (sc.hasNextLine()) {
							String line = sc.nextLine();
							text += line + "\n";
						}
						sc.close();
						textArea.setText(text);

					} catch (FileNotFoundException e1) {
						e1.printStackTrace();
					}

					readNovel = new ReadNovelImp(filePath);
					locations = readNovel.getLocations();
					String[] locationsArray = new String[locations.size() + 1];
					locationsArray[0] = "Locations";
					int i = 1;
					for (String loc : locations) {
						locationsArray[i] = loc;
						i++;
					}
					comboBox.setModel(new DefaultComboBoxModel<String>(
							locationsArray));
					locations = readNovel.getLocations();
					progressLabel.setText("");
				}
			}
		});

		btnBrowse.setBounds(21, 391, 185, 23);
		contentPane.add(btnBrowse);

		JLabel lblPartOfSpeech = new JLabel("Part Of Speech:");
		lblPartOfSpeech.setForeground(new Color(72, 61, 139));
		lblPartOfSpeech.setFont(new Font("Franklin Gothic Medium Cond",
				Font.PLAIN, 16));
		lblPartOfSpeech.setBounds(857, 113, 146, 20);
		contentPane.add(lblPartOfSpeech);

		JButton btnHelp = new JButton("Help");
		btnHelp.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				try {
					Desktop.getDesktop().open(new File("Files/Help.txt"));
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
		btnHelp.setBackground(new Color(0, 206, 209));
		btnHelp.setForeground(new Color(72, 61, 139));
		btnHelp.setFont(new Font("Franklin Gothic Medium Cond", Font.PLAIN, 14));
		btnHelp.setBounds(1095, 0, 89, 23);
		contentPane.add(btnHelp);
	}

	public void highlightLocation(JTextComponent textComp, String location) {
		// Highlights a single location throughout the text
		Highlighter.HighlightPainter locationHighlighter = new MyHighlighPainter(
				Color.cyan);

		try {
			Highlighter highlighter = textComp.getHighlighter();
			highlighter.removeAllHighlights();
			Document doc = textComp.getDocument();
			String text = doc.getText(0, doc.getLength()).toUpperCase();
			int size = location.length();
			LinkedList<Integer> indeces = allIndeces(text,
					location.toUpperCase());
			for (int i = 0; i < indeces.size(); i++) {
				int index = indeces.get(i);
				highlighter.addHighlight(index, index + size,
						locationHighlighter);
			}

			if (!cloudSizeField.getText().equals("")) {

				LinkedList<String> words = returnWordsIgnoringUnintersesting(
						textComp, currentLocation,
						Integer.parseInt(cloudSizeField.getText()));

				freq = new CountFrequencyImp(words);

				lblMostFrequentWord
						.setText("Most Frequent Word: " + freq.MFW());
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void highlightWithDistance(JTextComponent textComp, String location,
			int index, int size, LinkedList<Integer> wordIndeces) {
		Highlighter.HighlightPainter cloudHighlighter = new MyHighlighPainter(
				new Color(211, 211, 211));
		Highlighter highlighter = textComp.getHighlighter();
		Document doc = textComp.getDocument();
		String text;
		try {
			text = doc.getText(0, doc.getLength()).toUpperCase();
			int txtSize = text.split(" ").length - 1;
			location = location.toUpperCase();
			LinkedList<Integer> locationIndeces = allIndeces(text, location);
			Map<Integer, Integer> map = getMap(wordIndeces, locationIndeces);
			indicesForScroll = locationIndeces;
			int locationIndex = map.get(index);

			if (size >= index) {
				highlighter.addHighlight(0, locationIndex, cloudHighlighter);
			} else if (size != 0) {
				int i = locationIndex - 2;
				int counterBefore = 0;

				while (i >= 0 && counterBefore < size) {
					if (text.charAt(i) == ' ') {
						i--;
						counterBefore++;
					} else
						i--;
				}
				highlighter
						.addHighlight(i + 2, locationIndex, cloudHighlighter);
			}
			if (locationIndeces.indexOf(index) + size >= txtSize && size != 0) {

				highlighter.addHighlight(locationIndex + location.length(),
						text.length(), cloudHighlighter);
			} else if (size != 0) {
				int start = locationIndex + location.length() + 1;
				int i = start;
				int counterAfter = 0;

				while (i < text.length() && counterAfter < size) {
					if (text.charAt(i) == ' ') {
						i++;
						counterAfter++;
					} else
						i++;
				}
				highlighter.addHighlight(start - 1, i - 1, cloudHighlighter);
			}

		} catch (BadLocationException e) {
			e.printStackTrace();
		}
	}

	public LinkedList<String> returnWords(JTextComponent textComp, int index,
			int size) {

		LinkedList<String> toReturn = new LinkedList<String>();
		Document doc = textComp.getDocument();
		String text = "";
		String[] wordList;
		try {
			text = doc.getText(0, doc.getLength());
			wordList = text.split("\\W+");

			// To avoid lower index out of bound
			if (index - size < 0) {
				for (int i = 0; i < index; i++)
					if (!wordList[i].equals(chosenLocation))
						toReturn.add(wordList[i]);
			}

			// If no lower index out of bound
			else {
				for (int i = index - size; i < index; i++) {
					if (!wordList[i].equals(chosenLocation))
						toReturn.add(wordList[i]);
				}
			}

			// To avoid upper index out of bound
			if (index + size >= wordList.length) {
				for (int i = index + 1; i < wordList.length; i++)
					if (!wordList[i].equals(chosenLocation))
						toReturn.add(wordList[i]);
			}

			// If no upper index out of bound
			else {
				for (int i = index + 1; i <= index + size; i++)
					if (!wordList[i].equals(chosenLocation))
						toReturn.add(wordList[i]);
			}

		} catch (BadLocationException e) {
			e.printStackTrace();
		}
		return toReturn;
	}

	public String toCloud(LinkedList<String> returnedWords) {
		String toCloud = "";
		for (int i = 0; i < returnedWords.size(); i++) {
			toCloud += returnedWords.get(i) + " ";
		}
		return toCloud;
	}

	public static LinkedList<Integer> allIndeces(String s, String word) {
		word = word.split(" ")[0];
		LinkedList<Integer> positions = new LinkedList<Integer>();
		Pattern p = Pattern.compile(" " + word.toUpperCase());
		Pattern p1 = Pattern.compile("\n" + word.toUpperCase());
		Pattern p2 = Pattern.compile("\t" + word.toUpperCase());
		Matcher m = p.matcher(" " + s.toUpperCase());
		Matcher m1 = p1.matcher(" " + s.toUpperCase());
		Matcher m2 = p2.matcher(" " + s.toUpperCase());

		while (m.find()) {
			positions.add(m.start());
		}

		while (m1.find()) {
			positions.add(m1.start());
		}

		while (m2.find()) {
			positions.add(m2.start());
		}
		return positions;
	}

	public Map<Integer, Integer> getMap(LinkedList<Integer> wordIndex,
			LinkedList<Integer> occurances) {

		Map<Integer, Integer> map = new HashMap<Integer, Integer>();
		for (int i = 0; i < wordIndex.size(); i++) {
			map.put(wordIndex.get(i), occurances.get(i));
		}
		return map;
	}

	public void setCurrentLocation(int newLocation) {
		currentLocation = newLocation;
	}

	public void nextLocation(JTextComponent textComp, String location,
			int currentIndex, LinkedList<Integer> indexList) {
		try {
			Highlighter highlighter = textComp.getHighlighter();
			highlighter.removeAllHighlights();
			highlightLocation(textComp, location);
			indexList = wordIndeces;
			int index = indexList.indexOf(currentIndex);
			if (indexForScroll < indicesForScroll.size() - 1)
				indexForScroll++;
			currentIndex = indicesForScroll.get(indexForScroll);
			if (index == indexList.size() - 1) {
				JOptionPane.showMessageDialog(null,
						"No more instances of this location");
			} else {
				index++;
				setCurrentLocation(indexList.get(index));
				textComp.setCaretPosition(currentIndex + 100);
				highlightWithDistance(textComp, location, indexList.get(index),
						size, indexList);

				LinkedList<String> words = returnWordsIgnoringUnintersesting(
						textComp, currentLocation,
						Integer.parseInt(cloudSizeField.getText()));

				freq = new CountFrequencyImp(words);

				lblMostFrequentWord
						.setText("Most Frequent Word: " + freq.MFW());
			}
		} catch (Exception e) {

		}

	}

	public void previousLocation(JTextComponent textComp, String location,
			int currentIndex, LinkedList<Integer> indexList) {
		try {
			Highlighter highlighter = textComp.getHighlighter();
			highlighter.removeAllHighlights();
			highlightLocation(textComp, location);
			indexList = wordIndeces;
			int index = indexList.indexOf(currentIndex);
			if (indexForScroll > 0)
				indexForScroll--;
			currentIndex = indicesForScroll.get(indexForScroll);
			if (index == 0) {
				JOptionPane.showMessageDialog(null,
						"No more instances of this location");
			} else if (index < indexList.size()) {
				index--;
				setCurrentLocation(indexList.get(index));
				textComp.setCaretPosition(currentIndex);
				highlightWithDistance(textComp, location, indexList.get(index),
						Integer.parseInt(cloudSizeField.getText()), indexList);
			} else {
				index = indexList.size() - 1;
				setCurrentLocation(indexList.get(index));
				textComp.setCaretPosition(currentIndex);
				highlightWithDistance(textComp, location, indexList.get(index),
						Integer.parseInt(cloudSizeField.getText()), indexList);

				LinkedList<String> words = returnWordsIgnoringUnintersesting(
						textComp, currentLocation,
						Integer.parseInt(cloudSizeField.getText()));

				freq = new CountFrequencyImp(words);

				lblMostFrequentWord
						.setText("Most Frequent Word: " + freq.MFW());
			}
		} catch (Exception e) {

		}
	}

	public String generateGlobalCloud(JTextComponent textComp,
			LinkedList<Integer> indeces, int size) {

		String toGlobalCloud = "";
		for (int i = 0; i < indeces.size(); i++) {
			LinkedList<String> words = returnWords(textComp, indeces.get(i),
					size);

			LinkedList<String> partsOfSpeech = new LinkedList<String>();

			int countParts = 0;
			if (chckbxNoun.isSelected()) {
				partsOfSpeech.add("noun");
				countParts++;
			}
			if (chckbxVerb.isSelected()) {
				partsOfSpeech.add("verb");
				countParts++;

			}
			if (chckbxAdverb.isSelected()) {
				partsOfSpeech.add("adverb");
				countParts++;

			}
			if (chckbxAdjective.isSelected()) {
				partsOfSpeech.add("adjective");
				countParts++;

			}
			LinkedList<String> neededWords = new LinkedList<String>();

			if (countParts == 0) {
				LinkedList<String> fullParts = new LinkedList<String>();
				fullParts.add("verb");
				fullParts.add("noun");
				fullParts.add("adverb");
				fullParts.add("adjective");
				neededWords = pos.promptedPartsOfSpeech(words, fullParts);

			} else
				neededWords = pos.promptedPartsOfSpeech(words, partsOfSpeech);

			toGlobalCloud += toCloud(neededWords) + " ";
		}
		return toGlobalCloud;
	}

	public LinkedList<String> returnWordsIgnoringUnintersesting(
			JTextComponent textComp, int index, int size) {

		LinkedList<String> ignore = UninterstingWords.uninterstingWords();
		LinkedList<String> toReturn = new LinkedList<String>();
		Document doc = textComp.getDocument();
		String text = "";
		String[] wordList;
		try {
			text = doc.getText(0, doc.getLength());
			wordList = text.split("\\W+");

			// To avoid lower index out of bound
			if (index - size < 0) {
				for (int i = 0; i < index; i++)
					if (!ignore.contains(wordList[i]))
						toReturn.add(wordList[i]);
			}

			// If no lower index out of bound
			else {
				for (int i = index - size; i < index; i++) {
					if (!ignore.contains(wordList[i]))

						toReturn.add(wordList[i]);
				}
			}

			// To avoid upper index out of bound
			if (index + size >= wordList.length) {
				for (int i = index + 1; i < wordList.length; i++)
					if (!ignore.contains(wordList[i]))
						toReturn.add(wordList[i]);
			}

			// If no upper index out of bound
			else {
				for (int i = index + 1; i <= index + size; i++)
					if (!ignore.contains(wordList[i]))
						toReturn.add(wordList[i]);
			}

		} catch (BadLocationException e) {
			e.printStackTrace();
		}
		return toReturn;
	}
}