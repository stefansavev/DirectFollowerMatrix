package mining.stages

import java.time.ZonedDateTime

import mining.{ZoneDateTimeOrdering, ZoneDateTimeUtils}
import models.Trace

trait TraceFilterArgs {
  def apply(events: Iterator[Trace]): Iterator[Trace]
}

object NoTraceFilter extends TraceFilterArgs {
  def apply(events: Iterator[Trace]): Iterator[Trace] = {
    events
  }
}

class InBetweenDatesTraceFilter(
    val startedAtOrAfter: ZonedDateTime,
    val endedAtOrAfter: ZonedDateTime
) extends TraceFilterArgs {
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

object InBetweenDatesTraceFilter {
  def fromStrings(
      format: String,
      startedAtOrAfter: String,
      endedAtOrAfter: String
  ): TraceFilterArgs = {
    val dateFormat = new java.text.SimpleDateFormat(format)
    new InBetweenDatesTraceFilter(
      ZoneDateTimeUtils.stringToZoneDateTime(dateFormat, startedAtOrAfter),
      ZoneDateTimeUtils.stringToZoneDateTime(dateFormat, endedAtOrAfter)
    )
  }
}

object TraceFilterFactory {
  type TraceFilterProcessor = (Iterator[Trace]) => Iterator[Trace]

  def fromArgs(args: TraceFilterArgs): TraceFilterProcessor = {
    args.apply
  }
}
