package com.example.avatwin.Converter

import android.os.Build
import androidx.annotation.RequiresApi
import com.google.gson.*
import java.lang.reflect.Type
import java.time.LocalDateTime
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

class LocalDateTimeConverter : JsonDeserializer<LocalDateTime>, JsonSerializer<LocalDateTime> {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun deserialize(
            json: JsonElement,
            typeOfT: Type,
            context: JsonDeserializationContext
    ): LocalDateTime {
        return LocalDateTime.parse(json.asString, DateTimeFormatter.ISO_DATE_TIME)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun serialize(
            src: LocalDateTime,
            typeOfSrc: Type,
            context: JsonSerializationContext
    ): JsonElement {
        val dateTimeString = src.format(DateTimeFormatter.ISO_DATE_TIME)
        return JsonPrimitive(dateTimeString)
    }
}