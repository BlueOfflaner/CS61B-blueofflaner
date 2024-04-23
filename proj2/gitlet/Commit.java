package gitlet;

// TODO: any imports you need here

import static gitlet.MyUtils.generatorId;
import static gitlet.MyUtils.getObjectFile;
import static gitlet.MyUtils.saveObjectFile;
import static gitlet.Utils.readObject;

import java.io.File;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Represents a gitlet commit object.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 * @author TODO
 */
public class Commit implements Serializable {
    /**
     * TODO: add instance variables here.
     * <p>
     * List all instance variables of the Commit class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided one example for `message`.
     */

    private String id;
    private Date date;
    /**
     * The message of this Commit.
     */
    private String message;
    private File file;
    private Map<String, String> tracked;
    private List<String> parents;

    /* TODO: fill in the rest of this class. */


    public Commit(String msg, Map<String, String> tracked, List<String> parents, boolean isInit) {
        if (isInit) {
            this.date = new Date(0);
        } else {
            this.date = new Date();
        }
        this.message = msg;
        this.id = generatorId(getTimestamp(), message);
        this.file = getObjectFile(id);
        this.tracked = tracked;
        this.parents = parents;
    }

    public void save() {
        saveObjectFile(file, this);
    }

    public String getId() {
        return id;
    }

    public String getTimestamp() {
        // Thu Jan 1 00:00:00 1970 +0000
        DateFormat dateFormat = new SimpleDateFormat("EEE MMM d HH:mm:ss yyyy Z", Locale.ENGLISH);
        return dateFormat.format(date);
    }

    public Map<String, String> getTracked() {
        return tracked;
    }

    public List<String> getParents() {
        return parents;
    }

    public String getMessage() {
        return message;
    }

    public Date getDate() {
        return date;
    }

    public String getLog() {
        StringBuilder logBuilder = new StringBuilder();
        logBuilder.append("===").append("\n");
        logBuilder.append("commit").append(" ").append(id).append("\n");
        if (parents.size() > 1) {
            logBuilder.append("Merge:");
            for (String parent : parents) {
                logBuilder.append(" ").append(parent, 0, 7);
            }
            logBuilder.append("\n");
        }
        logBuilder.append("Date:").append(" ").append(getTimestamp()).append("\n");
        logBuilder.append(message).append("\n");
        return logBuilder.toString();
    }

    public static Commit fromFile(String id) {
        File commitFile = getObjectFile(id);
        return readObject(commitFile, Commit.class);
    }
}
