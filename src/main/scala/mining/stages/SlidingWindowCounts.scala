package mining.stages

import io.IOKeyedCounter
import models.Trace

object SlidingWindowCounter {
  def count(
      slidingProcessor: WindowedEventsFactory.WindowedEventsProcessor,
      filteredEvents: Iterator[Trace]
  ): Seq[((String, String), Int)] = {
    val counter = new IOKeyedCounter[(String, String), Int](0, _ + _)
    filteredEvents.foreach(trace => {
      slidingProcessor(trace.events).foreach { conseqEvents =>
        {
          val first = conseqEvents(0).activity
          val second = conseqEvents(1).activity
          counter.add((first, second), 1)
        }
      }
    })
    counter.getResults
  }
}
