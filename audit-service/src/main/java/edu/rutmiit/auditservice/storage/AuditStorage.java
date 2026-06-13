package edu.rutmiit.auditservice.storage;

import edu.rutmiit.auditservice.model.AuditEntry;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.atomic.AtomicLong;

@Component
public class AuditStorage {

    private final ConcurrentLinkedDeque<AuditEntry> entries = new ConcurrentLinkedDeque<>();
    private final AtomicLong sequence = new AtomicLong(0);

    private final Set<String> processedEventIds = ConcurrentHashMap.newKeySet();

    public boolean isDuplicate(String eventId) {
        return !processedEventIds.add(eventId);
    }

    public AuditEntry save(AuditEntry entry){
        AuditEntry auditEntry = new AuditEntry(
                sequence.incrementAndGet(),
                entry.eventId(),
                entry.eventType(),
                entry.source(),
                entry.eventTimestamp(),
                entry.receivedAt(),
                entry.description()
        );
        entries.addFirst(auditEntry);
        return auditEntry;
    }

    public List<AuditEntry> findLatest(int limit) {
        return entries.stream()
                .limit(limit)
                .toList();
    }

    public int count() {
        return entries.size();
    }
}
