package gitlet;

/**
 * @author blueofflaner <blueofflaner@gmail.com>
 * Created on 2024-04-19
 */
public enum FailureMessage {
    EMPTY_COMMAND("Please enter a command."),
    INVAlID_ARGS("Incorrect operands."),
    COMMAND_NOT_EXIST("No command with that name exists."),
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
    CHECKOUT_UNTRACKED_FILE_EXIST
            ("There is an untracked file in the way; delete it, or add and commit it first."),
    COMMIT_ID_TOO_SHORT("The commit id must contain at least 6 characters"),
    MULTI_COMMIT_MATCHING("There are more than 1 commit id matching this shortened id"),
    BRANCH_ALREADY_EXIST("A branch with that name already exists."),
    REMOVE_BRANCH_CAN_NOT_REMOVE_CURRENT_BRANCH("Cannot remove the current branch."),
    REMOVE_BRANCH_NOT_EXIST("A branch with that name does not exist.");

    final String msg;

    FailureMessage(String s) {
        this.msg = s;
    }
}
