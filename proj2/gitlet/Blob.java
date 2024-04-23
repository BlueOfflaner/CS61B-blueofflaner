package gitlet;

import java.io.File;
import java.io.Serializable;
import static gitlet.MyUtils.*;
import static gitlet.Utils.*;

public class Blob implements Serializable {
    private String id;
    private File file;
    private String filePath;
    private File saveBlobFile;
    private byte[] contents;

    public Blob(File fileName) {
        file = fileName;
        filePath = file.getPath();
        id = generatorId(file.toString(), filePath);
        saveBlobFile = getObjectFile(id);
        contents = readContents(file);
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

    public static Blob fromFile(String id) {
        return readObject(getObjectFile(id), Blob.class);
    }
}
