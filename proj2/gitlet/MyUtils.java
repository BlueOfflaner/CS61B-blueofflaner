package gitlet;

import static gitlet.Utils.join;
import static gitlet.Utils.message;
import static gitlet.Utils.sha1;
import static gitlet.Utils.writeObject;

import java.io.File;
import java.io.Serializable;

/**
 * @author blueofflaner <blueofflaner@gmail.com>
 * Created on 2024-04-19
 */
public class MyUtils {

    static void exit(FailureMessage msg, Object... args) {
        exit(msg.msg, args);
    }

    static void exit(String msg, Object... args) {
        message(msg, args);
        System.exit(0);
    }

    static String generatorId(Object... args) {
        return sha1(args);
    }

    static File getObjectFile(String id) {
        String dirName = getDirName(id);
        String fileName = getFileName(id);
        return join(Repository.OBJECTS_DIR, dirName, fileName);
    }

    static String getDirName(String id) {
        return id.substring(0, 2);
    }

    static String getFileName(String id) {
        return id.substring(2);
    }

    public static void saveObjectFile(File file, Serializable obj) {
        File dir = file.getParentFile();
        if (!dir.exists()) {
            dir.mkdir();
        }
        writeObject(file, obj);
    }

    public static void removeFile(File file) {
        if (!file.delete()) {
            throw new IllegalArgumentException(String
                    .format("rm: %s: Failed to delete.", file.getPath()));
        }
    }
}
