package gitlet;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.TreeMap;
import java.util.stream.Collectors;

import static gitlet.Utils.*;
import static gitlet.MyUtils.*;

// TODO: any imports you need here

/**
 * Represents a gitlet repository.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 * @author TODO
 */
public class Repository {
    /**
     * TODO: add instance variables here.
     *
     * List all instance variables of the Repository class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided two examples for you.
     */

    /**
     * The current working directory.
     */
    public static final File CWD = new File(System.getProperty("user.dir"));
    /**
     * The .gitlet directory.
     */
    public static final File GITLET_DIR = join(CWD, ".gitlet");
    /**
     * The HEAD file.
     */
    public static final File HEAD = join(GITLET_DIR, "HEAD");
    private static final String HEAD_BRANCH_REF_PREFIX = "ref: refs/heads/";

    /**
     * The INDEX file.
     */
    public static final File INDEX = join(GITLET_DIR, "INDEX");
    /**
     * The objects' directory.
     */
    public static final File OBJECTS_DIR = join(GITLET_DIR, "objects");
    /**
     * The refs' directory.
     */
    public static final File REFS_DIR = join(GITLET_DIR, "refs");
    /**
     * The refs/heads directory.
     */
    public static final File BRANCH_HEADS_DIR = join(REFS_DIR, "heads");

    private static final String DEFAULT_BRANCH_NAME = "master";


    /* TODO: fill in the rest of this class. */
    public static void init() {
        if (GITLET_DIR.exists()) {
            exit(FailureMessage.INIT);
        }
        GITLET_DIR.mkdir();
        OBJECTS_DIR.mkdir();
        REFS_DIR.mkdir();
        BRANCH_HEADS_DIR.mkdir();
        setCurrentBranch(DEFAULT_BRANCH_NAME);
        commit("initial commit", null, true);
    }

    public static void commit(String msg) {
        commit(msg, null, false);
    }

    public static void commit(String msg, String secondParent, boolean isInit) {
        List<String> parents = new ArrayList<>();
        Map<String, String> tracked = new TreeMap<>();
        if (!isInit) {
            StagingArea stagingArea = getStagingArea();
            if (stagingArea.isEmpty()) {
                exit(FailureMessage.COMMIT_NO_CHANGE);
            }
            tracked = stagingArea.commit();
            parents.add(getHeadCommit().getId());
            if (secondParent != null) {
                parents.add(secondParent);
            }
        }
        Commit newCommit = new Commit(msg, tracked, parents, isInit);
        newCommit.save();
        setBranchHeadCommit(getCurrentBranch(), newCommit.getId());
    }

    public static void add(String fileName) {
        File file = getFileFromCWD(fileName);
        if (!file.exists()) {
            exit(FailureMessage.ADD_NO_FILE);
        }
        StagingArea stagingArea = getStagingArea();
        if (stagingArea.add(file)) {
            stagingArea.save();
        }
    }

    public static void remove(String fileName) {
        File file = getFileFromCWD(fileName);
        StagingArea stagingArea = getStagingArea();
        if (stagingArea.remove(file)) {
            stagingArea.save();
        } else {
            exit(FailureMessage.REMOVE_NO_REASON);
        }
    }

    public static void log() {
        Commit current = getHeadCommit();
        StringBuilder logBuilder = new StringBuilder();
        while (true) {
            logBuilder.append(current.getLog()).append("\n");
            List<String> parents = current.getParents();
            if (parents.isEmpty()) {
                break;
            }
            current = Commit.fromFile(parents.get(0));
        }
        System.out.println(logBuilder);
    }

    public static void globalLog() {
        Queue<Commit> commits = getAllCommits();
        StringBuilder logBuilder = new StringBuilder();
        while (!commits.isEmpty()) {
            Commit poll = commits.poll();
            logBuilder.append(poll.getLog()).append("\n");
        }
        System.out.println(logBuilder);
    }

    public static void find(String msg) {
        Queue<Commit> commits = getAllCommits();
        StringBuilder sb = new StringBuilder();
        commits.forEach(
                commit -> {
                    if (commit.getMessage().equals(msg)) {
                        sb.append(commit.getId()).append("\n");
                    }
                }
        );
        if (sb.length() == 0) {
            exit(FailureMessage.FIND_NO_COMMIT);
        }
        System.out.println(sb);
    }

    public static void checkout(String branchName) {
        File branchHead = getBranchHead(branchName);
        if (!branchHead.exists()) {
            exit(FailureMessage.CHECKOUT_BRANCH_NOT_EXIST);
        }
        if (branchName.equals(getCurrentBranch())) {
            exit(FailureMessage.CHECKOUT_CURRENT_BRANCH);
        }
        String commitId = readContentsAsString(branchHead);
        Commit commit = Commit.fromFile(commitId);
        checkUntrackedFile(commit);
        checkout(commitId, null);
        setCurrentBranch(branchName);
    }

    public static void checkout(String commitId, String fileName) {
        //TODO complete checkout
        Commit commit = commitId == null ? getHeadCommit() : Commit.fromFile(commitId);



        if (fileName == null) {
            StagingArea stagingArea = getStagingArea();
            stagingArea.clear();
            List<File> files = getCurrentFiles();
            for (File file : files) {
                removeFile(file);
            }
            Map<String, String> tracked = commit.getTracked();
            for (String blobId : tracked.values()) {
                Blob blob = Blob.fromFile(blobId);
                writeContents(blob.getFile(), blob.getContents());
            }
        }
    }

    public static void checkWorkingDir() {
        if (!(GITLET_DIR.exists() && GITLET_DIR.isDirectory())) {
            exit(FailureMessage.WITHOUT_INIT);
        }
    }

    private static void checkUntrackedFile(Commit target) {
        Map<String, String> fileMap = getFileMapFromCWD();
        StagingArea stagingArea = getStagingArea();
        Map<String, String> addition = stagingArea.getAddition();
        Map<String, String> removal = stagingArea.getRemoval();
        Map<String, String> tracked = stagingArea.getTracked();
        Map<String, String> targetTracked = target.getTracked();
        Map<String, String> untrackedFiles = new TreeMap<>();

        for (String filePath : fileMap.keySet()) {
            if (tracked.containsKey(filePath)) {
                if (removal.containsKey(filePath)) {
                    untrackedFiles.put(filePath, fileMap.get(filePath));
                }
            } else {
                if (!addition.containsKey(filePath)) {
                    untrackedFiles.put(filePath, fileMap.get(filePath));
                }
            }
        }

        for (Entry entry : untrackedFiles.entrySet()) {
            if (!entry.getValue().equals(targetTracked.get(entry.getKey()))) {
                exit(FailureMessage.CHECKOUT_UNTRACKED_FILE_EXIST);
            }
        }
    }

    private static void setCurrentBranch(String branchName) {
        writeContents(HEAD, HEAD_BRANCH_REF_PREFIX + branchName);
    }

    private static void setBranchHeadCommit(String branchName, String commitId) {
        writeContents(getBranchHead(branchName), commitId);
    }

    private static String getCurrentBranch() {
        String path = readContentsAsString(HEAD);
        return path.replace(HEAD_BRANCH_REF_PREFIX, "");
    }

    private static File getBranchHead(String branchName) {
        return join(BRANCH_HEADS_DIR, branchName);
    }

    private static File getFileFromCWD(String fileName) {
        return Paths.get(fileName).isAbsolute()
               ? new File(fileName)
               : join(CWD, fileName);
    }

    private static Commit getHeadCommit() {
        File currentHead = getBranchHead(getCurrentBranch());
        String commitId = readContentsAsString(currentHead);
        return Commit.fromFile(commitId);
    }

    private static StagingArea getStagingArea() {
        StagingArea stagingArea = INDEX.exists() ? readObject(INDEX, StagingArea.class) : new StagingArea();
        stagingArea.setTracked(getHeadCommit().getTracked());
        return stagingArea;
    }

    private static List<File> getCurrentFiles() {
        List<File> files = null;
        try {
            files = Files.walk(Paths.get(CWD.getPath()))
                    .filter(Files::isRegularFile)
                    .filter(path -> !path.startsWith(GITLET_DIR.getPath()))
                    .map(Path::toFile)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return files;
    }
    //TODO 可能有 bug，等待测试
    private static Map<String, String> getFileMapFromCWD() {
        Map<String, String> map = new TreeMap<>();
        List<File> files = getCurrentFiles();
        for (File file : files) {
            String filePath = file.getAbsolutePath();
            String blobId = generatorId(file.toString(), filePath);
            map.put(filePath, blobId);
        }
        return map;
    }

    //TODO 可能会有 bug，暂定
    private static Queue<Commit> getAllCommits() {
        Queue<Commit> queue = new PriorityQueue<>(Comparator.comparing(Commit::getDate));
        Queue<Commit> commits = new ArrayDeque<>();
        List<String> branchHeads = plainFilenamesIn(BRANCH_HEADS_DIR);
        assert branchHeads != null;
        branchHeads.stream().forEach(
                head -> {
                    File file = join(head);
                    String commitIds = readContentsAsString(file);
                    Commit commit = Commit.fromFile(commitIds);
                    commits.add(commit);
                }
        );
        while (!commits.isEmpty()) {
            Commit c = commits.poll();
            queue.add(c);
            for (String s : c.getParents()) {
                commits.add(Commit.fromFile(s));
            }
        }
        return queue;
    }
}
