package com.allan.studyaid.service;

import com.allan.studyaid.store.PerformanceRecord;

import java.util.List;

final class PerformanceSummarizer {

    private PerformanceSummarizer() {}

    static String summarize(List<PerformanceRecord> records) {
        StringBuilder sb = new StringBuilder();
        for (PerformanceRecord r : records) {
            sb.append("- [").append(r.submittedAt()).append("] ")
              .append(r.subject()).append(": ")
              .append(r.score()).append("/").append(r.total())
              .append(" correct. Weak topics: ").append(String.join(", ", r.weakTopics()))
              .append(". Strong topics: ").append(String.join(", ", r.strongTopics()))
              .append("\n");
        }
        return sb.toString();
    }
}
