package com.allan.studyaid.store;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * In-memory only, as agreed for this stage of the project — swap for a real store
 * once performance history needs to survive a restart or scale beyond one instance.
 * Caps history per student to keep memory bounded.
 */
@Component
public class PerformanceStore {

    private static final int MAX_RECORDS_PER_STUDENT = 100;

    private final Map<String, List<PerformanceRecord>> recordsByStudent = new ConcurrentHashMap<>();

    public void addRecord(String studentId, PerformanceRecord record) {
        List<PerformanceRecord> records = recordsByStudent.computeIfAbsent(studentId, k -> new CopyOnWriteArrayList<>());
        records.add(record);
        while (records.size() > MAX_RECORDS_PER_STUDENT) {
            records.remove(0);
        }
    }

    public List<PerformanceRecord> getRecords(String studentId) {
        return recordsByStudent.getOrDefault(studentId, List.of());
    }
}
