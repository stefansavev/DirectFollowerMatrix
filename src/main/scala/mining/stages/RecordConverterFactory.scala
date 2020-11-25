package mining.stages

import java.text.SimpleDateFormat

import mining.ZoneDateTimeUtils
import models.Event
import org.apache.commons.csv.CSVRecord

case class RecordConverterArgs(
    caseIDHeader: String,
    activityHeader: String,
    startHeader: String,
    startDateFormat: String
)

object RecordConverterFactory {

  type RecordConverter = CSVRecord => Event

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
      ZoneDateTimeUtils.stringToZoneDateTime(args.startDateFormat, startTime)
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
