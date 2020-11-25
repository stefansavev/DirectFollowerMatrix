package mining

import java.time.ZonedDateTime

import scala.math.Ordering

object ZoneDateTimeOrdering extends Ordering[ZonedDateTime] {
  def compare(a: ZonedDateTime, b: ZonedDateTime): Int = a.compareTo(b)
}
