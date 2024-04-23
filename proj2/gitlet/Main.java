package gitlet;

import static gitlet.MyUtils.exit;
import static gitlet.FailureMessage.*;

/** Driver class for Gitlet, a subset of the Git version-control system.
 *  @author TODO
 */
public class Main {

    /** Usage: java gitlet.Main ARGS, where ARGS contains
     *  <COMMAND> <OPERAND1> <OPERAND2> ... 
     */
    public static void main(String[] args) {
        if (args.length == 0) {
            exit(EMPTY_COMMAND);
        }

        String firstArg = args[0];
        switch(firstArg) {
            case "init": {
                validateNumArgs(args, 1);
                Repository.init();
                break;
            }
            case "add": {
                Repository.checkWorkingDir();
                validateNumArgs(args, 2);
                String fileName = args[1];
                Repository.add(fileName);
                break;
                // TODO: handle the `add [filename]` command
            }
            case "rm": {
                Repository.checkWorkingDir();
                validateNumArgs(args, 2);
                String fileName = args[1];
                Repository.remove(fileName);
                break;
            }
            case "commit": {
                Repository.checkWorkingDir();
                validateNumArgs(args, 2);
                String message = args[1];
                if (message.length() == 0) {
                    exit(COMMIT_NO_MESSAGE);
                }
                Repository.commit(message);
                break;
            }
            case "log": {
                Repository.checkWorkingDir();
                validateNumArgs(args, 1);
                Repository.log();
                break;
            }
            case "global-log": {
                Repository.checkWorkingDir();
                validateNumArgs(args, 1);
                Repository.globalLog();
                break;
            }
            case "checkout": {
                Repository.checkWorkingDir();
                switch (args.length) {
                    case 2: {
                        String branch = args[1];
                        Repository.checkout(branch);
                        break;
                    }
                    case 3: {
                        if (!args[1].equals("--")) {
                            exit(INVAlID_ARGS);
                        }
                        String fileName = args[2];
                        Repository.checkout(null, fileName);
                        break;
                    }
                    case 4: {
                        if (!args[2].equals("--")) {
                            exit(INVAlID_ARGS);
                        }
                        String commitId = args[1];
                        String fileName = args[3];
                        Repository.checkout(commitId, fileName);
                        break;
                    }
                    default: {
                        exit(INVAlID_ARGS);
                        break;
                    }
                }
                break;
            }
            // TODO: FILL THE REST IN
        }
    }

    private static void validateNumArgs(String[] args, int n) {
        if (args.length != n) {
            exit(INVAlID_ARGS);
        }
    }
}
