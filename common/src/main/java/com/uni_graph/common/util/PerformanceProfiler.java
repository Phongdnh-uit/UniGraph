package com.uni_graph.common.util;

import org.slf4j.Logger;
import org.springframework.util.StopWatch;

/**
 * Utility for measuring and logging performance of various tasks. Wraps Spring's StopWatch to
 * provide a clean API and consistent logging.
 */
public class PerformanceProfiler {
  private final StopWatch stopWatch;
  private final Logger log;

  public PerformanceProfiler(String id, Logger log) {
    this.stopWatch = new StopWatch(id);
    this.log = log;
  }

  public void start(String taskName) {
    if (stopWatch.isRunning()) {
      stopWatch.stop();
    }
    stopWatch.start(taskName);
  }

  public void stop() {
    if (stopWatch.isRunning()) {
      stopWatch.stop();
    }
  }

  public void logSummary() {
    stop();
    if (log.isInfoEnabled()) {
      log.info("\n{}", stopWatch.prettyPrint());
    }
  }
}
