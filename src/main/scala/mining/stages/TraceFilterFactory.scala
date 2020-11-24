package mining.stages

import java.time.ZonedDateTime

import mining.ZoneDateTimeOrdering
import models.Trace

trait TraceFilterArgs {
  def apply(events: Iterator[Trace]): Iterator[Trace]
}

object NoTraceFilter extends TraceFilterArgs {
  def apply(events: Iterator[Trace]): Iterator[Trace] = {
    events
  }
}

case class TraceFilter(
    startedAtOrAfter: ZonedDateTime,
    endedAtOrAfter: ZonedDateTime
) {
  def apply(events: Iterator[Trace]): Iterator[Trace] = {
    def traceFilter(trace: Trace): Boolean = {
      if (trace.events.length == 0) {
        false
      } else {
        val first = trace.events(0)
        val last = trace.events.last
        ZoneDateTimeOrdering.compare(first.start, startedAtOrAfter) >= 0 &&
        ZoneDateTimeOrdering.compare(last.start, endedAtOrAfter) <= 0
      }
    }
    events.filter(traceFilter)
  }
}

object TraceFilterFactory {
  type TraceFilterProcessor = (Iterator[Trace]) => Iterator[Trace]

  def fromArgs(args: TraceFilterArgs): TraceFilterProcessor = {
    args.apply
  }
}
