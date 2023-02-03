package com.philips.hsp.logging.core;

import com.philips.hsp.logging.core.model.Entry;

import java.util.List;

public class LogRunner implements Runnable {
    private final LogSender sender;
    private final List<Entry> events;

    public LogRunner(LogSender sender, List<Entry> events) {
        this.sender = sender;
        this.events = events;
    }

    @Override
    public void run() {
        sender.send(events);
    }
}
