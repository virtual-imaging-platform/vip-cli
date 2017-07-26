package fr.insalyon.creatis.vip.cli.control;

import fr.insalyon.creatis.vip.cli.model.ArgumentException;

import java.util.*;

import static fr.insalyon.creatis.vip.cli.control.ArgType.*;

public class Arguments {
    private Map<String, String> argsWithFlag;
    private List<String> argsWithoutFlag;
    private Set<String> options;

    private ArgType action;

    public Arguments(String[] args) throws ArgumentException {
        if (args.length == 0) {
            throw new ArgumentException(("no option"));
        }

        argsWithFlag = new HashMap<>();
        argsWithoutFlag = new ArrayList<>();
        options = new HashSet<>();
        switch (args[0]) {
            case "execute":
                action = EXECUTE;
                break;
            case "status":
                action = STATUS;
                break;
            case "executions":
                action = EXECTUIONS;
                break;
            case "result":
                action = RESULT;
                break;
            case "kill":
                action = KILL;
                break;
            case "delete":
                action = DELETE;
                break;
            case "pipeline":
                action = PIPELINE;
                break;
            case "testargs":
                action = TESTARGS;
                break;
            case "setapikey":
                action = SETAPIKEY;
                break;
            case "getapikey":
                action = GETAPIKEY;
                break;
            case "relaunch":
                action = RELAUNCH;
                break;
            case "getgateinput":
                action = GETGATEINPUT;
                break;
            case "upload":
                action=UPLOAD;
                break;
            case "getgaterelease":
                action=GETGATERELEASE;
                break;
            default:
                throw new ArgumentException("Option not correct.");
        }

        int it = 1;
        while (it < args.length) {
            String currentArg = args[it];
            String nextArg = (it + 1) < args.length ? args[it + 1] : null;

            boolean isArgWithoutFlag = currentArg.length() < 2 || !currentArg.substring(0, 1).equals("-");

            if (isArgWithoutFlag) {
                argsWithoutFlag.add(args[it]);
                it++;
            } else if (isFlagArg(currentArg, nextArg)) {
                argsWithFlag.put(currentArg.substring(2), nextArg);
                it += 2;
            } else if (isOption(currentArg)) {
                options.add(currentArg.substring(1));
                it += 1;

            } else {
                throw new ArgumentException("");

            }
        }

    }

    private boolean isFlagArg(String arg, String nextArg) throws ArgumentException {

        if (!arg.startsWith("--")) return false;
        if (nextArg == null) throw new ArgumentException(arg + " has no value");
        if (nextArg.startsWith("-")) throw new ArgumentException(arg + " has no value");
        return true;
    }

    private boolean isOption(String arg) {
        if (arg.startsWith("-") || !arg.startsWith("--")) return true;
        return false;
    }

    public Map<String, String> getArgsWithFlag() {
        return argsWithFlag;
    }

    public List<String> getArgsWithoutFlag() {
        return argsWithoutFlag;
    }

    public Set<String> getOptions() {
        return options;
    }

    public ArgType getAction() {
        return action;
    }

    public boolean hasOption (String option) {
        return options.contains(option);
    }


}
