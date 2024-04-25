package gitlet;

import static gitlet.MyUtils.exit;
import static gitlet.MyUtils.getDirName;
import static gitlet.MyUtils.removeFile;
import static gitlet.Utils.join;
import static gitlet.Utils.plainFilenamesIn;
import static gitlet.Utils.readContents;
import static gitlet.Utils.readContentsAsString;
import static gitlet.Utils.readObject;
import static gitlet.Utils.writeContents;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;

/**
 * Represents a gitlet repository.
 * <p>
 * does at a high level.
 *
 * @author blueofflaner
 */
public class Repository {
    /**
     *
     * List all instance variables of the Repository class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided two examples for you.
     */

    /**
     * The current working directory.
     */
    public static File CWD = new File(System.getProperty("user.dir"));
    /**
     * The .gitlet directory.
     */
    public static File GITLET_DIR = join(CWD, ".gitlet");
    /**
     * The HEAD file.
     */
    public static File HEAD = join(GITLET_DIR, "HEAD");
    private static final String HEAD_BRANCH_REF_PREFIX = "ref: refs/heads/";

    /**
     * The INDEX file.
     */
    public static File INDEX = join(GITLET_DIR, "INDEX");
    /**
     * The objects' directory.
     */
    public static File OBJECTS_DIR = join(GITLET_DIR, "objects");
    public static File COMMITS_DIR = join(OBJECTS_DIR, "commits");
    public static File BLOBS_DIR = join(OBJECTS_DIR, "blobs");
    /**
     * The refs' directory.
     */
    public static File REFS_DIR = join(GITLET_DIR, "refs");
    /**
     * The refs/heads directory.
     */
    public static File BRANCH_HEADS_DIR = join(REFS_DIR, "heads");

    private static String DEFAULT_BRANCH_NAME = "master";

    public static void refresh() {
        GITLET_DIR = join(CWD, ".gitlet");
        HEAD = join(GITLET_DIR, "HEAD");
        INDEX = join(GITLET_DIR, "INDEX");
        OBJECTS_DIR = join(GITLET_DIR, "objects");
        COMMITS_DIR = join(OBJECTS_DIR, "commits");
        BLOBS_DIR = join(OBJECTS_DIR, "blobs");
        REFS_DIR = join(GITLET_DIR, "refs");
        BRANCH_HEADS_DIR = join(REFS_DIR, "heads");
    }

    public static void init() {
        if (GITLET_DIR.exists()) {
            exit(FailureMessage.INIT);
        }
        GITLET_DIR.mkdir();
        OBJECTS_DIR.mkdir();
        COMMITS_DIR.mkdir();
        BLOBS_DIR.mkdir();
        REFS_DIR.mkdir();
        BRANCH_HEADS_DIR.mkdir();
        setCurrentBranch(DEFAULT_BRANCH_NAME);
        new StagingArea().save();
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
            assert current != null;
            logBuilder.append(current.getLog()).append("\n");
            List<String> parents = current.getParents();
            if (parents.isEmpty()) {
                break;
            }
            current = Commit.fromFile(parents.get(0));
        }
        System.out.print(logBuilder);
    }

    public static void globalLog() {
        List<Commit> commits = getAllCommits();
        StringBuilder logBuilder = new StringBuilder();
        for (Commit commit : commits) {
            logBuilder.append(commit.getLog()).append("\n");
        }
        System.out.print(logBuilder);
    }

    public static void find(String msg) {
        List<Commit> commits = getAllCommits();
        StringBuilder sb = new StringBuilder();
        commits.forEach(commit -> {
            if (commit.getMessage().equals(msg)) {
                sb.append(commit.getId()).append("\n");
            }
        });
        if (sb.length() == 0) {
            exit(FailureMessage.FIND_NO_COMMIT);
        }
        System.out.print(sb);
    }

    public static void status() {
        StringBuilder statusBuilder = new StringBuilder();
        statusBuilder.append("=== Branches ===").append("\n");
        List<String> branchHeads = plainFilenamesIn(BRANCH_HEADS_DIR);
        String currentBranch = getCurrentBranch();
        assert branchHeads != null;
        for (String branch : branchHeads) {
            if (branch.equals(currentBranch)) {
                statusBuilder.append("*");
            }
            statusBuilder.append(branch).append("\n");
        }
        statusBuilder.append("\n");

        StagingArea stagingArea = getStagingArea();
        statusBuilder.append("=== Staged Files ===").append("\n");
        for (String filePath : stagingArea.getAddition().keySet()) {
            statusBuilder.append(Paths.get(filePath).getFileName()).append("\n");
        }
        statusBuilder.append("\n");

        statusBuilder.append("=== Removed Files ===").append("\n");
        for (String filePath : stagingArea.getRemoval().keySet()) {
            statusBuilder.append(Paths.get(filePath).getFileName()).append("\n");
        }
        statusBuilder.append("\n");

        //TODO extra credit
        statusBuilder.append("=== Modifications Not Staged For Commit ===").append("\n");
        statusBuilder.append(getModifiedNotStagedFiles());
        statusBuilder.append("\n");

        statusBuilder.append("=== Untracked Files ===").append("\n");
        statusBuilder.append(getUntrackedFiles());
        statusBuilder.append("\n");

        System.out.println(statusBuilder);
    }

    public static void branch(String branchName) {
        File branchHead = getBranchHead(branchName);
        if (branchHead.exists()) {
            exit(FailureMessage.BRANCH_ALREADY_EXIST);
        }
        setBranchHeadCommit(branchHead, getHeadCommit().getId());
    }

    public static void rmBranch(String branchName) {
        if (getCurrentBranch().equals(branchName)) {
            exit(FailureMessage.REMOVE_BRANCH_CAN_NOT_REMOVE_CURRENT_BRANCH);
        }
        File branchHead = getBranchHead(branchName);
        if (!branchHead.exists()) {
            exit(FailureMessage.REMOVE_BRANCH_NOT_EXIST);
        }
        removeFile(branchHead);
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
        assert commit != null;
        checkUntrackedFile(commit);
        checkout(commitId, null);
        setCurrentBranch(branchName);
    }

    public static void checkout(String commitId, String fileName) {
        Commit commit = commitId == null ? getHeadCommit()
                                         : Commit.fromFile(getRealCommitId(commitId));
        if (commit == null) {
            exit(FailureMessage.CHECKOUT_COMMIT_NOT_EXIST);
        }
        if (fileName == null) {
            StagingArea stagingArea = getStagingArea();
            stagingArea.clear();
            List<File> files = getCurrentFiles();
            for (File file : files) {
                removeFile(file);
            }
            assert commit != null;
            Map<String, String> tracked = commit.getTracked();
            for (String blobId : tracked.values()) {
                Blob blob = Blob.fromFile(blobId);
                writeContents(blob.getFile(), blob.getContents());
            }
            return;
        }
        assert commit != null;
        File file = getFileFromCWD(fileName);
        if (!commit.getTracked().containsKey(file.getPath())) {
            exit(FailureMessage.CHECKOUT_FILE_NOT_EXIST);
        } else {
            removeFile(file);
            String blobId = commit.getTracked().get(file.getPath());
            Blob blob = Blob.fromFile(blobId);
            writeContents(blob.getFile(), blob.getContents());
        }
    }

    public static void reset(String commitId) {
        Commit commit = Commit.fromFile(getRealCommitId(commitId));
        if (commit == null) {
            exit(FailureMessage.CHECKOUT_COMMIT_NOT_EXIST);
        }
        assert commit != null;
        checkUntrackedFile(commit);
        checkout(commitId, null);
        setBranchHeadCommit(getCurrentBranch(), commitId);
    }

    public static void merge(String mergedBranch) {
        String currentBranch = getCurrentBranch();
        if (mergedBranch.equals(currentBranch)) {
            exit(FailureMessage.MERGE_CAN_NOT_MERGE_ITSELF);
        }
        File branchHead = getBranchHead(mergedBranch);
        if (!branchHead.exists()) {
            exit(FailureMessage.MERGE_BRANCH_NOT_EXIST);
        }
        StagingArea stagingArea = getStagingArea();
        if (!stagingArea.isEmpty()) {
            exit(FailureMessage.MERGE_UNCOMMITTED_CHANGES);
        }
        Commit mergedHeadCommit = getBranchHeadCommit(branchHead);
        checkUntrackedFile(mergedHeadCommit);
        Commit currentHeadCommit = getHeadCommit();
        Commit lca = getLatestCommonAncestorCommit(currentHeadCommit, mergedHeadCommit);
        String lcaId = lca.getId();
        if (lcaId.equals(currentHeadCommit.getId())) {
            checkout(mergedHeadCommit.getId(), null);
            setCurrentBranch(mergedBranch);
            exit(FailureMessage.MERGE_CURRENT_BRANCH_EQUALS_SPLIT_POINT);
        } else if (lcaId.equals(mergedHeadCommit.getId())) {
            exit(FailureMessage.MERGE_MERGED_BRANCH_EQUALS_SPLIT_POINT);
        }

        boolean isConflict = filterNewCommitFiles(currentHeadCommit, mergedHeadCommit, lca);

        String msg = String.format("Merged %s into %s.", mergedBranch, currentBranch);
        Repository.commit(msg, mergedHeadCommit.getId(), false);
        // Repository.rmBranch(mergedBranch);
        if (isConflict) {
            System.out.println("Encountered a merge conflict.");
        }
    }

    private static String getModifiedNotStagedFiles() {
        StringBuilder stagedThenModifiedBuilder = new StringBuilder();
        Map<String, String> fileMap = getFileMapFromCWD();
        Map<String, String> tracked = getHeadCommit().getTracked();
        StagingArea stagingArea = getStagingArea();
        Map<String, String> stagingAreaFilter = new TreeMap<>();
        stagingAreaFilter.putAll(stagingArea.getAddition());
        stagingAreaFilter.putAll(stagingArea.getRemoval());
        for (String filePath : tracked.keySet()) {
            if (stagingAreaFilter.containsKey(filePath)) {
                continue;
            }
            if (!fileMap.containsKey(filePath)) {
                stagedThenModifiedBuilder.append(join(filePath).getName());
                stagedThenModifiedBuilder.append(" (deleted)");
                stagedThenModifiedBuilder.append("\n");
            } else if (!fileMap.get(filePath).equals(tracked.get(filePath))) {
                stagedThenModifiedBuilder.append(join(filePath).getName());
                stagedThenModifiedBuilder.append(" (modified)");
                stagedThenModifiedBuilder.append("\n");
            }
        }
        return stagedThenModifiedBuilder.toString();
    }

    private static String getUntrackedFiles() {
        StringBuilder untrackedBuilder = new StringBuilder();
        List<File> files = getCurrentFiles();
        Map<String, String> tracked = getHeadCommit().getTracked();
        StagingArea stagingArea = getStagingArea();
        Map<String, String> stagingAreaFilter = new TreeMap<>();
        stagingAreaFilter.putAll(stagingArea.getAddition());
        stagingAreaFilter.putAll(stagingArea.getRemoval());
        for (File file : files) {
            if (stagingAreaFilter.containsKey(file.getAbsolutePath())) {
                continue;
            }
            if (!tracked.containsKey(file.getAbsolutePath())) {
                untrackedBuilder.append(file.getName());
                untrackedBuilder.append("\n");
            }
        }
        return untrackedBuilder.toString();
    }

    private static String getConflictContent(String currentBlobId, String mergedBlobId) {
        StringBuilder contentBuilder = new StringBuilder();
        contentBuilder.append("<<<<<<< HEAD").append("\n");
        if (currentBlobId != null) {
            Blob currentBlob = Blob.fromFile(currentBlobId);
            contentBuilder.append(currentBlob.getContentAsString());
        }
        contentBuilder.append("=======").append("\n");
        if (mergedBlobId != null) {
            Blob mergedBlob = Blob.fromFile(mergedBlobId);
            contentBuilder.append(mergedBlob.getContentAsString());
        }
        contentBuilder.append(">>>>>>>").append("\n");
        return contentBuilder.toString();
    }

    /*
        因为这并不是个树。实际上是个有向无环图，一个结点可能有两个 parent，考虑如下情况
        a - b - b+c
             \ /
              c
             / \
        d - e - e+c
        很明显 c 才是 b+c 和 e+c 两个结点的 split point
     */
    private static Commit getLatestCommonAncestorCommit(Commit commitA, Commit commitB) {
        Comparator<Commit> commitComparator = Comparator.comparing(Commit::getDate).reversed();
        Queue<Commit> commitsQueue = new PriorityQueue<>(commitComparator);
        Set<String> checkedCommitIds = new HashSet<>();
        commitsQueue.add(commitA);
        while (!commitsQueue.isEmpty()) {
            Commit latestCommit = commitsQueue.poll();
            checkedCommitIds.add(latestCommit.getId());
            List<String> parents = latestCommit.getParents();
            if (parents.size() > 0) {
                Commit firstParent = Commit.fromFile(parents.get(0));
                commitsQueue.add(firstParent);
            }
            if (parents.size() > 1) {
                Commit secondParent = Commit.fromFile(parents.get(1));
                commitsQueue.add(secondParent);
            }
        }

        commitsQueue.add(commitB);
        while (!commitsQueue.isEmpty()) {
            Commit latestCommit = commitsQueue.poll();
            if (checkedCommitIds.contains(latestCommit.getId())) {
                return latestCommit;
            }
            checkedCommitIds.add(latestCommit.getId());
            List<String> parents = latestCommit.getParents();
            if (parents.size() > 0) {
                Commit firstParent = Commit.fromFile(parents.get(0));
                if (checkedCommitIds.contains(parents.get(0))) {
                    return firstParent;
                }
                commitsQueue.add(firstParent);
            }
            if (parents.size() > 1) {
                Commit secondParent = Commit.fromFile(parents.get(1));
                if (checkedCommitIds.contains(parents.get(1))) {
                    return secondParent;
                }
                commitsQueue.add(secondParent);
            }
        }
        return null;
    }

    private static boolean filterNewCommitFiles(Commit currentHeadCommit,
            Commit mergedHeadCommit, Commit splitPointCommit) {
        Map<String, String> currentHeadCommitTracked = currentHeadCommit.getTracked();
        Map<String, String> mergedHeadCommitTracked = mergedHeadCommit.getTracked();
        Map<String, String> splitPointCommitTracked = splitPointCommit.getTracked();
        Set<String> filePaths = new HashSet<>();
        filePaths.addAll(currentHeadCommitTracked.keySet());
        filePaths.addAll(mergedHeadCommitTracked.keySet());
        filePaths.addAll(splitPointCommitTracked.keySet());
        StagingArea stagingArea;
        boolean isConflict = false;
        for (String filePath : filePaths) {
            String currentBlobId = currentHeadCommitTracked.get(filePath);
            String mergedBlobId = mergedHeadCommitTracked.get(filePath);
            String splitPointBlobId = splitPointCommitTracked.get(filePath);
            if (currentHeadCommitTracked.containsKey(filePath)
                    && mergedHeadCommitTracked.containsKey(filePath)) { // modified in other and HEAD in same way
                if (currentBlobId.equals(mergedBlobId)) {
                    Repository.add(join(filePath).getName());
                    continue;
                }
                if (!currentBlobId.equals(splitPointBlobId)
                        && !mergedBlobId.equals(splitPointBlobId)) { // modified in other and HEAD in diff way
                    isConflict = true;
                    writeContents(join(filePath), getConflictContent(currentBlobId, mergedBlobId));
                    Repository.add(join(filePath).getName());
                } else if (currentBlobId.equals(splitPointBlobId)) { // modified in other but not HEAD -> other
                    Blob mergedBlob = Blob.fromFile(mergedBlobId);
                    writeContents(mergedBlob.getFile(), mergedBlob.getContents());
                    stagingArea = getStagingArea();
                    stagingArea.add(mergedBlob.getFile());
                    stagingArea.save();
                } else { // modified in HEAD but not other -> HEAD
                    Repository.add(join(filePath).getName());
                }
                continue;
            }
            if (splitPointCommitTracked.containsKey(filePath)) {
                if (currentHeadCommitTracked.containsKey(filePath)
                        && !mergedHeadCommitTracked.containsKey(filePath)) {
                    if (splitPointBlobId.equals(currentBlobId)) {
                        // unmodified in HEAD but not present in other -> REMOVE
                        Repository.remove(join(filePath).getName());
                    } else { // modified in HEAD but removed in other -> conflict
                        isConflict = true;
                        writeContents(join(filePath), getConflictContent(currentBlobId, mergedBlobId));
                        Repository.add(join(filePath).getName());
                    }
                    continue;
                }
                // unmodified in other but not present in HEAD -> REMAIN REMOVE
                if (!currentHeadCommitTracked.containsKey(filePath)
                        && mergedHeadCommitTracked.containsKey(filePath)) {
                    if (!splitPointBlobId.equals(mergedBlobId)) {
                        // modified in other but removed in HEAD -> conflict
                        isConflict = true;
                        String conflictContent = getConflictContent(currentBlobId, mergedBlobId);
                        writeContents(join(filePath), conflictContent);
                        Repository.add(join(filePath).getName());
                    }
                }
            } else {
                if (currentHeadCommitTracked.containsKey(filePath)
                        && !mergedHeadCommitTracked.containsKey(filePath)) {
                    // not in split non other but in HEAD -> HEAD
                    Repository.add(join(filePath).getName());
                    continue;
                }
                if (!currentHeadCommitTracked.containsKey(filePath)
                        && mergedHeadCommitTracked.containsKey(filePath)) {
                    // not in split non HEAD but in other -> other
                    Blob mergedBlob = Blob.fromFile(mergedBlobId);
                    writeContents(mergedBlob.getFile(), mergedBlob.getContents());
                    stagingArea = getStagingArea();
                    stagingArea.add(mergedBlob.getFile());
                    stagingArea.save();
                }
            }
        }
        return isConflict;
    }

    private static String getRealCommitId(String commitId) {
        if (commitId.length() < 6) {
            exit(FailureMessage.COMMIT_ID_TOO_SHORT);
        }
        String dirName = getDirName(commitId);
        File dir = join(COMMITS_DIR, dirName);
        if (!dir.exists()) {
            exit(FailureMessage.CHECKOUT_COMMIT_NOT_EXIST);
        }
        String[] fileNames = Objects.requireNonNull(dir.list());
        List<String> res = new ArrayList<>();
        commitId = commitId.substring(2);
        for (String fileName : fileNames) {
            if (commitId.equals(fileName.substring(0, commitId.length()))) {
                res.add(dirName + fileName);
            }
        }
        if (res.isEmpty()) {
            exit(FailureMessage.CHECKOUT_COMMIT_NOT_EXIST);
        }
        if (res.size() > 1) {
            exit(FailureMessage.MULTI_COMMIT_MATCHING);
        }
        return res.get(0);
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

    private static void setBranchHeadCommit(File branchHead, String commitId) {
        writeContents(branchHead, commitId);
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
        return getBranchHeadCommit(getBranchHead(getCurrentBranch()));
    }

    private static Commit getBranchHeadCommit(File branchHead) {
        return Commit.fromFile(readContentsAsString(branchHead));
    }

    private static StagingArea getStagingArea() {
        StagingArea stagingArea = INDEX.exists() ? readObject(INDEX, StagingArea.class)
                                                 : new StagingArea();
        stagingArea.setTracked(getHeadCommit().getTracked());
        return stagingArea;
    }

    private static List<File> getCurrentFiles() {
        List<File> files;
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

    private static Map<String, String> getFileMapFromCWD() {
        Map<String, String> map = new TreeMap<>();
        List<File> files = getCurrentFiles();
        for (File file : files) {
            String filePath = file.getAbsolutePath();
            String blobId = Blob.generatorId(filePath, readContents(file));
            map.put(filePath, blobId);
        }
        return map;
    }

    private static List<Commit> getAllCommits() {
        List<Commit> list = new ArrayList<>();
        File commitDir = join(COMMITS_DIR);
        String[] dirNames = Objects.requireNonNull(commitDir.list());
        for (String dirName : dirNames) {
            List<String> filenames = plainFilenamesIn(join(commitDir, dirName));
            assert filenames != null;
            for (String fileName : filenames) {
                Commit commit = Commit.fromFile(dirName + fileName);
                list.add(commit);
            }
        }
        return list;
    }
}
