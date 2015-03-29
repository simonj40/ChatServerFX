package fr.ece.logger;

import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

/**
 * Created by Salil Junior on 3/29/2015.
 */
public class LogFormatter extends Formatter {

    @Override
    public String format(LogRecord record){
        return record.getThreadID()+"::"+record.getSourceClassName()+"::"
                +record.getSourceMethodName()+"::"
                +new Date(record.getMillis())+"::"
                +formatMessage(record)+"\n";
    }

}
