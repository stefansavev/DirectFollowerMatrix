package mining.stages

import models.Event

case class SlidingEventsArgs(useStartEnd: Boolean)

object WindowedEventsFactory {
  type WindowedEventsProcessor = Seq[Event] => Iterator[Seq[Event]]

  def fromArgs(args: SlidingEventsArgs): WindowedEventsProcessor = {
    // Please note the discrepancy in terminology between Scala and mine
    // I use windowed, like Spark and SQL, while Scala uses sliding
    def windowed(inp: Seq[Event]): Iterator[Seq[Event]] = {
      inp.sliding(2)
    }
    def windowedWithStartEnd(inp: Seq[Event]): Iterator[Seq[Event]] = {
      if (inp.length == 0) {
        Iterator.empty
      } else {
        val first = inp(0).copy(activity = "[Start]")
        val last = inp.last.copy(activity = "[End]")
        val extended = Seq.concat(Seq(first), inp, Seq(last))
        extended.sliding(2)
      }
    }
    if (args.useStartEnd) {
      windowedWithStartEnd
    } else {
      windowed
    }
  }
}
