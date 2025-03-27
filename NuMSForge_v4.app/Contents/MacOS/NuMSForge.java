import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.awt.event.*;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.Arrays;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;
import javax.swing.JTextPane;
import javax.swing.text.StyledDocument;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.BadLocationException;
import javax.swing.text.Element;
import java.util.stream.Collectors;
import java.util.Set;
import java.util.HashSet;


public class NucleicAcidAppLauncher extends JFrame {
    public NucleicAcidAppLauncher() {
        setTitle("Nucleic Acid Tools");
        setSize(400, 400); 
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(5, 1, 10, 10)); 

        JButton massToMzButton = new JButton("Sequence to m/z");
        JButton mzToMassButton = new JButton("m/z to Sequence");
        JButton digestionButton = new JButton("Digestion");
        JButton BaseMassApp = new JButton("Base Mass");
        JButton helpButton = new JButton("Help");

        massToMzButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                NucleicAcidMassCalculatorApp app = new NucleicAcidMassCalculatorApp(NucleicAcidAppLauncher.this);
                app.setVisible(true);
            }
        });

        mzToMassButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                MzToMassApp app = new MzToMassApp();
                app.setVisible(true);
            }
        });

        digestionButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                DigestionApp app = new DigestionApp(NucleicAcidAppLauncher.this);
                app.setVisible(true);
            }
        });

        BaseMassApp.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                BaseMassApp app = new BaseMassApp();
                app.setVisible(true);
            }
        });

        helpButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(NucleicAcidAppLauncher.this,
                        "Nucleic Acid Tools:\n\n" +
                        "- Sequence to m/z: Calculator nucleic acid sequences to m/z values.\n" +
                        "- m/z to Sequence: Prediction sequences based on m/z values.\n" +
                        "- Digestion: Perform sequence digestion analysis.\n" +
                        "- Base Mass: Calculate nucleotide or nucleoside masses.\n\n" +
                        "For detailed help, consult the manual or support resources.",
                        "Help", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        add(massToMzButton);
        add(mzToMassButton);
        add(digestionButton);
        add(BaseMassApp);
        add(helpButton);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            NucleicAcidAppLauncher launcher = new NucleicAcidAppLauncher();
            launcher.setVisible(true);
        });
    }
}


class NucleicAcidMassCalculatorApp extends JFrame {
    private JTextField sequenceField;
    private JComboBox<String> typeComboBox;
    private JComboBox<String> massTypeComboBox;
    private JComboBox<String> modeComboBox;
    private JComboBox<String> fivePrimeComboBox;
    private JComboBox<String> threePrimeComboBox;
    private JComboBox<String> methodComboBox;

    public NucleicAcidMassCalculatorApp(JFrame parentFrame) {
        setTitle("Nucleic Acid Mass Calculator");
        setSize(320, 300);  
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        initializeUI();
    }

    private void initializeUI() {
        setLayout(new BorderLayout(5, 5));

        // Input Panel with Sequence Field and Calculate Button
        JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        JLabel label = new JLabel("Sequence:");
        sequenceField = new JTextField(10); 
        JButton calculateButton = new JButton("Calculate");

        inputPanel.add(label);
        inputPanel.add(sequenceField);
        inputPanel.add(calculateButton);
        add(inputPanel, BorderLayout.NORTH);

        // Options Panel with vertical layout for each ComboBox
        JPanel optionsPanel = new JPanel();
        optionsPanel.setLayout(new BoxLayout(optionsPanel, BoxLayout.Y_AXIS));
        
        typeComboBox = createComboBox(new String[]{"RNA", "DNA"});
        massTypeComboBox = createComboBox(new String[]{"Monoisotopic Mass", "Average Mass"});
        modeComboBox = createComboBox(new String[]{"Negative Mode","Positive Mode"});
        fivePrimeComboBox = createComboBox(new String[]{"OH", "Phosphate", "Triphosphate"});
        threePrimeComboBox = createComboBox(new String[]{"OH", "Phosphate", "Cyclic Phosphate"});
        methodComboBox = createComboBox(new String[]{"Electrospray Series", "CID Fragments"});

        // Adding each ComboBox with a label to the optionsPanel with minimum spacing
        optionsPanel.add(createLabeledPanel("Type:", typeComboBox));
        optionsPanel.add(createLabeledPanel("Mass Type:", massTypeComboBox));
        optionsPanel.add(createLabeledPanel("Mode:", modeComboBox));
        optionsPanel.add(createLabeledPanel("5' Terminal:", fivePrimeComboBox));
        optionsPanel.add(createLabeledPanel("3' Terminal:", threePrimeComboBox));
        optionsPanel.add(createLabeledPanel("Method:", methodComboBox));

        add(optionsPanel, BorderLayout.CENTER);

        calculateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showResultsWindow();
            }
        });
    }

    private JComboBox<String> createComboBox(String[] items) {
        JComboBox<String> comboBox = new JComboBox<>(items);
        comboBox.setPreferredSize(new Dimension(180, 20));  
        return comboBox;
    }

    private JPanel createLabeledPanel(String labelText, JComponent component) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0)); 
        JLabel label = new JLabel(labelText);
        label.setPreferredSize(new Dimension(80, label.getPreferredSize().height));  
        panel.add(label);
        panel.add(component);
        return panel;
    } 

    private void showResultsWindow() {
        String sequence = sequenceField.getText().toUpperCase().trim();
        if (sequence.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a nucleic acid sequence.");
            return;
        }
    
        double totalMass = 0.0;
        MassConstants.MassMode mode = massTypeComboBox.getSelectedItem().equals("Monoisotopic Mass")
                ? MassConstants.MassMode.MONOISOTOPIC
                : MassConstants.MassMode.AVERAGE;
        MassConstants.setMassMode(mode);
    
        List<Nucleotide> nucleotides = mode == MassConstants.MassMode.MONOISOTOPIC
                ? NucleotideRegistry.getMonoIsotopicNucleotides()
                : NucleotideRegistry.getAverageMassNucleotides();
    
        Map<String, Nucleotide> nucleotideMap = nucleotides.stream()
                .collect(Collectors.toMap(Nucleotide::getSymbol, nucleotide -> nucleotide));
    
        if (methodComboBox.getSelectedItem().equals("Electrospray Series")) {
            double linkageMassPerLink = MassConstants.getPhosphateMass();
            double totalLinkageMass = linkageMassPerLink * (sequence.length() - 1);
    
            for (char base : sequence.toCharArray()) {
                Nucleotide nucleotide = nucleotideMap.get(String.valueOf(base));
                if (nucleotide == null) {
                    JOptionPane.showMessageDialog(this, "Invalid base in sequence: " + base);
                    return;
                }
                totalMass += nucleotide.getBaseMassWithSugar() + MassConstants.getSugarMass();
            }
    
            totalMass += totalLinkageMass;
    
            if (typeComboBox.getSelectedItem().equals("RNA")) {
                totalMass += MassConstants.getOxygenMass() * sequence.length();
            }
    
            switch (fivePrimeComboBox.getSelectedItem().toString()) {
                case "OH":
                    break;
                case "Phosphate":
                    totalMass += MassConstants.getPhosphateMass();
                    break;
                case "Triphosphate":
                    totalMass += MassConstants.getPhosphateMass() * 3;
                    break;
            }
    
            switch (threePrimeComboBox.getSelectedItem().toString()) {
                case "OH":
                    totalMass += MassConstants.getWaterMass();
                    break;
                case "Phosphate":
                    totalMass += MassConstants.getWaterMass() + MassConstants.getPhosphateMass();
                    break;
                case "Cyclic Phosphate":
                    totalMass += MassConstants.getPhosphateMass();
                    break;
            }

    
            displayMzResults(sequence, totalMass);
    
        } else if (methodComboBox.getSelectedItem().equals("CID Fragments")) {
            displayCIDResults(sequence, nucleotideMap);
        }
    }
    
    private void displayMzResults(String sequence, double totalMass) {
        StringBuilder result = new StringBuilder("Sequence: " + sequence + "\n");
        result.append("m/z values:\n");
    
        double protonMass = MassConstants.getProtonMass();
        boolean isPositiveMode = modeComboBox.getSelectedItem().equals("Positive Mode");
        double chargeSign = isPositiveMode ? 1 : -1;
    
        int charge = 1;
        while (true) {
            // プロトン質量を考慮したm/z計算
            double mz = (totalMass + (chargeSign * charge * protonMass)) / charge;
            result.append(String.format("z = %2d: %10.4f\n", charge * (int) chargeSign, mz));
    
            if (mz < 250) { // 条件に基づいて終了
                break;
            }
            charge++;
        }
    
        JFrame resultsFrame = new JFrame("Calculation Results");
        resultsFrame.setSize(400, 300);
        resultsFrame.setLocationRelativeTo(this);
    
        JTextArea resultsTextArea = new JTextArea(result.toString());
        resultsTextArea.setEditable(false);
        resultsFrame.add(new JScrollPane(resultsTextArea));
    
        resultsFrame.setVisible(true);
    }
    
    
    private void displayCIDResults(String sequence, Map<String, Nucleotide> nucleotideMap) {
        StringBuilder result = new StringBuilder("CID Fragmentation (MS2) Results:\n");
        result.append(String.format("%-5s %-5s %-8s %-8s %-8s %-8s\n", "No.", "z", "a-ion", "w-ion", "c-ion", "y-ion"));
    
        double fivePrimeTerminalMass = switch (fivePrimeComboBox.getSelectedItem().toString()) {
            case "Phosphate" -> MassConstants.getPhosphateMass();
            case "Triphosphate" -> MassConstants.getPhosphateMass() * 3;
            default -> 0.0;
        };
    
        double threePrimeTerminalMass = switch (threePrimeComboBox.getSelectedItem().toString()) {
            case "Phosphate" -> MassConstants.getWaterMass() + MassConstants.getPhosphateMass();
            case "Cyclic Phosphate" -> MassConstants.getPhosphateMass();
            default -> MassConstants.getWaterMass();
        };
    
        double protonMass = MassConstants.getProtonMass();
        double chargeSign = modeComboBox.getSelectedItem().equals("Positive Mode") ? 1 : -1;
    
        for (int i = 1; i < sequence.length(); i++) {
            String nFragment = sequence.substring(0, i);
            double aIonMass = calculateFragmentMass(nFragment, nucleotideMap) + fivePrimeTerminalMass;
            double cIonMass = aIonMass + MassConstants.getPhosphateMass();
    
            String cFragment = sequence.substring(sequence.length() - i);
            double yIonMass = calculateFragmentMass(cFragment, nucleotideMap) + threePrimeTerminalMass;
            double wIonMass = yIonMass + MassConstants.getPhosphateMass();
    
            if (i > 1) {
                double additionalMass = (i - 1) * MassConstants.getPhosphateMass();
                aIonMass += additionalMass;
                cIonMass += additionalMass;
                yIonMass += additionalMass;
                wIonMass += additionalMass;
            }
    
            boolean firstCharge = true;
            int charge = 1;
            while (true) {
                double aIonMz = (aIonMass + chargeSign * protonMass * charge) / charge;
                double wIonMz = (wIonMass + chargeSign * protonMass * charge) / charge;
                double cIonMz = (cIonMass + chargeSign * protonMass * charge) / charge;
                double yIonMz = (yIonMass + chargeSign * protonMass * charge) / charge;
    
                if (aIonMz < 250 && wIonMz < 250 && cIonMz < 250 && yIonMz < 250) {
                    break;
                }
    
                result.append(String.format("%-5s %-5d %-8.4f %-8.4f %-8.4f %-8.4f\n",
                        firstCharge ? i : "", charge, aIonMz, wIonMz, cIonMz, yIonMz));
    
                firstCharge = false;
                charge++;
            }
        }
    
        JFrame resultsFrame = new JFrame("CID Fragmentation Results");
        resultsFrame.setSize(800, 600);
        resultsFrame.setLocationRelativeTo(this);
    
        JTextArea resultsTextArea = new JTextArea(result.toString());
        resultsTextArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        resultsTextArea.setEditable(false);
        resultsFrame.add(new JScrollPane(resultsTextArea));
    
        resultsFrame.setVisible(true);
    }
    
    private double calculateFragmentMass(String fragment, Map<String, Nucleotide> nucleotideMap) {
        double mass = 0.0;
        for (char base : fragment.toCharArray()) {
            Nucleotide nucleotide = nucleotideMap.get(String.valueOf(base));
            if (nucleotide == null) {
                JOptionPane.showMessageDialog(this, "Invalid base in fragment: " + base);
                return 0.0;
            }
            mass += nucleotide.getBaseMassWithSugar() + MassConstants.getSugarMass();
        }
    
        if (typeComboBox.getSelectedItem().equals("RNA")) {
            mass += MassConstants.getOxygenMass() * fragment.length();
        }
    
        return mass;
    }
    
}


class MzToMassApp extends JFrame {
    private JTextField mzInputField, chargeInputField, referenceSequenceField;
    private JTextArea resultArea;
    private JComboBox<String> typeComboBox, modeComboBox;
    private JButton predictButton;
    private Highlighter.HighlightPainter highlightPainter = new DefaultHighlighter.DefaultHighlightPainter(Color.GREEN);
    private Object currentHighlight;

    private final Map<Character, Double> monoisotopicMasses = new HashMap<>();
    private double protonMass, waterMass, phosphateMass, sugarMass, oxygenMass;

    public MzToMassApp() {
        setTitle("m/z to Sequence Predictor");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        initializeMasses();
        initializeUI();
    }

    private void initializeMasses() {
        // Initialize masses using MassConstants
        protonMass = MassConstants.getProtonMass();
        waterMass = MassConstants.getWaterMass();
        phosphateMass = MassConstants.getPhosphateMass();
        sugarMass = MassConstants.getSugarMass();
        oxygenMass = MassConstants.getOxygenMass();

        // Initialize nucleotide masses using NucleotideRegistry
        List<Nucleotide> nucleotides = NucleotideRegistry.getMonoIsotopicNucleotides();
        for (Nucleotide nucleotide : nucleotides) {
            monoisotopicMasses.put(nucleotide.getSymbol().charAt(0), nucleotide.getBaseMassWithSugar());
        }
    }

    private JComboBox<String> createComboBox(String[] items) {
        JComboBox<String> comboBox = new JComboBox<>(items);
        comboBox.setPreferredSize(new Dimension(180, 20));
        return comboBox;
    }

    
   
    
    private void initializeUI() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));

        JPanel inputPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        predictButton = new JButton("Predict");
        predictButton.setPreferredSize(new Dimension(60, 20));
        inputPanel.add(predictButton, gbc);
        gbc.gridwidth = 1;

        gbc.gridx = 0;
        gbc.gridy = 1;
        inputPanel.add(new JLabel("m/z value:"), gbc);

        gbc.gridx = 1;
        mzInputField = new JTextField(10);
        mzInputField.setPreferredSize(new Dimension(100, 20));
        inputPanel.add(mzInputField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        inputPanel.add(new JLabel("Charge:"), gbc);

        gbc.gridx = 1;
        chargeInputField = new JTextField(10);
        chargeInputField.setPreferredSize(new Dimension(100, 20));
        inputPanel.add(chargeInputField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        inputPanel.add(new JLabel("Type:"), gbc);

        gbc.gridx = 1;
        typeComboBox = createComboBox(new String[]{"RNA", "DNA"});
        typeComboBox.setPreferredSize(new Dimension(100, 20));
        inputPanel.add(typeComboBox, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        inputPanel.add(new JLabel("Mode:"), gbc);

        gbc.gridx = 1;
        modeComboBox = createComboBox(new String[]{"Negative Mode", "Positive Mode"});
        modeComboBox.setPreferredSize(new Dimension(100, 20));
        inputPanel.add(modeComboBox, gbc);

        gbc.gridx = 0;
        gbc.gridy = 5;
        inputPanel.add(new JLabel("Reference Seq:"), gbc);

        gbc.gridx = 1;
        referenceSequenceField = new JTextField(10);
        referenceSequenceField.setPreferredSize(new Dimension(120, 20));
        inputPanel.add(referenceSequenceField, gbc);

        resultArea = new JTextArea();
        resultArea.setEditable(false);
        resultArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JScrollPane scrollPane = new JScrollPane(resultArea);
        scrollPane.setPreferredSize(new Dimension(500, 400));

        mainPanel.add(inputPanel, BorderLayout.WEST);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        add(mainPanel);

        predictButton.addActionListener(e -> predictSequence());

        resultArea.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 1) {
                    highlightSelectedLine(e);
                } else if (e.getClickCount() == 2) {
                    showCIDInfo(e);
                }
            }
        });
    }

    private void highlightSelectedLine(MouseEvent e) {
        try {
            int offset = resultArea.viewToModel2D(e.getPoint());
            int line = resultArea.getLineOfOffset(offset);
            int lineStart = resultArea.getLineStartOffset(line);
            int lineEnd = resultArea.getLineEndOffset(line);

            if (currentHighlight != null) {
                resultArea.getHighlighter().removeHighlight(currentHighlight);
            }
            currentHighlight = resultArea.getHighlighter().addHighlight(lineStart, lineEnd, highlightPainter);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void showCIDInfo(MouseEvent e) {
        try {
            int offset = resultArea.viewToModel2D(e.getPoint());
            int line = resultArea.getLineOfOffset(offset);
            int lineStart = resultArea.getLineStartOffset(line);
            int lineEnd = resultArea.getLineEndOffset(line);
            String selectedLine = resultArea.getText(lineStart, lineEnd - lineStart).trim();

            if (!selectedLine.isEmpty()) {
                showCIDWindow(selectedLine);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void showCIDWindow(String sequence) {
        JFrame cidFrame = new JFrame("CID Results for Selected Sequence");
        cidFrame.setSize(800, 600);
        cidFrame.setLocationRelativeTo(this);

        JTextArea cidTextArea = new JTextArea();
        cidTextArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        cidTextArea.setEditable(false);

        String cidResults = calculateCID(sequence);
        cidTextArea.setText(cidResults);

        cidFrame.add(new JScrollPane(cidTextArea));
        cidFrame.setVisible(true);
    }

    private String calculateCID(String fragment) {
        StringBuilder result = new StringBuilder("CID Fragmentation (MS2) Results:\n");
        result.append(String.format("%-5s %-5s %-8s %-8s %-8s %-8s\n", "No.", "z", "a-ion", "w-ion", "c-ion", "y-ion"));

        String regex = "^(HO-|ppp-|p-)(.*?)(-OH|-p|>p)\\s*\\(.*\\)$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(fragment);

        if (matcher.find()) {
            String fivePrimePart = matcher.group(1);
            String extractedSequence = matcher.group(2);
            String threePrimePart = matcher.group(3);
            double chargeSign = modeComboBox.getSelectedItem().equals("Positive Mode") ? 1 : -1;

            double fivePrimeTerminalMass = switch (fivePrimePart) {
                case "HO-" -> 0.0;
                case "ppp-" -> 3 * phosphateMass;
                case "p-" -> phosphateMass;
                default -> 0.0;
            };

            double threePrimeTerminalMass = switch (threePrimePart) {
                case "-OH" -> waterMass;
                case "-p" -> phosphateMass + waterMass;
                case ">p" -> phosphateMass;
                default -> 0.0;
            };

            for (int i = 1; i < extractedSequence.length(); i++) {
                String nFragment = extractedSequence.substring(0, i);
                double aIonMass = calculateFragmentMass(nFragment) + fivePrimeTerminalMass;
                double cIonMass = aIonMass + phosphateMass;

                String cFragment = extractedSequence.substring(extractedSequence.length() - i);
                double yIonMass = calculateFragmentMass(cFragment) + threePrimeTerminalMass;
                double wIonMass = yIonMass + phosphateMass;

                boolean firstCharge = true;
                int charge = 1;
                while (true) {
                    double aIonMz = (aIonMass + chargeSign * protonMass * charge) / charge;
                    double wIonMz = (wIonMass + chargeSign * protonMass * charge) / charge;
                    double cIonMz = (cIonMass + chargeSign * protonMass * charge) / charge;
                    double yIonMz = (yIonMass + chargeSign * protonMass * charge) / charge;

                    if (aIonMz < 250 && wIonMz < 250 && cIonMz < 250 && yIonMz < 250) {
                        break;
                    }

                    result.append(String.format("%-5s %-5d %-8.4f %-8.4f %-8.4f %-8.4f\n",
                            firstCharge ? i : "",
                            charge,
                            aIonMz,
                            wIonMz,
                            cIonMz,
                            yIonMz));

                    firstCharge = false;
                    charge++;
                }
            }
        } else {
            result.append("No matching sequence found between the specified markers.\n");
        }

        return result.toString();
    }

    private double calculateFragmentMass(String sequence) {
        double mass = 0.0;
        for (char base : sequence.toCharArray()) {
            mass += monoisotopicMasses.getOrDefault(base, 0.0) + sugarMass;
        }
        if (typeComboBox.getSelectedItem().equals("RNA")) {
            mass += oxygenMass * sequence.length();
        }
        return mass;
    }
    
    private void highlightMatches() {
        String referenceSequence = referenceSequenceField.getText().trim().toUpperCase();
        if (referenceSequence.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a reference sequence.");
            return;
        }
    
        try {
            Highlighter highlighter = resultArea.getHighlighter();
            highlighter.removeAllHighlights(); 
    
            String fullText = resultArea.getText();
            String[] lines = fullText.split("\n"); 
    
            for (String line : lines) {
                String cleanLine = extractBaseSequence(line);
    
                if (!cleanLine.isEmpty() && referenceSequence.contains(cleanLine)) {
                    int start = fullText.indexOf(line) + line.indexOf(cleanLine);
                    int end = start + cleanLine.length();
    
                    highlighter.addHighlight(start, end, highlightPainter);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private String extractBaseSequence(String line) {
        Matcher matcher = Pattern.compile("^(HO-|ppp-|p-)?([AUGCT]+)(-OH|-p|>p)?.*\\(m/z:").matcher(line);
        if (matcher.find()) {
            return matcher.group(2).toUpperCase(); 
        }
        return ""; 
    }
    
    


    private void predictSequence() {
        resultArea.setText(""); 
        String mzText = mzInputField.getText().trim();
        String chargeText = chargeInputField.getText().trim();
        String referenceSequence = referenceSequenceField.getText().trim().toUpperCase(); 

        
        if (mzText.isEmpty() || chargeText.isEmpty()) {
            resultArea.setText("Please enter both m/z and charge values.");
            return;
        }
        
        try {
            double mzValue = Double.parseDouble(mzText);
            int charge = Integer.parseInt(chargeText);
            if (charge == 0) {
                resultArea.setText("Charge cannot be zero.");
                return;
            }
        
            double chargeSign = modeComboBox.getSelectedItem().equals("Positive Mode") ? -1 : 1;
            double neutralMass = (mzValue * Math.abs(charge)) + (chargeSign * protonMass * Math.abs(charge));
            resultArea.append("Calculated mass: " + neutralMass + " Da\n");
        
            double[] terminalMassAdjustments = {
                waterMass,  // 5'OH, 3'OH
                waterMass + phosphateMass,  // 5'OH, 3'p or 5'p, 3'OH
                phosphateMass,  // 5'OH, 3'cyclicP
                waterMass + phosphateMass + phosphateMass,  // 5'p, 3'p
                phosphateMass + phosphateMass,  // 5'p, 3'cyclicP
                waterMass + phosphateMass + phosphateMass + phosphateMass,  // 5'ppp 3'OH
                waterMass + phosphateMass + phosphateMass + phosphateMass + phosphateMass,  // 5'ppp 3'p
                phosphateMass + phosphateMass + phosphateMass + phosphateMass  // 5'ppp 3'cyclicP
            };
        
            String[] terminalStates = {
                "HO-, -OH",
                "HO-, -p",
                "HO-, >p",
                "p-, -p",
                "p-, >p",
                "ppp-, -OH",
                "ppp-, -p",
                "ppp-, >p"
            };
        
            double minBaseMass = monoisotopicMasses.get('C'); // min base
            double maxBaseMass = monoisotopicMasses.get('G'); // max base
        
            for (int i = 0; i < terminalMassAdjustments.length; i++) {
                String terminalState = terminalStates[i];
                String[] parts = terminalState.split(", ");
                String fivePrimePart = parts[0];
                String threePrimePart = parts[1];
        
                double fivePrimeTerminalMass = switch (fivePrimePart) {
                    case "HO-" -> 0.0;
                    case "ppp-" -> 3 * phosphateMass;
                    case "p-" -> phosphateMass;
                    default -> 0.0;
                };
        
                double threePrimeTerminalMass = switch (threePrimePart) {
                    case "-OH" -> waterMass;
                    case "-p" -> phosphateMass + waterMass;
                    case ">p" -> phosphateMass;
                    default -> 0.0;
                };
        
                double adjustedMass = neutralMass - fivePrimeTerminalMass - threePrimeTerminalMass;
        
                int minLength = estimateSequenceLength(adjustedMass, 'C', false);
                int maxLength = estimateSequenceLength(adjustedMass, 'G', true);
        
                for (int length = minLength; length <= maxLength; length++) {
                    double adjustedSugarMass = sugarMass;
                    if (typeComboBox.getSelectedItem().equals("RNA")) {
                        adjustedSugarMass += oxygenMass;  
                    }
        
                    double lengthAdjustedMass = adjustedMass - (length * adjustedSugarMass);
                    if (length > 1) {
                        lengthAdjustedMass -= (length - 1) * phosphateMass;
                    }
        
                    if (lengthAdjustedMass < length * minBaseMass || lengthAdjustedMass > length * maxBaseMass) {
                        continue;
                    }
        
                    findMatchingCombination(lengthAdjustedMass, length, terminalState, mzValue, charge);
                }
            }

            if (!referenceSequence.isEmpty()) {
                highlightMatches();
            }
        } catch (NumberFormatException ex) {
            resultArea.setText("Invalid m/z or charge value entered. Please enter numeric values.");
        }
    }
    
    private void findMatchingCombination(double lengthAdjustedMass, int length, String terminalState, double mzValue, int charge) {
        List<Nucleotide> nucleotides = NucleotideRegistry.getMonoIsotopicNucleotides();
        List<Character> bases = new ArrayList<>();
        for (Nucleotide nucleotide : nucleotides) {
            bases.add(nucleotide.getSymbol().charAt(0));
        }
        findCombination(new ArrayList<>(), bases, length, lengthAdjustedMass, terminalState, mzValue, charge);
    }
    
    private void findCombination(List<Character> currentCombination, List<Character> bases, int length, double targetMass, String terminalState, double mzValue, int charge) {
        if (currentCombination.size() == length) {
            double totalMass = currentCombination.stream()
                                                 .mapToDouble(base -> monoisotopicMasses.get(base))
                                                 .sum();
            if (Math.abs(totalMass - targetMass) <= 0.01) {
                calculateMzForSequenceWithTerminals(currentCombination, charge, terminalState.split(", ")[0], terminalState.split(", ")[1]);
            }
            return;
        }
        for (Character base : bases) {
            currentCombination.add(base);
            findCombination(currentCombination, bases, length, targetMass, terminalState, mzValue, charge);
            currentCombination.remove(currentCombination.size() - 1);
        }
    }
    

    
    
    private void calculateMzForSequenceWithTerminals(List<Character> sequence, int charge, String fivePrimePart, String threePrimePart) {
        double chargeSign = modeComboBox.getSelectedItem().equals("Positive Mode") ? 1 : -1;
        double baseMass = sequence.stream()
                                  .mapToDouble(base -> monoisotopicMasses.get(base))
                                  .sum();
    
        double adjustedSugarMass = sugarMass;
        if (typeComboBox.getSelectedItem().equals("RNA")) {
            adjustedSugarMass += oxygenMass; 
        }
    
        int length = sequence.size();
        double sequenceMass = baseMass + (length * adjustedSugarMass) + ((length - 1) * phosphateMass);
    
        double fivePrimeTerminalMass = switch (fivePrimePart) {
            case "HO-" -> 0.0;
            case "ppp-" -> 3 * phosphateMass;
            case "p-" -> phosphateMass;
            default -> 0.0;
        };
    
        double threePrimeTerminalMass = switch (threePrimePart) {
            case "-OH" -> waterMass;
            case "-p" -> phosphateMass + waterMass;
            case ">p" -> phosphateMass;
            default -> 0.0;
        };
    
        double totalMass = sequenceMass + fivePrimeTerminalMass + threePrimeTerminalMass;
    
        double predictedMz = (totalMass - (charge * protonMass)) / Math.abs(charge);
    
    
        resultArea.append(String.format("%s%s%s (m/z: %.10f (z = %d))\n", 
                                fivePrimePart, 
                                sequence.stream().map(String::valueOf).reduce((a, b) -> a + b).orElse(""), 
                                threePrimePart, 
                                predictedMz, 
                                Math.round(charge * chargeSign)));

    }
    

    private int estimateSequenceLength(double mass, char base, boolean includeNegative) {
        double adjustedSugarMass = sugarMass;
        if (typeComboBox.getSelectedItem().equals("RNA")) {
            adjustedSugarMass += oxygenMass;  
        }

        double remainingMass = mass - waterMass; 

        if (!includeNegative) {
            remainingMass -= 4 * phosphateMass;
        }

        double basePlusSugar = monoisotopicMasses.get(base) + adjustedSugarMass;
        double basePlusSugarAndPhosphate = basePlusSugar + phosphateMass;
        int length = 1;  

        remainingMass -= basePlusSugar;
        while ((includeNegative && remainingMass >= -basePlusSugarAndPhosphate) || (!includeNegative && remainingMass >= basePlusSugarAndPhosphate)) {
            remainingMass -= basePlusSugarAndPhosphate;
            length++;
        }

        return length;
    }
}


class DigestionApp extends JFrame {
    private JTextField sequenceField;
    private JTextField minLengthField;
    private JComboBox<String> rnaseComboBox;
    private JList<String> fragmentList;
    private DefaultListModel<String> listModel;
    private JComboBox<String> fivePrimeComboBox;
    private JComboBox<String> threePrimeComboBox;
    private JComboBox<String> rnaDnaComboBox;
    private JComboBox<String> massTypeComboBox;
    private JComboBox<String> ionModeComboBox;
    private JComboBox<String> internalThreePrimeComboBox;

    private final Map<Character, Double> monoisotopicMasses = new HashMap<>();
    private final Map<Character, Double> averageMasses = new HashMap<>();

    public DigestionApp(JFrame parentFrame) {
        setTitle("Digestion Tool");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        initializeMasses();
        initializeUI();
    }

    private void initializeMasses() {
        List<Nucleotide> monoNucleotides = NucleotideRegistry.getMonoIsotopicNucleotides();
        for (Nucleotide nucleotide : monoNucleotides) {
            monoisotopicMasses.put(nucleotide.getSymbol().charAt(0), nucleotide.getBaseMassWithSugar());
        }

        List<Nucleotide> avgNucleotides = NucleotideRegistry.getAverageMassNucleotides();
        for (Nucleotide nucleotide : avgNucleotides) {
            averageMasses.put(nucleotide.getSymbol().charAt(0), nucleotide.getBaseMassWithSugar());
        }
    }

    private JComboBox<String> createComboBox(String[] items) {
        JComboBox<String> comboBox = new JComboBox<>(items);
        comboBox.setPreferredSize(new Dimension(180, 20));
        return comboBox;
    }

    private JPanel createLabeledPanel(String labelText, JComponent component) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        JLabel label = new JLabel(labelText);
        label.setPreferredSize(new Dimension(100, label.getPreferredSize().height));
        panel.add(label);
        panel.add(component);
        return panel;
    }

    private void initializeUI() {
        setLayout(new BorderLayout(10, 10));
    
        JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        JLabel sequenceLabel = new JLabel("Sequence:");
        sequenceField = new JTextField(15);
        JLabel minLengthLabel = new JLabel("Min Fragment Length:");
        minLengthField = new JTextField(3);
        JButton digestButton = new JButton("Digest");
    
        inputPanel.add(sequenceLabel);
        inputPanel.add(sequenceField);
        inputPanel.add(minLengthLabel);
        inputPanel.add(minLengthField);
        inputPanel.add(digestButton);
        add(inputPanel, BorderLayout.NORTH);
    
        JPanel optionsPanel = new JPanel();
        optionsPanel.setLayout(new BoxLayout(optionsPanel, BoxLayout.Y_AXIS));
    
        rnaDnaComboBox = createComboBox(new String[]{"RNA", "DNA"});
        massTypeComboBox = createComboBox(new String[]{"Monoisotopic Mass", "Average Mass"});
        ionModeComboBox = createComboBox(new String[]{"Positive Mode", "Negative Mode"});
        fivePrimeComboBox = createComboBox(new String[]{"OH", "Phosphate", "Triphosphate"});
        threePrimeComboBox = createComboBox(new String[]{"OH", "Phosphate", "Cyclic Phosphate"});
        internalThreePrimeComboBox = createComboBox(new String[]{"Phosphate", "Cyclic Phosphate"});
        rnaseComboBox = createComboBox(new String[]{"RNase T1", "RNase A", "RNase T1 + BAP", "RNase A + BAP", "Nuclease P1 + BAP"});
    
        optionsPanel.add(createLabeledPanel("Nucleic Acid Type:", rnaDnaComboBox));
        optionsPanel.add(createLabeledPanel("Mass Type:", massTypeComboBox));
        optionsPanel.add(createLabeledPanel("Ion Mode:", ionModeComboBox));
        optionsPanel.add(createLabeledPanel("5' End:", fivePrimeComboBox));
        optionsPanel.add(createLabeledPanel("3' End:", threePrimeComboBox));
        optionsPanel.add(createLabeledPanel("Internal 3' End:", internalThreePrimeComboBox));
        optionsPanel.add(createLabeledPanel("Select RNase Type:", rnaseComboBox));
    
        JPanel mzRangePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        JLabel mzMinLabel = new JLabel("m/z Min:");
        JTextField mzMinField = new JTextField(5);
        JLabel mzMaxLabel = new JLabel("m/z Max:");
        JTextField mzMaxField = new JTextField(5);
        mzRangePanel.add(mzMinLabel);
        mzRangePanel.add(mzMinField);
        mzRangePanel.add(mzMaxLabel);
        mzRangePanel.add(mzMaxField);
        optionsPanel.add(mzRangePanel);
    
        add(optionsPanel, BorderLayout.WEST);
    
        listModel = new DefaultListModel<>();
        fragmentList = new JList<>(listModel);
        fragmentList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    
        fragmentList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) { 
                    String selectedFragment = fragmentList.getSelectedValue();
                    if (selectedFragment != null) {
                        showCIDResults(selectedFragment);
                    }
                }
            }
        });
    
        JScrollPane listScrollPane = new JScrollPane(fragmentList);
        add(listScrollPane, BorderLayout.CENTER);
    
        digestButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String sequence = sequenceField.getText().toUpperCase().trim();
                if (sequence.isEmpty()) {
                    JOptionPane.showMessageDialog(DigestionApp.this, "Please enter a nucleic acid sequence.");
                    return;
                }
    
                double protonMass = MassConstants.getProtonMass();
                double sugarMass = MassConstants.getSugarMass();
                double phosphateMass = MassConstants.getPhosphateMass();
                double waterMass = MassConstants.getWaterMass();
    
                Map<Character, Double> massMap = massTypeComboBox.getSelectedItem().equals("Monoisotopic Mass")
                        ? monoisotopicMasses
                        : averageMasses;
    
                int minLength = 0;
                String minLengthText = minLengthField.getText().trim();
                if (!minLengthText.isEmpty()) {
                    try {
                        minLength = Integer.parseInt(minLengthText);
                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(DigestionApp.this, "Please enter a valid minimum fragment length.");
                        return;
                    }
                }

                double mzMin;
                double mzMax;
                boolean mzRangeSpecified = !mzMinField.getText().trim().isEmpty() && !mzMaxField.getText().trim().isEmpty();
                if (mzRangeSpecified) {
                    try {
                        mzMin = Double.parseDouble(mzMinField.getText().trim());
                        mzMax = Double.parseDouble(mzMaxField.getText().trim());
                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(DigestionApp.this, "Please enter valid m/z range values.");
                        return;
                    }
                } else {
                    mzMin = 0.0;
                    mzMax = 99999.0;
                }
    
                String rnaseType = (String) rnaseComboBox.getSelectedItem();
                List<String> fragments = digestSequence(sequence, rnaseType);
    
                listModel.clear();

                double chargeSign = ionModeComboBox.getSelectedItem().equals("Positive Mode") ? 1 : -1;

                // Add extra mass if RNA is selected
                double extraMass = 0.0;
                if (rnaDnaComboBox.getSelectedItem().equals("RNA")) {
                    extraMass = massTypeComboBox.getSelectedItem().equals("Monoisotopic Mass") ? 15.995 : 15.999;
                }                                                   

                // Get the 5' and 3' end mass additions
                double fivePrimeMass = switch ((String) fivePrimeComboBox.getSelectedItem()) {
                    case "OH" -> 0.0;
                    case "Phosphate" -> phosphateMass;
                    case "Triphosphate" -> 3 * phosphateMass;
                    default -> 0.0;
                };

                String fivePrimeDisplay = switch ((String) fivePrimeComboBox.getSelectedItem()) {
                    case "OH" -> "HO-";
                    case "Phosphate" -> "p-";
                    case "Triphosphate" -> "ppp-";
                    default -> "";
                };

                double threePrimeMass = switch ((String) threePrimeComboBox.getSelectedItem()) {
                    case "OH" -> waterMass; // Mass of water (H2O)
                    case "Phosphate" -> phosphateMass + waterMass; // Phosphate + H2O
                    case "Cyclic Phosphate" -> phosphateMass; // Cyclic phosphate
                    default -> 0.0;
                };

                String threePrimeDisplay = switch ((String) threePrimeComboBox.getSelectedItem()) {
                    case "OH" -> "-OH";
                    case "Phosphate" -> "-p";
                    case "Cyclic Phosphate" -> ">p";
                    default -> "";
                };

                String internalThreePrimeSelection = (String) internalThreePrimeComboBox.getSelectedItem();
                double internalThreePrimeMass = switch (internalThreePrimeSelection) {
                    case "Phosphate" -> phosphateMass + waterMass;
                    case "Cyclic Phosphate" -> phosphateMass;
                    default -> 0.0;
                };
    
                for (int i = 0; i < fragments.size(); i++) {
                    String fragment = fragments.get(i);
                    if (minLength == 0 || fragment.length() >= minLength) {
                        double fragmentMass = calculateFragmentMass(fragment, massMap, sugarMass);
                        int OxygenCount = fragment.length();
                        fragmentMass += extraMass * OxygenCount; // Add extra mass for RNA
                
                        // Add PhosphateMass for each linkage in the fragment (length - 1)
                        int phosphateCount = fragment.length() - 1;
                        fragmentMass += phosphateMass * phosphateCount;

                        
                
                        // Only add internal 3' end mass if the RNase type is not T1 + BAP or A + BAP
                        if (i != fragments.size() - 1) {
                            if (rnaseType.equals("RNase T1 + BAP") || rnaseType.equals("RNase A + BAP")|| rnaseType.equals("Nuclease P1 + BAP")) {
                                fragmentMass += waterMass; // Add WaterMass for BAP-treated fragments
                            } else {
                                fragmentMass += internalThreePrimeMass;
                            }
                        }
                
                        String displayFragment;
                        if (rnaseType.equals("Nuclease P1 + BAP")) {
                            if (i == fragments.size() - 1) {
                                fragmentMass += waterMass; 
                            }
                            displayFragment = fragment;
                        } else {
                            String fivePrimePart = "";
                            String threePrimePart = "";
                        
                            // Add 5' modification only to the first fragment
                            if (i == 0) {
                                if (rnaseType.equals("RNase T1 + BAP") || rnaseType.equals("RNase A + BAP")) {
                                    fivePrimePart = "HO-";
                                } else {
                                    fragmentMass += fivePrimeMass;
                                    fivePrimePart = fivePrimeDisplay;
                                }
                            } else {
                                fivePrimePart = "HO-";
                            }
                        
                            // For internal fragments, set internal 3' end to "-OH" if RNase T1 + BAP or A + BAP is selected
                            if (i != fragments.size() - 1) {
                                threePrimePart = (rnaseType.equals("RNase T1 + BAP") || rnaseType.equals("RNase A + BAP")) ? "-OH" : (internalThreePrimeSelection.equals("Phosphate") ? "-p" : ">p");
                            } else {
                                fragmentMass += threePrimeMass;
                                threePrimePart = threePrimeDisplay;
                            }
                        
                            displayFragment = fivePrimePart + fragment + threePrimePart;
                        }
                        
                        for (int charge = 1; charge <= 3; charge++) {
                            double mz = (fragmentMass + chargeSign * protonMass * charge) / charge;
                            if (mz >= mzMin && mz <= mzMax) {
                                listModel.addElement(displayFragment + " (m/z: " + String.format("%.4f", mz) + ", z = " + Math.round(charge * chargeSign) + ")");
                                break;
                            }
                        }
                        
                    }
                }
            }
        });
    }
    
    
    private List<String> remove3Phosphates(List<String> fragments) {
        List<String> processedFragments = new ArrayList<>();
        for (String fragment : fragments) {
            if (fragment.endsWith("-p") || fragment.endsWith(">p")) {
                processedFragments.add(fragment.substring(0, fragment.length() - 2));
            } else {
                processedFragments.add(fragment); 
            }
        }
        return processedFragments;
    }
    
    
    
    private List<String> digestSequence(String sequence, String rnaseType) {
        List<String> fragments = new ArrayList<>();
    
        if (rnaseType.equals("Nuclease P1 + BAP")) {
            Set<Character> allBases = NucleotideRegistry.getMonoIsotopicNucleotides()
                    .stream()
                    .map(nucleotide -> nucleotide.getSymbol().charAt(0))
                    .collect(Collectors.toSet());
            String cleavagePattern = allBases.stream()
                    .map(String::valueOf)
                    .collect(Collectors.joining("|"));
    
            fragments = splitSequence(sequence, cleavagePattern);
            fragments = remove3Phosphates(fragments); 
            return fragments;
        }
    
        Set<Character> cleavageBases = new HashSet<>();
        for (Nucleotide nucleotide : NucleotideRegistry.getMonoIsotopicNucleotides()) {
            if (nucleotide.getCleavableBy().contains(rnaseType.replace(" + BAP", ""))) {
                cleavageBases.add(nucleotide.getSymbol().charAt(0));
            }
        }
    
        String cleavagePattern = cleavageBases.stream()
                .map(String::valueOf)
                .collect(Collectors.joining("|"));
    
        fragments = splitSequence(sequence, cleavagePattern);
    
        if (rnaseType.endsWith("+ BAP")) {
            fragments = remove3Phosphates(fragments);
        }
    
        return fragments;
    }
    
    
    
    

    private List<String> splitSequence(String sequence, String regex) {
        List<String> fragments = new ArrayList<>();
        String[] parts = sequence.split("(?<=" + regex + ")"); 
        for (String part : parts) {
            if (!part.isEmpty()) {
                fragments.add(part);
            }
        }
        return fragments;
    }
    
    

    private double calculateFragmentMass(String fragment, Map<Character, Double> massMap, double sugarMass) {
        double mass = 0.0;
        for (char base : fragment.toCharArray()) {
            Double baseMass = massMap.get(base);
            if (baseMass == null) {
                JOptionPane.showMessageDialog(this, "Invalid base in fragment: " + base);
                return 0.0;
            }
            mass += baseMass + sugarMass;
        }
        return mass;
    }

    private void showCIDResults(String fragment) {
        StringBuilder result = new StringBuilder("CID Fragmentation (MS2) Results:\n");
        result.append(String.format("%-5s %-5s %-8s %-8s %-8s %-8s\n", "No.", "z", "a-ion", "w-ion", "c-ion", "y-ion"));
    
        String regex = "^(HO-|ppp-|p-)(.*?)(-OH|-p|>p)\\s*\\(.*\\)$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(fragment);
    
        if (matcher.find()) {
            String fivePrimePart = matcher.group(1);
            String extractedSequence = matcher.group(2);
            String threePrimePart = matcher.group(3);
    
            double protonMass = MassConstants.getProtonMass();
            double phosphateMass = MassConstants.getPhosphateMass();
            double waterMass = MassConstants.getWaterMass();
            double chargeSign = ionModeComboBox.getSelectedItem().equals("Positive Mode") ? 1 : -1;
    
            double fivePrimeTerminalMass = switch (fivePrimePart) {
                case "HO-" -> 0.0;
                case "ppp-" -> 3 * phosphateMass;
                case "p-" -> phosphateMass;
                default -> 0.0;
            };
    
            double threePrimeTerminalMass = switch (threePrimePart) {
                case "-OH" -> waterMass;
                case "-p" -> phosphateMass + waterMass;
                case ">p" -> phosphateMass;
                default -> 0.0;
            };
    
            double oxygenMass = 0.0;
            if (rnaDnaComboBox.getSelectedItem().equals("RNA")) {
                oxygenMass = MassConstants.getMassMode() == MassConstants.MassMode.MONOISOTOPIC ? 15.995 : 15.999;
            }
    
            for (int i = 1; i < extractedSequence.length(); i++) {
                String nFragment = extractedSequence.substring(0, i);
                double aIonMass = calculateFragmentMass(nFragment) + fivePrimeTerminalMass + (oxygenMass * i);
                double cIonMass = aIonMass + phosphateMass;
    
                String cFragment = extractedSequence.substring(extractedSequence.length() - i);
                double yIonMass = calculateFragmentMass(cFragment) + threePrimeTerminalMass + (oxygenMass * i);
                double wIonMass = yIonMass + phosphateMass;
    
                if (i > 1) {
                    double additionalMass = (i - 1) * phosphateMass;
                    aIonMass += additionalMass;
                    cIonMass += additionalMass;
                    yIonMass += additionalMass;
                    wIonMass += additionalMass;
                }
    
                boolean firstCharge = true;
                int charge = 1;
                while (true) {
                    double aIonMz = (aIonMass + chargeSign * protonMass * charge) / charge;
                    double wIonMz = (wIonMass + chargeSign * protonMass * charge) / charge;
                    double cIonMz = (cIonMass + chargeSign * protonMass * charge) / charge;
                    double yIonMz = (yIonMass + chargeSign * protonMass * charge) / charge;
    
                    if (aIonMz < 250 && wIonMz < 250 && cIonMz < 250 && yIonMz < 250) {
                        break;
                    }
    
                    result.append(String.format("%-5s %-5d %-8.4f %-8.4f %-8.4f %-8.4f\n",
                            firstCharge ? i : "",
                            charge,
                            aIonMz,
                            wIonMz,
                            cIonMz,
                            yIonMz));
    
                    firstCharge = false;
                    charge++;
                }
            }
        } else {
            result.append("No matching sequence found between the specified markers.\n");
        }
    
        JFrame cidFrame = new JFrame("CID Results");
        cidFrame.setSize(800, 600);
        cidFrame.setLocationRelativeTo(this);
    
        JTextArea cidTextArea = new JTextArea(result.toString());
        cidTextArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        cidTextArea.setEditable(false);
        cidFrame.add(new JScrollPane(cidTextArea));
    
        cidFrame.setVisible(true);
    }
    
    private double calculateFragmentMass(String sequence) {
        double sugarMass = MassConstants.getSugarMass();
        Map<Character, Double> massMap = massTypeComboBox.getSelectedItem().equals("Monoisotopic Mass")
                ? monoisotopicMasses
                : averageMasses;
    
        double mass = 0.0;
        for (char base : sequence.toCharArray()) {
            Double baseMass = massMap.get(base);
            if (baseMass == null) {
                JOptionPane.showMessageDialog(this, "Invalid base in fragment: " + base);
                return 0.0;
            }
            mass += baseMass + sugarMass;
        }
        return mass;
    }

}


class BaseMassApp extends JFrame {
    private JComboBox<String> ionModeComboBox;
    private JComboBox<String> typeComboBox;
    private JTable table;
    private DefaultTableModel model;

    public BaseMassApp() {
        setTitle("Nucleot(s)ide Mass Information");
        setSize(700, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel optionsPanel = new JPanel(new FlowLayout());
        ionModeComboBox = new JComboBox<>(new String[]{"Positive Mode", "Negative Mode"});
        typeComboBox = new JComboBox<>(new String[]{"RNA", "DNA"});

        optionsPanel.add(new JLabel("Ion Mode:"));
        optionsPanel.add(ionModeComboBox);
        optionsPanel.add(new JLabel("Type:"));
        optionsPanel.add(typeComboBox);

        add(optionsPanel, BorderLayout.NORTH);

        String[] columnNames = {"Short Name", "Symbol", "Base", "Sugar + Mod", "N", "Np", "N>p"};
        model = new DefaultTableModel(columnNames, 0);
        table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        updateTableData();

        ionModeComboBox.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                updateTableData();
            }
        });
        typeComboBox.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                updateTableData();
            }
        });
    }

    private void updateTableData() {
        model.setRowCount(0);

        String ionMode = (String) ionModeComboBox.getSelectedItem();
        String type = (String) typeComboBox.getSelectedItem();
        double protonMass = MassConstants.getProtonMass();
        double waterMass = MassConstants.getWaterMass();
        double phospateMass = MassConstants.getWaterMass();


        List<Nucleotide> nucleotides = NucleotideRegistry.getMonoIsotopicNucleotides();

        for (Nucleotide nucleotide : nucleotides) {
            String shortName = nucleotide.getShortName();
            String symbol = nucleotide.getSymbol();
            double baseMass = nucleotide.getBaseMass() + protonMass; 
            double sugarModMass = calculateSugarMod(type) + nucleotide.getSugarMod();
            double nMass = baseMass + sugarModMass;
            double npMass = nMass + phospateMass; 
            double ngtMass = npMass - waterMass; 

            if ("Positive Mode".equals(ionMode)) {
                baseMass += protonMass;
                nMass += protonMass;
                npMass += protonMass;
                ngtMass += protonMass;
            } else {
                baseMass -= protonMass;
                nMass -= protonMass;
                npMass -= protonMass;
                ngtMass -= protonMass;
            }

            model.addRow(new Object[]{shortName, symbol, baseMass, sugarModMass, nMass, npMass, ngtMass});
        }
    }

    private double calculateSugarMod(String type) {
        double sugarBase =  MassConstants.getSugarMass() + MassConstants.getWaterMass() - MassConstants.getProtonMass() ; 
        if ("RNA".equals(type)) {
            sugarBase += MassConstants.getOxygenMass(); 
        }
        return sugarBase;
    }
}

class NucleotideRegistry {
    private static final List<Nucleotide> monoIsotopicList = new ArrayList<>();
    private static final List<Nucleotide> averageMassList = new ArrayList<>();

    static {
        monoIsotopicList.add(new Nucleotide("A", "A", 134.0466701646, 0.0, List.of()));
        monoIsotopicList.add(new Nucleotide("U", "U", 111.0194523551, 0.0, List.of("RNase A")));
        monoIsotopicList.add(new Nucleotide("G", "G", 150.0415847869, 0.0, List.of("RNase T1")));
        monoIsotopicList.add(new Nucleotide("C", "C", 110.0354367721, 0.0, List.of("RNase A")));
        monoIsotopicList.add(new Nucleotide("T", "T", 125.0351024189, 0.0, List.of("RNase A"))); 
        monoIsotopicList.add(new Nucleotide(":", "Am", 134.0466701646, 14.0157, List.of()));
        monoIsotopicList.add(new Nucleotide("J", "Um", 111.0194523551, 14.0157, List.of()));
        monoIsotopicList.add(new Nucleotide("#", "Gm", 150.0415847869, 14.0157, List.of()));
        monoIsotopicList.add(new Nucleotide("B", "Cm", 110.0354367721, 14.0157, List.of()));
        monoIsotopicList.add(new Nucleotide("\"", "m1A", 148.0623, 0.0, List.of()));
        monoIsotopicList.add(new Nucleotide("/", "m2A", 148.0623, 0.0, List.of()));
        monoIsotopicList.add(new Nucleotide("=", "m6A", 148.0623, 0.0, List.of()));
        monoIsotopicList.add(new Nucleotide("K", "m1G", 164.0572 , 0.0, List.of("RNase T1")));
        monoIsotopicList.add(new Nucleotide("L", "m2G", 164.0572 , 0.0, List.of("RNase T1")));
        monoIsotopicList.add(new Nucleotide("7", "m7G", 164.0572 , 0.0, List.of("RNase T1")));
        monoIsotopicList.add(new Nucleotide("'", "m3C", 124.0511, 0.0, List.of("RNase A")));
        monoIsotopicList.add(new Nucleotide("?", "m5C", 124.0511, 0.0, List.of("RNase A")));
        monoIsotopicList.add(new Nucleotide("I", "I", 135.0307, 0.0, List.of()));
        monoIsotopicList.add(new Nucleotide("+", "i6A", 202.1093, 0.0, List.of()));
        monoIsotopicList.add(new Nucleotide("*", "ms2i6A",  248.0970, 0.0, List.of()));
        monoIsotopicList.add(new Nucleotide("`", "io6A", 202.1093, 0.0, List.of()));
        monoIsotopicList.add(new Nucleotide("[", "ms2t6A", 325.0719, 0.0, List.of()));
        monoIsotopicList.add(new Nucleotide("6", "t6A", 279.0842, 0.0, List.of()));
        monoIsotopicList.add(new Nucleotide("e", "ct6A", 261.0736, 0.0, List.of()));
        monoIsotopicList.add(new Nucleotide("E", "m6t6A", 293.0998, 0.0, List.of()));
        monoIsotopicList.add(new Nucleotide("O", "m1I", 149.0463, 0.0, List.of()));
        monoIsotopicList.add(new Nucleotide("P", "Ψ", 111.0194523551, 0.0, List.of("RNase A")));
        monoIsotopicList.add(new Nucleotide("]", "m1Ψ", 125.0351, 0.0, List.of("RNase A")));
        monoIsotopicList.add(new Nucleotide("Z", "Ψm", 111.0194523551, 14.0157, List.of()));
        monoIsotopicList.add(new Nucleotide("D", "D", 113.0351, 0.0, List.of("RNase A")));
        monoIsotopicList.add(new Nucleotide("\\", "m5Um", 168.0409, 14.0157, List.of()));
        monoIsotopicList.add(new Nucleotide("~", "ncm5Um", 168.0409, 14.0157, List.of()));
        monoIsotopicList.add(new Nucleotide(")", "cmnm5Um", 198.0515, 14.0157, List.of()));
        monoIsotopicList.add(new Nucleotide(",", "mchm5U", 199.0355, 0.0, List.of("RNase A")));
        monoIsotopicList.add(new Nucleotide("{", "mnm5U", 154.0617, 0.0, List.of("RNase A")));
        monoIsotopicList.add(new Nucleotide("$", "cmnm5s2U", 214.0286, 0.0, List.of("RNase A")));
        monoIsotopicList.add(new Nucleotide("!", "cmnm5U", 198.0515, 0.0, List.of("RNase A")));
        monoIsotopicList.add(new Nucleotide("1", "mcm5U", 183.0406, 0.0, List.of("RNase A")));
        monoIsotopicList.add(new Nucleotide("2", "s2U",  126.9966, 0.0, List.of("RNase A")));
        monoIsotopicList.add(new Nucleotide("3", "mcm5s2U", 199.0177, 0.0, List.of("RNase A")));
        monoIsotopicList.add(new Nucleotide("4", "s4U", 126.9966, 0.0, List.of("RNase A")));
        monoIsotopicList.add(new Nucleotide("5", "mo5U", 141.0300, 0.0, List.of("RNase A")));
        monoIsotopicList.add(new Nucleotide("V", "cmo5U", 185.0198, 0.0, List.of("RNase A")));
        monoIsotopicList.add(new Nucleotide("F", "m5s2U", 141.0123, 0.0, List.of("RNase A")));
        monoIsotopicList.add(new Nucleotide("S", "mnm5s2U", 170.0388, 0.0, List.of("RNase A")));
        monoIsotopicList.add(new Nucleotide("X", "acp3U", 212.0671, 0.0, List.of("RNase A")));
        monoIsotopicList.add(new Nucleotide("&", "ncm5U", 168.0409, 0.0, List.of("RNase A")));
        monoIsotopicList.add(new Nucleotide("∀", "tm5U", 248.0341, 0.0, List.of("RNase A")));
        monoIsotopicList.add(new Nucleotide("∃", "tm5s2U", 264.0113, 0.0, List.of("RNase A")));
        monoIsotopicList.add(new Nucleotide("R", "m22G", 178.0729, 0.0, List.of("RNase T1")));
        monoIsotopicList.add(new Nucleotide("|", "m22Gm", 178.0729, 14.0157, List.of()));
        monoIsotopicList.add(new Nucleotide("Q", "Q", 276.1097, 0.0, List.of("RNase T1")));
        monoIsotopicList.add(new Nucleotide("8", "manQ", 438.1625, 0.0, List.of("RNase T1")));
        monoIsotopicList.add(new Nucleotide("9", "galQ", 438.1625, 0.0, List.of("RNase T1")));
        monoIsotopicList.add(new Nucleotide("Y", "yW", 375.1417, 0.0, List.of("RNase T1")));
        monoIsotopicList.add(new Nucleotide("W", "oyW", 407.1315, 0.0, List.of("RNase T1")));
        monoIsotopicList.add(new Nucleotide(">", "f5C", 138.0304, 0.0, List.of("RNase A")));
        monoIsotopicList.add(new Nucleotide("%", "s2C", 126.0126, 0.0, List.of("RNase A")));
        monoIsotopicList.add(new Nucleotide("M", "ac4C", 152.0460, 0.0, List.of("RNase A")));
        monoIsotopicList.add(new Nucleotide("ℵ", "ac4Cm", 152.0460, 14.0157, List.of()));
        monoIsotopicList.add(new Nucleotide("°", "f5Cm", 138.0304, 14.0157, List.of()));


        averageMassList.add(new Nucleotide("A", "A", 134.1280, 0.0, List.of()));
        averageMassList.add(new Nucleotide("U", "U", 112.0870, 0.0, List.of("RNase A")));
        averageMassList.add(new Nucleotide("G", "G", 150.1510, 0.0, List.of("RNase T1")));
        averageMassList.add(new Nucleotide("C", "C", 111.1030, 0.0, List.of("RNase A")));
        averageMassList.add(new Nucleotide("T", "T", 126.1130, 0.0, List.of("RNase A")));
        averageMassList.add(new Nucleotide("B", "Cm", 111.1030, 14.0157, List.of())); 
    }

    public static List<Nucleotide> getMonoIsotopicNucleotides() {
        return new ArrayList<>(monoIsotopicList);
    }

    public static List<Nucleotide> getAverageMassNucleotides() {
        return new ArrayList<>(averageMassList);
    }
}

class Nucleotide {
    private final String symbol;         
    private final String shortName;     
    private final double baseMass;       
    private final double sugarMod;       
    private final List<String> cleavableBy; 

    public Nucleotide(String symbol, String shortName, double baseMass, double sugarMod, List<String> cleavableBy) {
        this.symbol = symbol;
        this.shortName = shortName;
        this.baseMass = baseMass;
        this.sugarMod = sugarMod;
        this.cleavableBy = cleavableBy;
    }

    public String getSymbol() { return symbol; }
    public String getShortName() { return shortName; }
    public double getBaseMass() { return baseMass; }
    public double getSugarMod() { return sugarMod; }
    public List<String> getCleavableBy() { return cleavableBy; }
    public double getBaseMassWithSugar() { return baseMass + sugarMod;}
}

class MassConstants {
    public enum MassMode {
        MONOISOTOPIC,
        AVERAGE
    }

    private static final double PROTON_MASS_MONO = 1.0078250319;     
    private static final double WATER_MASS_MONO = 18.01056469;    
    private static final double PHOSPHATE_MASS_MONO = 79.96633039; 
    private static final double SUGAR_MASS_MONO = 99.04460447;
    private static final double OXYGEN_MASS_MONO = 15.99491;


    private static final double PROTON_MASS_AVG = 1.007946;     
    private static final double WATER_MASS_AVG = 18.01528;     
    private static final double PHOSPHATE_MASS_AVG = 79.979902;  
    private static final double SUGAR_MASS_AVG = 99.10788;     
    private static final double OXYGEN_MASS_AVG = 15.9994;


    private static MassMode currentMode = MassMode.MONOISOTOPIC;

    public static void setMassMode(MassMode mode) {
        currentMode = mode;
    }

    public static MassMode getMassMode() {
        return currentMode;
    }

    public static double getProtonMass() {
        return currentMode == MassMode.MONOISOTOPIC ? PROTON_MASS_MONO : PROTON_MASS_AVG;
    }

    public static double getWaterMass() {
        return currentMode == MassMode.MONOISOTOPIC ? WATER_MASS_MONO : WATER_MASS_AVG;
    }

    public static double getPhosphateMass() {
        return currentMode == MassMode.MONOISOTOPIC ? PHOSPHATE_MASS_MONO : PHOSPHATE_MASS_AVG;
    }

    public static double getSugarMass() {
        return currentMode == MassMode.MONOISOTOPIC ? SUGAR_MASS_MONO : SUGAR_MASS_AVG;
    }

    public static double getOxygenMass() {
        return currentMode == MassMode.MONOISOTOPIC ? OXYGEN_MASS_MONO : OXYGEN_MASS_AVG;
    }
}
