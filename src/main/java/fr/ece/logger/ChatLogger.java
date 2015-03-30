package fr.ece.logger;

import java.io.IOException;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.logging.*;

/**
 * Created by Salil Junior on 3/29/2015.
 */
public class ChatLogger {
    private Logger logger;
        //contstructor
    public ChatLogger(boolean debugOn, String className){
        //create resource bundle
        Locale locale = Locale.getDefault();
        String localeResourceName = ResourceBundle.getBundle("Internationalization",locale).getBaseBundleName();

        //suppress,

        //create an instance of Logger
        //we only localize messages by passing a localiztion resource bundle
        logger = Logger.getLogger(className, localeResourceName);

        //set logger to use only custom handlers
        //this way, we can decide which handlers to use and onnly enable console handler if debug flag is set
        logger.setUseParentHandlers(false);

        //create file handler for logger
        try {
            //if debug flag is on, add console handler
            if(debugOn == true){
                logger.addHandler(new ConsoleHandler());
            }
            //set maximum size of file handler
            Handler fileHandler = new FileHandler("mainchatlogs.log", 2000000, 2, true);
            //add a custom formatter to output logs in human readable format
            fileHandler.setFormatter(new LogFormatter());
            //add handler to logger
            logger.addHandler(fileHandler);
            //set level of logging to log all messages
            logger.setLevel(Level.ALL);
            //logging messages
        }catch (SecurityException | IOException e){

        }
    }

    public void logException(Level level, String errorTitle, Exception ex){
        logger.log(level, errorTitle, ex);
    }
}
