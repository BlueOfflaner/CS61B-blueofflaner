package gitlet;

import static gitlet.MyUtils.getObjectFile;
import static gitlet.MyUtils.saveObjectFile;
import static gitlet.Utils.readContents;
import static gitlet.Utils.readObject;

import java.io.File;
import java.io.Serializable;

public class Blob implements Serializable {
    private String id;
    private File file;
    private String filePath;
    private File saveBlobFile;
    private byte[] contents;

    public Blob(File fileName) {
        file = fileName;
        filePath = file.getPath();
        contents = readContents(file);
        id = generatorId(filePath, contents);
        saveBlobFile = getObjectFile(Repository.BLOBS_DIR, id);
    }

    public void save() {
        saveObjectFile(saveBlobFile, this);
    }

    public String getId() {
        return id;
    }

    public File getSaveBlobFile() {
        return saveBlobFile;
    }

    public File getFile() {
        return file;
    }

    public String getFilePath() {
        return filePath;
    }

    public byte[] getContents() {
        return contents;
    }

    public static String generatorId(String filePath, byte[] contents) {
        return MyUtils.generatorId(filePath, contents);
    }

    public static Blob fromFile(String id) {
        return readObject(getObjectFile(Repository.BLOBS_DIR, id), Blob.class);
    }
}
