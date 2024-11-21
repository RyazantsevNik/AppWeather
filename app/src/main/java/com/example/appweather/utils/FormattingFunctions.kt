package com.example.appweather.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import java.text.SimpleDateFormat
import java.util.Locale

fun formatDate(dateString: String): String {
    return try {

        val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val outputFormat = SimpleDateFormat("dd MMMM", Locale("ru", "RU"))
        val date = inputFormat.parse(dateString)


        outputFormat.format(date!!)
    } catch (e: Exception) {
        dateString
    }
}

fun formatHour(time: String): String {
    try {
        // Формат времени без миллисекунд
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())

        // Преобразуем строку в объект Date
        val date = dateFormat.parse(time)

        // Если парсинг успешен, форматируем время
        return if (date != null) {
            val hourFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
            hourFormat.format(date)
        } else {
            "Invalid time"
        }
    } catch (e: Exception) {
        // Логирование ошибки и возвращение сообщения о некорректном времени
        e.printStackTrace()
        return "Invalid time"
    }
}
