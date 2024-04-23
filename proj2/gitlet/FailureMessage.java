package gitlet;

/**
 * @author blueofflaner <blueofflaner@gmail.com>
 * Created on 2024-04-19
 */
public enum FailureMessage {
    EMPTY_COMMAND("Please enter a command."),
    INVAlID_ARGS("Incorrect operands."),
    INIT("A Gitlet version-control system already exists in the current directory."),
    WITHOUT_INIT("Not in an initialized Gitlet directory."),
    COMMIT_NO_CHANGE("No changes added to the commit."),
    COMMIT_NO_MESSAGE("Please enter a commit message."),
    ADD_NO_FILE("File does not exist."),
    REMOVE_NO_REASON("No reason to remove the file."),
    FIND_NO_COMMIT("Found no commit with that message."),
    CHECKOUT_FILE_NOT_EXIST("File does not exist in that commit."),
    CHECKOUT_COMMIT_NOT_EXIST("No commit with that id exists."),
    CHECKOUT_BRANCH_NOT_EXIST("No such branch exists."),
    CHECKOUT_CURRENT_BRANCH("No need to checkout the current branch."),
    CHECKOUT_UNTRACKED_FILE_EXIST("There is an untracked file in the way; delete it, or add and commit it first.");

    String msg;
    FailureMessage(String s) {
        this.msg = s;
    }
}
