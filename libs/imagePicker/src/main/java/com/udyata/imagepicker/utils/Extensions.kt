package com.udyata.imagepicker.utils

import android.content.Context
import android.content.Intent
import android.content.pm.ResolveInfo
import android.net.Uri
import android.os.Build
import android.os.Parcelable
import androidx.annotation.RequiresApi
import java.time.LocalDate
import java.time.Month
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.time.temporal.WeekFields
import java.util.Locale


fun Context.getEditImageCapableApps(): List<ResolveInfo> {
    val intent = Intent(Intent.ACTION_EDIT).apply {
        setType("image/*")
    }
    val resolveInfoList = packageManager.queryIntentActivities(intent, 0)
    return resolveInfoList.filterNot { it.activityInfo.packageName == "com.udyata.composecropper" }
}

fun Context.launchEditImageIntent(packageName: String, uri: Uri) {
    val intent = Intent(Intent.ACTION_EDIT).apply {
        addCategory(Intent.CATEGORY_DEFAULT)
        setDataAndType(uri, "image/*")
        putExtra("mimeType", "image/*")
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        setPackage(packageName)
    }
    startActivity(intent)
}

inline fun <reified T : Parcelable> Intent.parcelable(key: String): T? = when {
    Build.VERSION.SDK_INT >= 33 -> getParcelableExtra(key, T::class.java)
    else -> @Suppress("DEPRECATION") getParcelableExtra(key) as? T
}


fun Int.toOrdinal(): String {
    if (this in 11..13) {
        return "${this}th"
    }
    return when (this % 10) {
        1 -> "${this}st"
        2 -> "${this}nd"
        3 -> "${this}rd"
        else -> "${this}th"
    }
}


@RequiresApi(Build.VERSION_CODES.O)
object DateUtils {

    fun String.toCustomDateFormat(): String {
        val localDate = LocalDate.parse(this)
        return localDate.format(DateTimeFormatter.ofPattern("MMMM dd, yyyy"))
    }

    fun String.toCustomWeeklyFormat(): String {
        val parts = this.split("-W")
        val year = parts[0].toInt()
        val weekOfYear = parts[1].toInt()

        val firstDayOfWeek = LocalDate.of(year, 1, 1).with(WeekFields.ISO.weekOfYear(), weekOfYear.toLong()).with(
            WeekFields.ISO.dayOfWeek(), 1)
        val month = firstDayOfWeek.month.getDisplayName(TextStyle.FULL, Locale.getDefault())
        val weekOfMonth = firstDayOfWeek.get(WeekFields.ISO.weekOfMonth()).toOrdinal()

        return "$month $weekOfMonth week, $year"
    }

    fun String.toCustomMonthlyFormat(): String {
        val parts = this.split("-")
        val year = parts[0]
        val month = Month.valueOf(parts[1].uppercase(Locale.getDefault())).getDisplayName(
            TextStyle.FULL, Locale.getDefault())
        return "$month, $year"
    }
}
