package mining.stages

import java.time.{LocalDateTime, ZoneId, ZonedDateTime}

import mining.Header
import models.Event
import org.apache.commons.csv.CSVRecord

object CSVToRecordConverter {
  val format = new java.text.SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS")

  def stringToZoneDateTime(time: String): ZonedDateTime = {
    val localStartTime = format.parse(time)
    val zone = ZoneId.systemDefault
    val ldt = LocalDateTime.ofInstant(localStartTime.toInstant, zone)
    ZonedDateTime.of(ldt, zone)
  }

  def csvRecordToEvent(csvRecord: CSVRecord): Event = {
    val traceId = csvRecord.get(Header.CaseID)
    val activity = csvRecord.get(Header.Activity)
    val startTime = csvRecord.get(Header.Start)
    Event(traceId, activity, stringToZoneDateTime(startTime))
  }
}

