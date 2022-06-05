package ru.leadpogrommer.vk22.d40

import io.ktor.util.*
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVParser
import java.io.File
import java.io.FileReader
import java.time.DayOfWeek
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeFormatterBuilder
import java.util.*
import kotlin.collections.List


private val formatter = DateTimeFormatter.ofPattern("EEEE", Locale.forLanguageTag("ru"))


// time in seconds from day start
data class Lesson(val Channel: String, val days: List<DayOfWeek>, val start: Int, val end: Int, val users: List<String>)

fun loadCsv(file: File): List<Lesson>{
    val parser = CSVParser(FileReader(file), CSVFormat.DEFAULT)
    val (channels, days, hours, users) = parser.records

    return (0 until channels.size()).map { i ->
        val (start, end) = days[i].split('-').map { it.trim().toLowerCasePreservingASCIIRules() }.map { DayOfWeek.from(formatter.parse(it)) }
        val realDays = (start.value .. end.value).map {
            DayOfWeek.of(it)
        }

        // time from start of the dat in minutes
        val times = hours[i].split('-').map {
            val (h, m) = it.trim().split(':').map { s->s.toInt() }
            h*60+m
        }
        Lesson(channels[i], realDays, times[0], times[1], users[i].split(','))

    }
}