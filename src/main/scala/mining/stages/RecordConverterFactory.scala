package mining.stages

import java.text.SimpleDateFormat
import java.time.{LocalDateTime, ZoneId, ZonedDateTime}

import models.Event
import org.apache.commons.csv.CSVRecord

object RecordConverterArgsDefaults {
  val defaultStartDateFormat = "yyyy/MM/dd HH:mm:ss.SSS"
}

case class RecordConverterArgs(
    caseIDHeader: String,
    activityHeader: String,
    startHeader: String,
    startDateFormat: String = RecordConverterArgsDefaults.defaultStartDateFormat
)

object RecordConverterFactory {

  type RecordConverter = CSVRecord => Event

  def stringToZoneDateTime(
      format: SimpleDateFormat,
      timeStr: String
  ): ZonedDateTime = {
    val localStartTime = format.parse(timeStr)
    val zone = ZoneId.systemDefault
    val ldt = LocalDateTime.ofInstant(localStartTime.toInstant, zone)
    ZonedDateTime.of(ldt, zone)
  }

  case class PreparedArgs(
      caseIDHeader: String,
      activityHeader: String,
      startHeader: String,
      startDateFormat: SimpleDateFormat
  )

  def csvRecordToEvent(args: PreparedArgs)(csvRecord: CSVRecord): Event = {
    val traceId = csvRecord.get(args.caseIDHeader)
    val activity = csvRecord.get(args.activityHeader)
    val startTime = csvRecord.get(args.startHeader)
    Event(
      traceId,
      activity,
      stringToZoneDateTime(args.startDateFormat, startTime)
    )
  }

  def fromArgs(args: RecordConverterArgs): RecordConverter = {
    val dateFormat = new java.text.SimpleDateFormat(args.startDateFormat)
    val format = PreparedArgs(
      args.caseIDHeader,
      args.activityHeader,
      args.startHeader,
      dateFormat
    )
    csvRecordToEvent(format)
  }
}
