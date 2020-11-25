package mining

import java.text.SimpleDateFormat
import java.time.{LocalDateTime, ZoneId, ZonedDateTime}

import scala.math.Ordering

object ZoneDateTimeOrdering extends Ordering[ZonedDateTime] {
  def compare(a: ZonedDateTime, b: ZonedDateTime): Int = a.compareTo(b)
}

object ZoneDateTimeUtils {
  def stringToZoneDateTime(
      format: SimpleDateFormat,
      timeStr: String
  ): ZonedDateTime = {
    val localStartTime = format.parse(timeStr)
    val zone = ZoneId.systemDefault
    val ldt = LocalDateTime.ofInstant(localStartTime.toInstant, zone)
    ZonedDateTime.of(ldt, zone)
  }
}
