package gitlet;

import java.io.File;
import java.io.Serializable;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.stream.Collectors;

public class StagingArea implements Serializable {

    /*
        filePath to blobId
     */
    private final Map<String, String> addition = new TreeMap<>();
    private final Map<String, String> removal = new TreeMap<>();
    private transient Map<String, String> tracked;

    public boolean add(File file) {
        String filePath = file.getPath();
        Blob blob = new Blob(file);
        String blobId = blob.getId();
        String trackedBlobId = tracked.get(filePath);
        if (blobId.equals(trackedBlobId)) {
            if (addition.remove(filePath) != null) {
                return true;
            }
            return removal.remove(filePath) != null;
        }
        addition.put(filePath, blobId);
        if (!blob.getSaveBlobFile().exists()) {
            blob.save();
        }
        return true;
    }

    public void clear() {
        addition.clear();
        removal.clear();
        save();
    }

    public Map<String, String> commit() {
        tracked.putAll(addition);
        tracked.entrySet().stream()
                .filter(entry -> !removal.containsKey(entry.getKey()))
                .collect(Collectors.toMap(Entry::getKey, Entry::getValue));
        clear();
        return tracked;
    }

    public boolean remove(File file) {
        String filePath = file.getPath();
        if (addition.remove(filePath) != null) {
            return true;
        }
        String trackedBlobId = tracked.get(filePath);
        if (trackedBlobId != null) {
            if (file.exists()) {
                MyUtils.removeFile(file);
            }
            removal.put(filePath, trackedBlobId);
            return true;
        }
        return false;
    }

    public void setTracked(Map<String, String> tracked) {
        this.tracked = tracked;
    }

    public void save() {
        Utils.writeObject(Repository.INDEX, this);
    }

    public boolean isEmpty() {
        return addition.isEmpty() && removal.isEmpty();
    }

    public Map<String, String> getAddition() {
        return addition;
    }

    public Map<String, String> getRemoval() {
        return removal;
    }

    public Map<String, String> getTracked() {
        return tracked;
    }
}
