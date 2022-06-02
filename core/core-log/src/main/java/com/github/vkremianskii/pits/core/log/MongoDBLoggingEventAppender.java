package com.github.vkremianskii.pits.core.log;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.IThrowableProxy;
import com.mongodb.BasicDBObject;
import org.bson.Document;

import java.util.Date;

public class MongoDBLoggingEventAppender extends MongoDBAppenderBase<ILoggingEvent> {

    private String application;

    @Override
    protected Document toMongoDocument(ILoggingEvent event) {
        var logEntry = new Document();
        logEntry.append("message", event.getFormattedMessage());
        logEntry.append("logger", event.getLoggerName());
        logEntry.append("thread", event.getThreadName());
        logEntry.append("application", application);
        logEntry.append("timestamp", new Date(event.getTimeStamp()));
        logEntry.append("level", event.getLevel().toString());
        if (event.getMDCPropertyMap() != null && !event.getMDCPropertyMap().isEmpty()) {
            logEntry.append("mdc", event.getMDCPropertyMap());
        }
        appendThrowableIfAvailable(logEntry, event);
        return logEntry;
    }

    private void appendThrowableIfAvailable(Document doc, ILoggingEvent event) {
        if (event.getThrowableProxy() != null) {
            var val = toMongoDocument(event.getThrowableProxy());
            doc.append("throwable", val);
        }
    }

    private BasicDBObject toMongoDocument(IThrowableProxy throwable) {
        var throwableDoc = new BasicDBObject();
        throwableDoc.append("class", throwable.getClassName());
        throwableDoc.append("message", throwable.getMessage());
        throwableDoc.append("stackTrace", toSteArray(throwable));
        if (throwable.getCause() != null) {
            throwableDoc.append("cause", toMongoDocument(throwable.getCause()));
        }
        return throwableDoc;
    }

    private String[] toSteArray(IThrowableProxy throwableProxy) {
        var elementProxies = throwableProxy.getStackTraceElementProxyArray();
        var totalFrames = elementProxies.length - throwableProxy.getCommonFrames();
        var stackTraceElements = new String[totalFrames];
        for (var i = 0; i < totalFrames; ++i) {
            stackTraceElements[i] = elementProxies[i].getStackTraceElement().toString();
        }
        return stackTraceElements;
    }

    public void setApplication(String application) {
        this.application = application;
    }
}
