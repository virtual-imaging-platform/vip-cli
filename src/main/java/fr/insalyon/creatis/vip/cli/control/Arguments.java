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
        options=new HashSet<>();
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
                action=TESTARGS;
                break;
            case "setapikey":
                action=SETAPIKEY;
                break;
            case "getapikey":
                action=GETAPIKEY;
                break;
            default:
                throw new ArgumentException("Option not correct.");
        }

        int it = 1;
        while (it < args.length) {
            if (args[it].length() < 2 || !args[it].substring(0, 1).equals("-")) {

                argsWithoutFlag.add(args[it]);
                it++;
            } else if (args[it].substring(0, 2).equals("--") ) {
                if ((it + 1) < args.length
                        && !args[it + 1].substring(0, 1).equals("--")) {
                    argsWithFlag.put(args[it].substring(2), args[it + 1]);
                    it += 2;
                } else {
                    throw new ArgumentException(args[it]+" has no value");
                }
            }
            else if (args[it].substring(0, 1).equals("-") && !args[it].substring(0, 2).equals("--")) {

                options.add(args[it].substring(1));
                it += 1;

            } else {
                throw new ArgumentException("");

            }
        }

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


}
