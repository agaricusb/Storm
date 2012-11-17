package com.github.StormTeam.Storm;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;

/**
 * @author H31IX
 */

public class Updater {
    private Plugin plugin;
    private UpdateType type;
    private String versionTitle;
    private String versionLink;
    private long totalSize;
    private int sizeLine;
    private int multiplier;
    private boolean announce;
    private URL url; // Connecting to RSS
    private static final String DBOUrl = "http://dev.bukkit.org/server-mods/"; // Slugs will be appended to this to get to the project's RSS feed
    private String[] noUpdateTag = {"-DEV", "-PRE"}; // If the version number contains one of these, don't update.
    private static final int BYTE_SIZE = 1024; // Used for downloading files
    private String updateFolder = YamlConfiguration.loadConfiguration(new File("bukkit.yml")).getString("settings.update-folder"); // The folder that downloads will be placed in
    private Updater.UpdateResult result = Updater.UpdateResult.SUCCESS; // Used for determining the outcome of the update process

    // Strings for reading RSS
    private static final String TITLE = "title";
    private static final String LINK = "link";
    private static final String ITEM = "item";

    public enum UpdateResult {
        /**
         * The updater found an update, and has readied it to be isFromFile the next time the server restarts/reloads.
         */
        SUCCESS(1),
        /**
         * The updater did not find an update, and nothing was downloaded.
         */
        NO_UPDATE(2),
        /**
         * The updater found an update, but was unable to download it.
         */
        FAIL_DOWNLOAD(3),
        /**
         * For some reason, the updater was unable to contact dev.bukkit.org to download the file.
         */
        FAIL_DBO(4),
        /**
         * When running the version check, the file on DBO did not contain the a version in the format 'vVersion' such as 'v1.0'.
         */
        FAIL_NOVERSION(5),
        /**
         * The slug provided by the plugin running the updater was invalid and doesn't exist on DBO.
         */
        FAIL_BADSLUG(6),
        /**
         * The updater found an update, but because of the UpdateType being set to NO_DOWNLOAD, it wasn't downloaded.
         */
        UPDATE_AVAILABLE(7);

        private static final Map<Integer, Updater.UpdateResult> valueList = new HashMap<Integer, Updater.UpdateResult>();
        private final int value;

        private UpdateResult(int value) {
            this.value = value;
        }

        public int getValue() {
            return this.value;
        }

        public static Updater.UpdateResult getResult(int value) {
            return valueList.get(value);
        }

        static {
            for (Updater.UpdateResult result : Updater.UpdateResult.values()) {
                valueList.put(result.value, result);
            }
        }
    }

    /**
     * Allows the dev to specify the type of update that will be run.
     */
    public enum UpdateType {
        /**
         * Run a version check, and then if the file is out of date, download the newest version.
         */
        DEFAULT(1),
        /**
         * Don't run a version check, just find the latest update and download it.
         */
        NO_VERSION_CHECK(2),
        /**
         * Get information about the version and the download size, but don't actually download anything.
         */
        NO_DOWNLOAD(3);

        private static final Map<Integer, Updater.UpdateType> valueList = new HashMap<Integer, Updater.UpdateType>();
        private final int value;

        private UpdateType(int value) {
            this.value = value;
        }

        public int getValue() {
            return this.value;
        }

        public static Updater.UpdateType getResult(int value) {
            return valueList.get(value);
        }

        static {
            for (Updater.UpdateType result : Updater.UpdateType.values()) {
                valueList.put(result.value, result);
            }
        }
    }

    public Updater(Plugin plugin, String slug, File file, UpdateType type, boolean announce) {
        this.plugin = plugin;
        this.type = type;
        this.announce = announce;
        try {
            // Obtain the results of the project's file feed
            url = new URL(DBOUrl + slug + "/files.rss");
        } catch (MalformedURLException ex) {
            // The slug doesn't exist
            plugin.getLogger().warning("The author of this plugin has misconfigured their Auto Update system");
            plugin.getLogger().warning("The project slug added ('" + slug + "') is invalid, and does not exist on dev.bukkit.org");
            result = Updater.UpdateResult.FAIL_BADSLUG; // Bad slug! Bad!
        }
        if (url != null) {
            // Obtain the results of the project's file feed
            readFeed();
            if (versionCheck(versionTitle)) {
                String fileLink = getFile(versionLink);
                if (fileLink != null && type != UpdateType.NO_DOWNLOAD) {
                    String name = file.getName();
                    // If it's a zip file, it shouldn't be downloaded as the plugin's name
                    if (fileLink.endsWith(".zip")) {
                        String[] split = fileLink.split("/");
                        name = split[split.length - 1];
                    }
                    saveFile(new File("plugins/" + updateFolder), name, fileLink);
                } else {
                    result = UpdateResult.UPDATE_AVAILABLE;
                }
            }
        }
    }

    public Updater.UpdateResult getResult() {
        return result;
    }

    public long getFileSize() {
        return totalSize;
    }

    public String getLatestVersionString() {
        return versionTitle;
    }

    private void saveFile(File folder, String file, String u) {
        if (!folder.exists()) {
            folder.mkdir();
        }
        BufferedInputStream in = null;
        FileOutputStream fout = null;
        try {
            // Download the file
            URL url = new URL(u);
            int fileLength = url.openConnection().getContentLength();
            in = new BufferedInputStream(url.openStream());
            fout = new FileOutputStream(folder.getAbsolutePath() + "/" + file);

            byte[] data = new byte[BYTE_SIZE];
            int count;
            if (announce) plugin.getLogger().info("About to download a new update: " + versionTitle);
            long downloaded = 0;
            while ((count = in.read(data, 0, BYTE_SIZE)) != -1) {
                downloaded += count;
                fout.write(data, 0, count);
                int percent = (int) (downloaded * 100 / fileLength);
                if (announce & (percent % 10 == 0)) {
                    plugin.getLogger().info("Downloading update: " + percent + "% of " + fileLength + " bytes.");
                }
            }
            //Just a quick check to make sure we didn't leave any files from last time...
            for (File xFile : new File("plugins/" + updateFolder).listFiles()) {
                if (xFile.getName().endsWith(".zip")) {
                    xFile.delete();
                }
            }
            // Check to see if it's a zip file, if it is, unzip it.
            File dFile = new File(folder.getAbsolutePath() + "/" + file);
            if (announce) plugin.getLogger().info("Finished updating.");
        } catch (Exception ex) {
            plugin.getLogger().warning("The auto-updater tried to download a new update, but was unsuccessful.");
            result = Updater.UpdateResult.FAIL_DOWNLOAD;
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
                if (fout != null) {
                    fout.close();
                }
            } catch (Exception ex) {
            }
        }
    }

    private String getFile(String link) {
        String download = null;
        try {
            // Open a connection to the page
            URL url = new URL(link);
            URLConnection urlConn = url.openConnection();
            InputStreamReader inStream = new InputStreamReader(urlConn.getInputStream());
            BufferedReader buff = new BufferedReader(inStream);

            int counter = 0;
            String line;
            while ((line = buff.readLine()) != null) {
                counter++;
                // Search for the download link
                if (line.contains("<li class=\"user-action user-action-download\">")) {
                    // Get the raw link
                    download = line.split("<a href=\"")[1].split("\">Download</a>")[0];
                }
                // Search for size
                else if (line.contains("<dt>Size</dt>")) {
                    sizeLine = counter + 1;
                } else if (counter == sizeLine) {
                    String size = line.replaceAll("<dd>", "").replaceAll("</dd>", "");
                    multiplier = size.contains("MiB") ? 1048576 : 1024;
                    size = size.replace(" KiB", "").replace(" MiB", "");
                    totalSize = (long) (Double.parseDouble(size) * multiplier);
                }
            }
            urlConn = null;
            inStream = null;
            buff.close();
            buff = null;
        } catch (Exception ex) {
            ex.printStackTrace();
            plugin.getLogger().warning("The auto-updater tried to contact dev.bukkit.org, but was unsuccessful.");
            result = Updater.UpdateResult.FAIL_DBO;
            return null;
        }
        return download;
    }

    private boolean versionCheck(String title) {
        if (type != UpdateType.NO_VERSION_CHECK) {
            String version = plugin.getDescription().getVersion();
            if (title.split("v").length == 2) {
                String remoteVersion = title.split("v")[1].split(" ")[0]; // Get the newest file's version number
                if (hasTag(version) || version.equalsIgnoreCase(remoteVersion)) {
                    // We already have the latest version, or this build is tagged for no-update
                    result = Updater.UpdateResult.NO_UPDATE;
                    return false;
                }
            } else {
                // The file's name did not contain the string 'vVersion'
                plugin.getLogger().warning("The author of this plugin has misconfigured their Auto Update system");
                plugin.getLogger().warning("Files uploaded to BukkitDev should contain the version number, seperated from the name by a 'v', such as PluginName v1.0");
                plugin.getLogger().warning("Please notify the author (" + plugin.getDescription().getAuthors().get(0) + ") of this error.");
                result = Updater.UpdateResult.FAIL_NOVERSION;
                return false;
            }
        }
        return true;
    }

    private boolean hasTag(String version) {
        for (String string : noUpdateTag) {
            if (version.contains(string)) {
                return true;
            }
        }
        return false;
    }

    private void readFeed() {
        try {
            // Set header values intial to the empty string
            String title = "";
            String link = "";
            // First create a new XMLInputFactory
            XMLInputFactory inputFactory = XMLInputFactory.newInstance();
            // Setup a new eventReader
            InputStream in = read();
            XMLEventReader eventReader = inputFactory.createXMLEventReader(in);
            // Read the XML document
            while (eventReader.hasNext()) {
                XMLEvent event = eventReader.nextEvent();
                if (event.isStartElement()) {
                    if (event.asStartElement().getName().getLocalPart().equals(TITLE)) {
                        event = eventReader.nextEvent();
                        title = event.asCharacters().getData();
                        continue;
                    }
                    if (event.asStartElement().getName().getLocalPart().equals(LINK)) {
                        event = eventReader.nextEvent();
                        link = event.asCharacters().getData();
                        continue;
                    }
                } else if (event.isEndElement()) {
                    if (event.asEndElement().getName().getLocalPart().equals(ITEM)) {
                        // Store the title and link of the first entry we get - the first file on the list is all we need
                        versionTitle = title;
                        versionLink = link;
                        // All done, we don't need to know about older files.
                        break;
                    }
                }
            }
        } catch (XMLStreamException e) {
            throw new RuntimeException(e);
        }
    }

    private InputStream read() {
        try {
            return url.openStream();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}