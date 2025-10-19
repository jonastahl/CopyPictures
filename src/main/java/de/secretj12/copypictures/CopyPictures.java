package de.secretj12.copypictures;

import com.drew.imaging.ImageMetadataReader;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.Tag;

import javax.swing.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;


public class CopyPictures {
    public static Fenster Window;
    public static final String version = "4.4";
    public static final boolean isWin = System.getProperty("os.name").toLowerCase().contains("win");
    public static final String home = System.getProperty("user.home");
    public static final String cfgFile = isWin ? "C:/CopyPictures/config.txt" : "~/.copypictures/config.cfg";
    public static final String defFile = isWin ? "C:" : "~";

    public static void main(String[] args) {
        Window = new Fenster();

        if (args.length > 0) {
            File Quelle = new File(args[0].replace("~", home));

            File Ziel = Fenster.ZielFile;
            if (args.length > 1) Ziel = new File(args[1].replace("~", home));

            boolean Unterordnereinbeziehen = Fenster.Unterordnereinbeziehen;
            if (args.length > 2) {
                if (args[2].equals("1") | args[2].equals("true")) Unterordnereinbeziehen = true;
                else if (args[2].equals("0") | args[2].equals("false")) Unterordnereinbeziehen = false;
                else {
                    JOptionPane.showMessageDialog(null, "Nur true, false, 1 oder 0 gültig: " + args[2], "Unterordnereinbeziehen", JOptionPane.ERROR_MESSAGE);
                    System.exit(1);
                }
            }

            boolean Verschieben = Fenster.Verschieben;
            if (args.length > 3) {
                if (args[3].equals("1") | args[3].equals("true")) Verschieben = true;
                else if (args[3].equals("0") | args[3].equals("false")) Verschieben = false;
                else {
                    JOptionPane.showMessageDialog(null, "Nur true, false, 1 oder 0 gültig: " + args[3], "Verschieben", JOptionPane.ERROR_MESSAGE);
                    System.exit(1);
                }
            }

            String Format = Fenster.Format;
            if (args.length > 4) {
                Format = args[4];
            }

            String Verzeichnis = Fenster.Verzeichnis;
            if (args.length > 5) {
                Verzeichnis = args[5];
            }

            int wennVorhanden = Fenster.wennVorhanden;
            if (args.length > 6) {
                switch (args[6]) {
                    case "0" -> wennVorhanden = 0;
                    case "1" -> wennVorhanden = 1;
                    case "2" -> wennVorhanden = 2;
                    case "3" -> wennVorhanden = 3;
                    default -> {
                        JOptionPane.showMessageDialog(null, "Nur 0, 1, 2 oder 3 gültig: " + args[6], "wennVorhanden", JOptionPane.ERROR_MESSAGE);
                        System.exit(1);
                    }
                }
            }

            String Suffix = Fenster.Suffix;
            if (args.length > 7) {
                Suffix = args[7];
            }

            int wennkeinExif = Fenster.wennkeinExif;
            if (args.length > 8) {
                switch (args[8]) {
                    case "0" -> wennkeinExif = 0;
                    case "1" -> wennkeinExif = 1;
                    case "2" -> wennkeinExif = 2;
                }
            }

            int Dateien = Fenster.Dateien;
            if (args.length > 9) {
                switch (args[9]) {
                    case "0" -> Dateien = 0;
                    case "1" -> Dateien = 1;
                    case "2" -> Dateien = 2;
                }
            }

            String benutzerdefinierteDateien = Fenster.benutzerdefinierteDateien;
            if (args.length > 10) {
                benutzerdefinierteDateien = args[10];
            }

            Start(Quelle, Ziel, Unterordnereinbeziehen, Verschieben, Format, Verzeichnis, wennVorhanden, Suffix, wennkeinExif, Dateien, benutzerdefinierteDateien);
            System.exit(1);
        } else {
            Window.init();
            Window.setVisible(true);
            System.out.println(Window.getContentPane().getWidth());
        }

        System.out.println("end");
    }

    public static void Start(File Quelle, File Ziel, boolean Unterordnereinbeziehen, boolean Verschieben, String Format, String Verzeichnis, int wennVorhanden, String Suffix, int wennkeinExif, int Dateien, String benutzerdefinierteDateien) {
        if (!Quelle.exists()) {
            JOptionPane.showMessageDialog(null, "Die Quelle konnte nicht gefunden werden: " + Quelle, "Ungültige Quelle", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (Verschieben) {
            if (wennVorhanden == 0) {
                int i = JOptionPane.showOptionDialog(Window, "Wollen sie die Dateien wirklich verschieben?\nWenn zwei Dateien mit dem gleichem Namen existieren wird die neue automatisch gelöscht!", "Warung", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, null, new String[]{"Ja", "Nein"}, "Nein");
                if (i == 1) {
                    return;
                }
            } else if (wennVorhanden == 1) {
                int i = JOptionPane.showOptionDialog(Window, "Wollen sie die Dateien wirklich verschieben?\nWenn zwei Dateien mit dem gleichem Namen existieren wird die alte automatisch gelöscht!", "Warung", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, null, new String[]{"Ja", "Nein"}, "Nein");
                if (i == 1) {
                    return;
                }
            }
        }

        Format = Format.replace("\\", "");
        Format = Format.replace("/", "");
        Format = Format.replace(":", "");
        Format = Format.replace("*", "");
        Format = Format.replace("?", "");
        Format = Format.replace("\"", "");
        Format = Format.replace("<", "");
        Format = Format.replace(">", "");
        Format = Format.replace("|", "");
        Verzeichnis = Verzeichnis.replace("\\", "/");
        Verzeichnis = Verzeichnis.replace(":", "");
        Verzeichnis = Verzeichnis.replace("*", "");
        Verzeichnis = Verzeichnis.replace("?", "");
        Verzeichnis = Verzeichnis.replace("\"", "");
        Verzeichnis = Verzeichnis.replace("<", "");
        Verzeichnis = Verzeichnis.replace(">", "");
        Verzeichnis = Verzeichnis.replace("|", "");

        KopierenFenster KopierenFenster = new KopierenFenster();
        KopierenFenster.setButtonText("Abbrechen");
        KopierenFenster.setText("Suche nach Bildern...");

        ArrayList<File> Bilder = new ArrayList<>();
        ArrayList<String> benutzerdefinierteDateienArray = new ArrayList<>(Arrays.asList(benutzerdefinierteDateien.toLowerCase().split(";")));
        if (benutzerdefinierteDateienArray.isEmpty()) benutzerdefinierteDateienArray.add(benutzerdefinierteDateien);
        readFile(Bilder, Quelle, Unterordnereinbeziehen, Dateien, benutzerdefinierteDateienArray);

        System.out.println(Bilder.size() + " Bilder gefunden!");

        if (KopierenFenster.isStopped()) {
            KopierenFenster.setVisible(false);
            System.out.println("Abgebrochen!");
            return;
        }

        if (Verschieben) {
            KopierenFenster.setText("Verschiebe Bilder...");
        } else {
            KopierenFenster.setText("Kopiere Bilder...");
        }

        int Fehler = 0;
        int Sortiert = 0;
        int Unsortiert = 0;
        int i = 1;
        for (File a : Bilder) {
            Date date = readPicture(a, wennkeinExif);

            if (date != null) {
                if (!savePicture(Quelle, a, Ziel, Verschieben, Format, Verzeichnis, date, wennVorhanden, Suffix)) {
                    Fehler++;
                    System.out.println("Fehler beim Kopieren: " + a);
                } else {
                    Sortiert++;
                }
            } else {
                if (wennkeinExif == 0) continue;
                else {
                    if (wennkeinExif == 2) System.out.println("Kein Datum gefunden: " + a.toString());

                    if (!savePicture(Quelle, a, Ziel, Verschieben, Format, Verzeichnis, null, wennVorhanden, Suffix)) {
                        Fehler++;
                        System.out.println("Fehler beim Kopieren: " + a);
                    } else {
                        Unsortiert++;
                    }
                }
            }

            if (KopierenFenster.isStopped()) {
                KopierenFenster.setVisible(false);
                System.out.println("Abgebrochen: Sortiert: " + Sortiert + " Unsortiert: " + Unsortiert + " Fehler: " + Fehler + " Fehlend: " + (Bilder.size() - Sortiert - Unsortiert - Fehler));
                return;
            }
            KopierenFenster.setProgress((int) ((double) i / (double) Bilder.size() * 100));
            i++;
        }
        KopierenFenster.setButtonText("OK");
        KopierenFenster.setText("Abgeschlossen: Sortiert: " + Sortiert + " Unsortiert: " + Unsortiert + " Fehler: " + Fehler);
        System.out.println("Sortiert: " + Sortiert + " Unsortiert: " + Unsortiert + " Fehler: " + Fehler);

        KopierenFenster.setCloseable(true);
    }

    public static void readFile(List<File> Bilder, File file, boolean Unterordnereinbeziehen, int Dateien, ArrayList<String> benutzerdefnierteDateien) {
        File[] Unterordner = file.listFiles();
        if (Unterordner == null) {
            return;
        }
        for (File subfile : Unterordner) {
            if (subfile.isDirectory()) {
                if (Unterordnereinbeziehen) {
                    readFile(Bilder, subfile, true, Dateien, benutzerdefnierteDateien);
                }
            } else if (!subfile.getName().startsWith("._")) {
                switch (Dateien) {
                    case 0:
                        try {
                            String contentType = Files.probeContentType(subfile.toPath());
                            if (contentType != null && contentType.contains("image"))
                                Bilder.add(subfile);
                        } catch (IOException e) {
                            System.err.println("Error reading metadata of file");
                        }
                        break;
                    case 1:
                        Bilder.add(subfile);
                        break;
                    case 2:
                        if (subfile.toString().contains(".") && benutzerdefnierteDateien.contains(subfile.toString().substring(subfile.toString().lastIndexOf(".") + 1).toLowerCase()))
                            Bilder.add(subfile);
                        break;
                    default:
                        break;
                }
            }
        }
    }

    public static Date readPicture(File Quelle, int wennkeinExif) {
        Date date = null;

        try {
            Metadata bild = ImageMetadataReader.readMetadata(Quelle);
            SimpleDateFormat format = new SimpleDateFormat("yyyy:MM:dd HH:mm:ss");
            SimpleDateFormat movAppleFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            for (Directory directory : bild.getDirectories()) {
                if (directory.getName().equals("Exif IFD0")) {
                    for (Tag tag : directory.getTags()) {
                        if (tag.getTagName().equals("Date/Time")) {
                            String PicDate = tag.getDescription();
                            try {
                                date = format.parse(PicDate);
                            } catch (ParseException e) {
                                System.err.println("Error parsing file: ");
                                System.exit(1);
                            }
                        }
                    }
                }
                if (directory.getName().equals("QuickTime Metadata")) {
                    for (Tag tag : directory.getTags()) {
                        if (tag.getTagName().equals("Creation Date")) {
                            String PicDate = tag.getDescription().substring(0, 19);
                            try {
                                date = movAppleFormat.parse(PicDate);
                            } catch (ParseException e) {
                                System.err.println("Error parsing file: ");
                                e.printStackTrace();
                                System.exit(1);
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Cannot determine metadata of file: " + Quelle);
            e.printStackTrace();
        }

        if (date == null) {
            if (wennkeinExif <= 1) return null;
            else {
                try {
                    Calendar cal = Calendar.getInstance();
                    cal.setTimeInMillis(Quelle.lastModified());
                    return cal.getTime();
                } catch (Exception ex) {
                    System.out.println("Error bei: " + Quelle);
                    return null;
                }
            }
        } else return date;

    }

    public static boolean savePicture(File anfangsOrdner, File Quelle, File Ziel, boolean Verschieben, String Name, String Verzeichnis, Date Date, int wennVorhanden, String Suffix) {
        String endung = "";
        if (Quelle.toString().contains(".")) endung = Quelle.toString().substring(Quelle.toString().lastIndexOf("."));

        if (Date != null) {
            Calendar cal = Calendar.getInstance();
            cal.setTime(Date);

            //Verzeichnis
            Verzeichnis = Verzeichnis.replace("YYYY", cal.get(Calendar.YEAR) + "");
            if (cal.get(Calendar.MONTH) + 1 < 10) {
                Verzeichnis = Verzeichnis.replace("MM", "0" + (cal.get(Calendar.MONTH) + 1));
            } else {
                Verzeichnis = Verzeichnis.replace("MM", (cal.get(Calendar.MONTH) + 1) + "");
            }
            if (cal.get(Calendar.DAY_OF_MONTH) < 10) {
                Verzeichnis = Verzeichnis.replace("DD", "0" + cal.get(Calendar.DAY_OF_MONTH));
            } else {
                Verzeichnis = Verzeichnis.replace("DD", cal.get(Calendar.DAY_OF_MONTH) + "");
            }
            if (cal.get(Calendar.HOUR_OF_DAY) < 10) {
                Verzeichnis = Verzeichnis.replace("hh", "0" + cal.get(Calendar.HOUR_OF_DAY));
            } else {
                Verzeichnis = Verzeichnis.replace("hh", cal.get(Calendar.HOUR_OF_DAY) + "");
            }
            if (cal.get(Calendar.MINUTE) < 10) {
                Verzeichnis = Verzeichnis.replace("mm", "0" + cal.get(Calendar.MINUTE));
            } else {
                Verzeichnis = Verzeichnis.replace("mm", cal.get(Calendar.MINUTE) + "");
            }
            if (cal.get(Calendar.SECOND) < 10) {
                Verzeichnis = Verzeichnis.replace("ss", "0" + cal.get(Calendar.SECOND));
            } else {
                Verzeichnis = Verzeichnis.replace("ss", cal.get(Calendar.SECOND) + "");
            }

            //Name
            Name = Name.replace("YYYY", cal.get(Calendar.YEAR) + "");
            if (cal.get(Calendar.MONTH) + 1 < 10) {
                Name = Name.replace("MM", "0" + (cal.get(Calendar.MONTH) + 1));
            } else {
                Name = Name.replace("MM", (cal.get(Calendar.MONTH) + 1) + "");
            }
            if (cal.get(Calendar.DAY_OF_MONTH) < 10) {
                Name = Name.replace("DD", "0" + cal.get(Calendar.DAY_OF_MONTH));
            } else {
                Name = Name.replace("DD", cal.get(Calendar.DAY_OF_MONTH) + "");
            }
            if (cal.get(Calendar.HOUR_OF_DAY) < 10) {
                Name = Name.replace("hh", "0" + cal.get(Calendar.HOUR_OF_DAY));
            } else {
                Name = Name.replace("hh", cal.get(Calendar.HOUR_OF_DAY) + "");
            }
            if (cal.get(Calendar.MINUTE) < 10) {
                Name = Name.replace("mm", "0" + cal.get(Calendar.MINUTE));
            } else {
                Name = Name.replace("mm", cal.get(Calendar.MINUTE) + "");
            }
            if (cal.get(Calendar.SECOND) < 10) {
                Name = Name.replace("ss", "0" + cal.get(Calendar.SECOND));
            } else {
                Name = Name.replace("ss", cal.get(Calendar.SECOND) + "");
            }
        } else {
            Verzeichnis = "Unsortiert" + Quelle.toString().replace(anfangsOrdner.toString(), "");
            Verzeichnis = Verzeichnis.substring(0, Verzeichnis.lastIndexOf("/"));

            String[] parts = Quelle.toString().split("/");
            String fullname = parts[parts.length - 1];

            Name = fullname.substring(0, fullname.lastIndexOf("."));
        }

        Ziel = new File(Ziel, Verzeichnis);

        if (!Ziel.exists() && !Ziel.mkdirs()) {
            System.err.println("Error creating target directory");
            System.exit(1);
        }

        File completeZiel = new File(Ziel, Name + endung);

        if (wennVorhanden == 3) {
            completeZiel = new File(Ziel, Name + Suffix.replace("xxx", "001") + endung);
        }

        if (completeZiel.exists() && (wennVorhanden == 2 | wennVorhanden == 3)) {
            int i = 2;
            while (completeZiel.exists()) {
                if (i < 10) {
                    completeZiel = new File(Ziel, Name + Suffix.replace("xxx", "00" + i) + endung);
                } else if (i < 100) {
                    completeZiel = new File(Ziel, Name + Suffix.replace("xxx", "0" + i) + endung);
                } else {
                    completeZiel = new File(Ziel, Name + Suffix.replace("xxx", i + "") + endung);
                }
                i++;
            }
        }

        if (completeZiel.exists() && wennVorhanden == 0) {
            return true;
        }

        return copy(Quelle, completeZiel, Verschieben);
    }

    public static boolean copy(File Quelle, File Ziel, boolean Verschieben) {
        try {
            FileInputStream fis = new FileInputStream(Quelle); //Stream fuer Quelldatei
            FileOutputStream fos = new FileOutputStream(Ziel); //Stream fuer Zieldatei
            byte[] buf = new byte[1024]; // Buffer fuer gelesene Daten
            int len;
            while ((len = fis.read(buf)) != -1) { // solange lesen, bis EOF
                fos.write(buf, 0, len); // Inhalt schreiben
            }
            fis.close();
            fos.flush();
            fos.close();

            if (Verschieben)
                if (!Quelle.delete())
                    System.err.println("Error deleting old file");
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}