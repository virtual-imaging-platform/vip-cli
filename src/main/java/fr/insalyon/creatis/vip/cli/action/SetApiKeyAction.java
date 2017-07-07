package fr.insalyon.creatis.vip.cli.action;

import fr.insalyon.creatis.vip.cli.control.Arguments;

import java.io.*;
import java.util.Properties;

import static fr.insalyon.creatis.vip.cli.control.Controller.PROPERTIESPATH;
import static java.lang.System.exit;

/**
 * Created by qifan on 2017/7/5.
 */
public class SetApiKeyAction {
    Arguments args;
    public SetApiKeyAction(Arguments args) {
        this.args=args;
    }
    public void execute() {
        try {
           String apiKey=args.getArgsWithoutFlag().get(0);
            InputStream is = new FileInputStream(PROPERTIESPATH);
            Properties prop = new Properties();
            prop.load(is);
            is.close();
            FileOutputStream fos=new FileOutputStream(PROPERTIESPATH);
            prop.setProperty("APIKEY",apiKey);
            System.out.println(prop.getProperty("APIKEY"));;
            prop.store(fos,"Properties");
            fos.close();
        } catch (IndexOutOfBoundsException e) {
            System.err.println("Api key value not indicated.");
            exit(0);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
