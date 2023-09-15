/**
 * Copyright 2023 bps-e.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.bps_e.db

import androidx.room.TypeConverter
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.time.LocalDateTime

/**
 * ```
 * @TypeConverters(ListTypeConverter::class)
 * RoomDatabase()
 * ```
 */
class ListTypeConverter {
    @TypeConverter
    fun fromList(list: List<String>): String {
        return Json.encodeToString(list)
    }
    @TypeConverter
    fun toList(str: String): List<String> {
        return Json.decodeFromString<List<String>>(str)
    }
}

/**
 * ```
 * @TypeConverters(MapTypeConverter::class)
 * RoomDatabase()
 * ```
 */
class MapTypeConverter {
    @TypeConverter
    fun fromMap(map: Map<String, String>): String {
        return Json.encodeToString(map)
    }
    @TypeConverter
    fun toMap(str: String): Map<String, String> {
        return Json.decodeFromString<Map<String, String>>(str)
    }
}

/**
 * ```
 * @TypeConverters(DateTimeTypeConverter::class)
 * RoomDatabase()
 * ```
 */
class DateTimeTypeConverter {
    @TypeConverter
    fun fromDate(dateTime: LocalDateTime): String {
        return dateTime.toString()
    }
    @TypeConverter
    fun toDate(str: String): LocalDateTime {
        return LocalDateTime.parse(str)
    }
}