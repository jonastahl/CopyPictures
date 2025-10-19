package de.secretj12.copypictures;

import javax.swing.*;
import javax.swing.plaf.metal.MetalIconFactory;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;
import java.nio.file.Files;
import java.util.HashMap;

import static de.secretj12.copypictures.CopyPictures.*;

class Fenster extends JFrame {
    @Serial
    private static final long serialVersionUID = 1L;

    public static File QuelleFile = new File(defFile);
    public static File ZielFile = new File(defFile);
    public static boolean Unterordnereinbeziehen = true;
    public static boolean Verschieben = false;
    public static String Format = "YYYY-MM-DD_hh-mm-ss";
    public static String Verzeichnis = "YYYY/MM";
    /*
     * 0: nicht kopieren
     * 1: überschreiben
     * 2: Suffix hinzufügen
     * 3: immer Suffix hinzufügen
     */
    public static int wennVorhanden = 2;
    public static String Suffix = "_xxx";
    /*
     * 0: nicht kopieren
     * 1: in unsortiert
     * 2: nutze zuletzt bearbeitet
     */
    public static int wennkeinExif = 1;
    /*
     * 0: nur Bilder
     * 1: alle Dateien
     * 2: nur folgende Dateien
     */
    public static int Dateien = 0;
    public static String benutzerdefinierteDateien = "jpg;png";

    public static JTextField QuelleTextField;
    public static JTextField ZielTextField;
    public static JCheckBox includeSubfolder;
    public static JRadioButton KopierenButton;
    public static JRadioButton VerschiebenButton;
    public static JTextField FormatTextField;
    public static JTextField VerzeichnisTextField;
    public static JTextField SuffixTextField;
    public static JComboBox<String> wennVorhandenBox;
    public static JComboBox<String> wennkeinExifBox;
    public static JTextField benutzerdefinierteDateienTextField;
    public static JComboBox<String> DateienBox;
    public static JFrame KonsolenFrame;

    public Fenster() {
        File config = new File(cfgFile.replace("~", home));

        if (config.exists()) {
            try {
                FileReader FReader = new FileReader(config);
                BufferedReader Reader = new BufferedReader(FReader);
                String line = Reader.readLine();
                HashMap<String, String> read = new HashMap<>();
                while (line != null) {
                    System.out.println(line);
                    String[] split = line.split("=");
                    try {
                        read.put(split[0], split[1]);
                    } catch (ArrayIndexOutOfBoundsException e) {
                        System.err.println("Error parsing config file");
                        System.exit(1);
                    }
                    line = Reader.readLine();
                }
                Reader.close();


                if (read.get("QuelleFile") != null) {
                    QuelleFile = new File(read.get("QuelleFile"));
                }

                if (read.get("ZielFile") != null) {
                    ZielFile = new File(read.get("ZielFile"));
                }

                if (read.get("Unterordnereinbeziehen") != null) {
                    Unterordnereinbeziehen = !read.get("Unterordnereinbeziehen").equals("false");
                }

                if (read.get("Verschieben") != null) {
                    Verschieben = read.get("Verschieben").equals("true");
                }

                if (read.get("Format") != null) {
                    Format = read.get("Format");
                }

                if (read.get("Verzeichnis") != null) {
                    Verzeichnis = read.get("Verzeichnis");
                }

                if (read.get("wennVorhanden") != null) {
                    String a = read.get("wennVorhanden");
                    switch (a) {
                        case "\"0\"" -> wennVorhanden = 0;
                        case "\"1\"" -> wennVorhanden = 1;
                        case "\"2\"" -> wennVorhanden = 2;
                        case "\"3\"" -> wennVorhanden = 3;
                    }

                    if (wennVorhanden > 3 | wennVorhanden < 0) {
                        wennVorhanden = 2;
                    }
                }

                if (read.get("Suffix") != null) {
                    Suffix = read.get("Suffix");
                }

                if (read.get("wennkeinExif") != null) {
                    String a = read.get("wennkeinExif");
                    switch (a) {
                        case "\"0\"" -> wennkeinExif = 0;
                        case "\"1\"" -> wennkeinExif = 1;
                        case "\"2\"" -> wennkeinExif = 2;
                    }

                    if (wennkeinExif > 2 | wennkeinExif < 0) {
                        wennkeinExif = 1;
                    }
                }

                if (read.get("Dateien") != null) {
                    String a = read.get("Dateien");
                    switch (a) {
                        case "\"0\"" -> Dateien = 0;
                        case "\"1\"" -> Dateien = 1;
                        case "\"2\"" -> Dateien = 2;
                    }
                    if (Dateien > 2 | Dateien < 0) {
                        Dateien = 0;
                    }
                }

                if (read.get("benutzerdefinierteDateien") != null) {
                    benutzerdefinierteDateien = read.get("benutzerdefinierteDateien");
                }
            } catch (IOException e) {
                System.err.println("Error parsing config file");
                System.exit(1);
            }
        } else {
            if (!new File(cfgFile.replace("~", home)).getParentFile().exists() && !new File(cfgFile.replace("~", home)).getParentFile().mkdirs()) {
                System.err.println("Error creating config file");
                System.exit(1);
            }
            try {
                if (!config.createNewFile()) {
                    System.err.println("Error parsing config file");
                    System.exit(1);
                }
            } catch (IOException e) {
                System.err.println("Error creating config file");
                System.exit(1);
            }
        }
        createConfig(config);
    }

    public void createConfig(File config) {
        try {
            if (!config.exists() && !config.createNewFile()) {
                System.err.println("Error creating config file");
                System.exit(1);
            }

            String output = "QuelleFile=" + QuelleFile;
            output += "\nZielFile=" + ZielFile;
            output += "\nUnterordnereinbeziehen=" + Unterordnereinbeziehen;
            output += "\nVerschieben=" + Verschieben;
            output += "\nFormat=" + Format;
            output += "\nVerzeichnis=" + Verzeichnis;
            output += "\nwennVorhanden=" + "\"" + wennVorhanden + "\"";
            output += "\nSuffix=" + Suffix;
            output += "\nwennkeinExif=" + "\"" + wennkeinExif + "\"";
            output += "\nDateien=" + "\"" + Dateien + "\"";
            output += "\nbenutzerdefinierteDateien=" + benutzerdefinierteDateien;

            FileWriter FWriter = new FileWriter(config);
            BufferedWriter writer = new BufferedWriter(FWriter);

            writer.write(output);

            writer.close();

        } catch (IOException e) {
            System.err.println("Error creating config file");
            System.exit(1);
        }
    }

    public void init() {
        this.setTitle("CopyPictures " + CopyPictures.version);
        this.setSize(586, 530);
        this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);


        JFenster Fenster = new JFenster();

        //Überschrift
        Fenster.add(new JLabel("Copy Pictures " + CopyPictures.version), this, 20);

        //Separator
        Fenster.add(new JSeparator(JSeparator.HORIZONTAL), 0, 50, 586, 100);

        //Quelle
        Fenster.add(new JLabel("Quelle:"), 10, 60);
        QuelleTextField = new JTextField(QuelleFile.toString());
        Fenster.add(QuelleTextField, 10, 80, 500, 20);
        JButton QuelleButton = new JButton(MetalIconFactory.getFileChooserNewFolderIcon());
        QuelleButton.addActionListener(e -> {
            QuelleFile = new File(QuelleTextField.getText().replace("~", home));

            JFileChooser Chooser = new JFileChooser(QuelleFile);
            Chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            Chooser.showOpenDialog(null);
            if (Chooser.getSelectedFile() != null) {
                QuelleFile = Chooser.getSelectedFile();
                QuelleTextField.setText(QuelleFile.toString().replace(home, "~"));
            }
        });
        Fenster.add(QuelleButton, 550, 80, 20, 20);

        //Ziel
        Fenster.add(new JLabel("Ziel:"), 10, 110);
        ZielTextField = new JTextField(ZielFile.toString());
        ZielTextField.addActionListener(e -> ZielFile = new File(ZielTextField.getText().replace("~", home)));
        Fenster.add(ZielTextField, 10, 130, 500, 20);
        JButton ZielButton = new JButton(MetalIconFactory.getFileChooserNewFolderIcon());
        ZielButton.addActionListener(e -> {
            ZielFile = new File(ZielTextField.getText().replace("~", home));

            JFileChooser Chooser = new JFileChooser(ZielFile);
            Chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            int i = Chooser.showOpenDialog(null);
            if (i == 0) {
                ZielFile = Chooser.getSelectedFile();
                ZielTextField.setText(ZielFile.toString().replace(home, "~"));
            }
        });
        Fenster.add(ZielButton, 550, 130, 20, 20);

        //Unterordner einbeziehen
        includeSubfolder = new JCheckBox("Unterordner einbeziehen", Unterordnereinbeziehen);
        includeSubfolder.setSelected(Unterordnereinbeziehen);
        Fenster.add(includeSubfolder, 10, 160, 300, 20);

        //Kopieren/Verschieben
        KopierenButton = new JRadioButton("Kopieren");
        VerschiebenButton = new JRadioButton("Verschieben");
        ButtonGroup KopierenVerschiebenGruppe = new ButtonGroup();
        KopierenVerschiebenGruppe.add(KopierenButton);
        KopierenVerschiebenGruppe.add(VerschiebenButton);
        KopierenButton.setSelected(!Verschieben);
        VerschiebenButton.setSelected(Verschieben);
        Fenster.add(KopierenButton, 10, 190, 90, 20);
        Fenster.add(VerschiebenButton, 100, 190, 200, 20);

        //Format
        Fenster.add(new JLabel("Format:"), 10, 220);
        FormatTextField = new JTextField(Format);
        FormatTextField.addActionListener(e -> Format = FormatTextField.getText());
        Fenster.add(FormatTextField, 100, 220, 200, 20);
        JButton DateFormatButton = new JButton("?");
        DateFormatButton.addActionListener(e -> JOptionPane.showOptionDialog(null, "Y = Jahr \n M = Monat \n D = Tag \n h = Stunde \n m = Minute \n s = Sekunde", "Format", JOptionPane.OK_OPTION, JOptionPane.DEFAULT_OPTION, null, new String[]{"OK"}, "OK"));
        Fenster.add(DateFormatButton, 550, 220, 20, 20);

        //Speicherverzeichnis
        Fenster.add(new JLabel("Verzeichnis:"), 10, 250);
        VerzeichnisTextField = new JTextField(Verzeichnis);
        VerzeichnisTextField.addActionListener(e -> Verzeichnis = VerzeichnisTextField.getText());
        Fenster.add(VerzeichnisTextField, 100, 250, 200, 20);

        //Wenn vorhanden
        Fenster.add(new JLabel("Wenn die Datei vorhanden ist "), 10, 280);
        SuffixTextField = new JTextField(Suffix);
        SuffixTextField.addActionListener(arg0 -> updateSuffix());
        String[] wennVorhandenItems = {" kopiere Datei nicht", " überschreibe alte Datei", " füge Suffix hinzu:", " füge immer Suffix hinzu:"};
        wennVorhandenBox = new JComboBox<>(wennVorhandenItems);
        wennVorhandenBox.setSelectedIndex(wennVorhanden);
        wennVorhandenBox.addActionListener(arg0 -> SuffixTextField.setVisible(wennVorhandenBox.getSelectedIndex() >= 2));
        Fenster.add(wennVorhandenBox, 200, 280, 180, 20);
        Fenster.add(SuffixTextField, 400, 280, 110, 20);
        SuffixTextField.setVisible(wennVorhandenBox.getSelectedIndex() >= 2);
        updateSuffix();

        //Wenn kein Exif
        Fenster.add(new JLabel("Wenn kein Exif vorhanden ist"), 10, 310);
        String[] wennkeinExifItems = {" kopiere Datei nicht", " kopiere in unsortiert", " sortiere nach letztem Änderungsdatum"};
        wennkeinExifBox = new JComboBox<>(wennkeinExifItems);
        wennkeinExifBox.setSelectedIndex(wennkeinExif);
        Fenster.add(wennkeinExifBox, 200, 310, 310, 20);

        //Dateien zum Kopieren
        Fenster.add(new JLabel("Kopiere "), 10, 340);
        benutzerdefinierteDateienTextField = new JTextField(benutzerdefinierteDateien);
        String[] DateienItems = {" nur Bilder", " alle Dateien", " benutzerdefinierte Dateien"};
        DateienBox = new JComboBox<>(DateienItems);
        DateienBox.setSelectedIndex(Dateien);
        DateienBox.addActionListener(e -> benutzerdefinierteDateienTextField.setVisible(DateienBox.getSelectedIndex() == 2));
        Fenster.add(DateienBox, 80, 340, 200, 20);
        Fenster.add(benutzerdefinierteDateienTextField, 300, 340, 210, 20);
        benutzerdefinierteDateienTextField.setVisible(DateienBox.getSelectedIndex() == 2);

        //Konsolenstart
        JLabel KonsolenLabel = new JLabel("Konsoleneigabe:");
        Fenster.add(KonsolenLabel, 10, 370, 100, 20);

        JButton KonsolenButton = new JButton("?");
        KonsolenButton.addActionListener(e -> {
            if (KonsolenFrame != null) KonsolenFrame.setVisible(false);

            KonsolenFrame = new JFrame("Konsoleneingabe");
            JFenster KonsolenFenster = new JFenster();
            KonsolenFrame.setSize(940, 190);
            KonsolenFrame.setLocationRelativeTo(null);
            KonsolenFrame.setResizable(false);

            KonsolenFenster.add(new JLabel("Syntax: (Für fehlende Argumente werden die akutellen Einstellungen verwendet)"), 10, 10);
            KonsolenFenster.add(new JLabel("Quelle [Ziel] [Unterordnereinbeziehen] [Verschieben] [Dateiformat] [Ordnerformat] [wennVorhanden] [Suffix] [wennkeinExif] [Dateien] [benutzerdefinierteDateien]"), 10, 40, 1000, 15);
            KonsolenFenster.add(new JLabel("(wennVorhanden: 0 = nicht kopieren; 1 = überschreiben; 2 = Suffix hinzufügen; 3 = Suffix immer hinzufügen)"), 10, 70);
            KonsolenFenster.add(new JLabel("(wennkeinExif: 0 = nicht kopieren; 1 = in unsortiert; 2 = nutze zuletzt bearbeitet)"), 10, 100);
            KonsolenFenster.add(new JLabel("(Dateien: kopiere 0 = nur Bilder; 1 = alle Dateien; 2 = benutzerdefinierte Dateien)"), 10, 130);

            JButton KonsolenButton1 = new JButton("Always on Top");
            KonsolenButton1.addActionListener(e1 -> KonsolenFrame.setAlwaysOnTop(!KonsolenFrame.isAlwaysOnTop()));
            KonsolenFenster.add(KonsolenButton1, 808, 10, 116, 20);

            KonsolenFrame.setContentPane(KonsolenFenster);
            KonsolenFrame.setVisible(true);
        });
        Fenster.add(KonsolenButton, 550, 370, 20, 20);


        //Separator
        Fenster.add(new JSeparator(JSeparator.HORIZONTAL), 0, 400, 586, 20);

        //Start
        JButton StartButton = new JButton("Start");
        StartButton.addActionListener(e -> {
            QuelleFile = new File(QuelleTextField.getText().replace("~", home));
            ZielFile = new File(ZielTextField.getText().replace("~", home));
            Format = FormatTextField.getText();
            Verzeichnis = VerzeichnisTextField.getText();
            Suffix = SuffixTextField.getText();

            Thread Thread = new Thread(() -> {
                update();
                CopyPictures.Start(QuelleFile, ZielFile, Unterordnereinbeziehen, Verschieben, Format, Verzeichnis, wennVorhanden, Suffix, wennkeinExif, Dateien, benutzerdefinierteDateien);
            });
            Thread.start();
        });
        Fenster.add(StartButton, this, 427, 200, 50);


        this.add(Fenster);
        this.setResizable(false);
        this.setLocationRelativeTo(null);

        this.addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent e) {
                update();

                createConfig(new File(cfgFile.replace("~", home)));
                System.exit(1);
            }
        });
    }

    private void updateSuffix() {
        if (!SuffixTextField.getText().contains("xxx")) {
            SuffixTextField.setText(SuffixTextField.getText() + "_xxx");
        }
        Suffix = SuffixTextField.getText();
    }

    private void update() {
        updateSuffix();

        QuelleFile = new File(QuelleTextField.getText().replace("~", home));
        ZielFile = new File(ZielTextField.getText().replace("~", home));
        Unterordnereinbeziehen = includeSubfolder.isSelected();
        Verschieben = VerschiebenButton.isSelected();
        Format = FormatTextField.getText();
        Verzeichnis = VerzeichnisTextField.getText();
        wennVorhanden = wennVorhandenBox.getSelectedIndex();
        Suffix = SuffixTextField.getText();
        wennkeinExif = wennkeinExifBox.getSelectedIndex();
        Dateien = DateienBox.getSelectedIndex();
        benutzerdefinierteDateien = benutzerdefinierteDateienTextField.getText();
    }
}
