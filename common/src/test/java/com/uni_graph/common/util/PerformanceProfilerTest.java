package com.uni_graph.common.util;

import static org.mockito.Mockito.*;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;

class PerformanceProfilerTest {

  @Test
  void logSummary_ShouldLogPrettyPrint() {
    // Given
    Logger log = mock(Logger.class);
    when(log.isInfoEnabled()).thenReturn(true);
    PerformanceProfiler profiler = new PerformanceProfiler("Test Profiler", log);

    // When
    profiler.start("Task 1");
    try {
      Thread.sleep(50);
    } catch (InterruptedException e) {
    }
    profiler.start("Task 2");
    try {
      Thread.sleep(50);
    } catch (InterruptedException e) {
    }
    profiler.logSummary();

    // Then
    verify(log).info(anyString(), contains("Task 1"));
    verify(log).info(anyString(), contains("Task 2"));
  }
}
