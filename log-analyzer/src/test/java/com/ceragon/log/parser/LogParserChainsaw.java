package com.ceragon.log.parser;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.log4j.chainsaw.LogFilePatternLayoutBuilder;
import org.apache.log4j.lf5.LogLevel;
import org.apache.log4j.lf5.util.LogFileParser;
import org.apache.log4j.lf5.viewer.LogBrokerMonitor;
import org.apache.log4j.spi.LoggingEvent;
import org.apache.log4j.spi.ThrowableInformation;
import org.apache.log4j.varia.LogFilePatternReceiver;
import org.junit.Ignore;
import org.junit.Test;

public class LogParserChainsaw
{
    
    @Test
    @Ignore
    public void parseLogFile() throws Exception
    {
        // Arrange
        
        File logFile = new File("d:/repositories/svn/polyview/branches/N7_2_2_0_0_1830_patch/PolyView/logs/pvman.log");
        LogFileParser logFileParser = new LogFileParser(logFile);
        
        // Act
        
        LogBrokerMonitor monitor = new LogBrokerMonitor(LogLevel.getLog4JLevels());

        logFileParser.parse(monitor);

        monitor.setFrameSize(500, 500);
        monitor.setFontSize(12);
        
        monitor.show();

        // Assert
    }
    
    @Test
    public void parseLogFormat() throws Exception
    {
        final String timestampFormat = "yyyy-MM-dd HH:mm:ss,SSS";
        
        final SimpleDateFormat sdf = new SimpleDateFormat(timestampFormat);

        final LogFilePatternReceiver logFilePatternReceiver = new LogFilePatternReceiver()
            {
                
                @Override
                public void doPost(LoggingEvent event)
                {
                    System.out.println(toStringEvent(event));
                }
                
                private String toStringEvent(LoggingEvent event)
                {
                    if (event == null)
                    {
                        return "null";
                    }
                    
                    StringBuilder builder = new StringBuilder();
                    final String dateTime = sdf.format(new Date(event.getTimeStamp()));
                    builder.append(" >> ");
                    builder.append(dateTime);
                    builder.append(" ").append(event.getLoggerName());
                    builder.append(" ").append(event.getThreadName());
                    builder.append(" ").append(event.getLevel());
                    builder.append(" ").append(event.getMessage());

                    final ThrowableInformation throwableInformation = event.getThrowableInformation();
                    
                    if (throwableInformation != null && throwableInformation.getThrowable() != null)
                    {
                        builder.append(" @@@@ ").append(throwableInformation.getThrowable());
                    }

                    final String[] throwableStrRep = event.getThrowableStrRep();
                    if (throwableStrRep != null && throwableStrRep.length > 0)
                    {
                        for (String throwableStr : throwableStrRep)
                        {
                            if (!throwableStr.trim().isEmpty())
                            {
                                builder.append(System.lineSeparator());
                                builder.append(throwableStr);
                            }
                        }
                        // builder.append(" ### ").append(Arrays.toString(throwableStrRep));
                        
                    }
                    return builder.toString();
                }
                
            };

        String patternLayout = "%d [%c] [%t] [%p] %m%n";
        final String convertedPatternLayour = LogFilePatternLayoutBuilder.getLogFormatFromPatternLayout(patternLayout);
        System.out.println("convertedPatternLayour " + convertedPatternLayour);
        logFilePatternReceiver.setLogFormat(convertedPatternLayour);
        
        logFilePatternReceiver.setTimestampFormat(timestampFormat);

        File logFile = new File("d:/repositories/svn/polyview/branches/N7_2_2_0_0_1830_patch/PolyView/logs/pvman.log");
        logFilePatternReceiver.setFileURL(logFile.toURI().toASCIIString());

        logFilePatternReceiver.setUseCurrentThread(true);

        logFilePatternReceiver.activateOptions();
    }
    
    public static void main(String[] args) throws Exception
    {
        try
        {
            new LogParserChainsaw().parseLogFile();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

}
