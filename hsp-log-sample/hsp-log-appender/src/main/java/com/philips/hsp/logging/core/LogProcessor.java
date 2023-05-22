package com.philips.hsp.logging.core;

import com.philips.hsp.logging.core.model.Entry;
import com.philips.hsp.logging.core.model.LogData;
import com.philips.hsp.logging.core.model.LogEntry;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.core.LogEvent;
import org.joda.time.DateTime;
import org.joda.time.format.ISODateTimeFormat;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;
import java.util.*;
import java.util.concurrent.*;

@Log4j2
public class LogProcessor {

    LogSender sender;
    private final ExecutorService executorService = Executors.newFixedThreadPool(10);
    private final BlockingQueue<Entry> queue = new LinkedBlockingQueue<>();
    private QueueProcessor processor;
    private volatile boolean isStarted = false;

    @Inject
    public LogProcessor(LogSender sender) {
        this.sender = sender;
        isStarted = true;
        processor = new QueueProcessor();
        processor.start();
    }

    @PostConstruct
    public void init() {
        isStarted = true;
        processor = new QueueProcessor();
        processor.start();
    }

    @PreDestroy
    public void destroy() {
        executorService.shutdown();
        isStarted = false;
        processor.interrupt();

    }

    public void process(LogEvent event, final byte[] bytes) {

        try {
            queue.put(convertEventToEntry(event, bytes));
        } catch (InterruptedException e) {
            log.warn("Log processor interrupted: {}", e.getMessage());
            Thread.currentThread().interrupt();
        }
    }

    private Entry convertEventToEntry(LogEvent event, final byte[] bytes) {
        LogEntry logEntry = new LogEntry();
        logEntry.setCategory(sender.getCredentials().getCategory());
        logEntry.setSeverity(event.getLevel().name());
        logEntry.setApplicationName(sender.getCredentials().getApplicationName());
        logEntry.setApplicationInstance(
                sender.getCredentials().getApplicationInstance() == null ? "1" : sender.getCredentials().getApplicationInstance());
        logEntry.setServiceName(sender.getCredentials().getServiceName());
        logEntry.setComponent(event.getLoggerName());
        logEntry.setServerName(sender.getCredentials().getServerName());
        logEntry.setApplicationVersion(sender.getCredentials().getApplicationVersion());
        logEntry.setEventId(
                event.getContextData().containsKey("eventId") ? event.getContextData().getValue("eventId") : UUID.randomUUID().toString());
        logEntry.setId(UUID.randomUUID().toString());
        List<String> idKeys = List.of("id", "transactionId");
        List<String> idValues = idKeys.stream()
                .map(event.getContextData().toMap()::get)
                .filter(Objects::nonNull).toList();
        logEntry.setTransactionId((idValues.size() > 0) ? idValues.get(0) : UUID.randomUUID().toString());
        logEntry.setLogTime(ISODateTimeFormat.dateTime().print(DateTime.now()));
        List<String> userKeys = List.of("user", "subject", "originatingUser");
        List<String> userValues = userKeys.stream()
                .map(event.getContextData().toMap()::get)
                .filter(Objects::nonNull).toList();
        logEntry.setOriginatingUser((userValues.size() > 0) ? userValues.get(0) : "na");
        logEntry.setLogData(new LogData(Base64.getEncoder().encodeToString(bytes)));
        Map<String, String> contexts = event.getContextData().toMap();
        logEntry.setCustom(contexts);
        return new Entry(logEntry);
    }

    private class QueueProcessor extends Thread {

        @Override
        public void run() {

            try {
                while (isStarted) {
                    if (!queue.isEmpty()) {
                        List<Entry> events = new ArrayList<>();
                        int numMsg = queue.drainTo(events, 25);
                        if (numMsg > 0) {
                            LogRunner worker = new LogRunner(sender, events);
                            executorService.submit(worker);
                        }
                    } else {
                        TimeUnit.SECONDS.sleep(5);
                    }
                }
            } catch (InterruptedException ex) {
                log.warn("Log processing queue thread interrupted: {}", ex.getMessage());
                Thread.currentThread().interrupt();
            }
        }
    }
}
