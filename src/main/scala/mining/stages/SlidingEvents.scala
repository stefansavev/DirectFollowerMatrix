package mining.stages

import models.Event

case class SlidingEventsArgs(useStartEnd: Boolean)

object SlidingEvents {
  type SlidingProcessor = Seq[Event] => Iterator[Seq[Event]]

  def fromArgs(args: SlidingEventsArgs): SlidingProcessor = {
    def sliding(inp: Seq[Event]): Iterator[Seq[Event]] = {
      inp.sliding(2)
    }
    def slidingWithStartEnd(inp: Seq[Event]): Iterator[Seq[Event]] = {
      if (inp.length == 0){
        Iterator.empty
      }
      else {
        val first = inp(0).copy(activity = "[Start]")
        val last = inp.last.copy(activity = "[End]")
        val extended = Seq.concat(Seq(first), inp, Seq(last))
        extended.sliding(2)
      }
    }
    if (args.useStartEnd)
      slidingWithStartEnd
    else{
      sliding
    }
  }
}