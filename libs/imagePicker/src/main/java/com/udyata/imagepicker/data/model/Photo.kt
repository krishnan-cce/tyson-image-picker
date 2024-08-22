@file:Suppress("EXTENSION_SHADOWED_BY_MEMBER")

package com.udyata.imagepicker.data.model

import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Immutable
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Date

sealed class PhotoSection {
    data class Header(val date: String) : PhotoSection()
    data class Body(val photo: Photo) : PhotoSection()
    data class Footer(val remainingCount: Int) : PhotoSection()
}

data class PhotoGroup(
    val groupId: Int,
    val header: PhotoSection.Header,
    val body: List<PhotoSection.Body>,
    val footer: PhotoSection.Footer? = null,
    val allPhotos: List<Photo>
)


@Immutable
data class Photo(
    val id: Long,
    val uri: Uri,
    val thumbnailUri: Uri?,
    val name: String,
    val dateTaken: Date,
    val size: Long,
    val width: Int,
    val height: Int,
    val mimeType: String
) {
    val dateOnly: String
        @RequiresApi(Build.VERSION_CODES.O)
        get() {
            val localDate = dateTaken.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
            val formattedDate = localDate.format(DateTimeFormatter.ISO_LOCAL_DATE)
            return formattedDate
        }
}


sealed class PhotoFilter {
    abstract fun apply(photo: Photo): Boolean

    data class NameFilter(val name: String) : PhotoFilter() {
        override fun apply(photo: Photo): Boolean {
            return photo.name.contains(name, ignoreCase = true)
        }
    }

    data object NoFilter : PhotoFilter() {
        override fun apply(photo: Photo): Boolean = true
    }

    fun getSelection(): String? {
        return when (this) {
            is NameFilter -> "${MediaStore.Images.Media.DISPLAY_NAME} LIKE ?"
            NoFilter -> null
        }
    }

    fun getSelectionArgs(): Array<String>? {
        return when (this) {
            is NameFilter -> arrayOf("%${(this as NameFilter).name}%")
            NoFilter -> null
        }
    }
}

sealed class PhotoSort(val sortOrder: String, val comparator: Comparator<Photo>, val name: String) {
    data object DateTakenAsc : PhotoSort("${MediaStore.Images.Media.DATE_TAKEN} ASC", compareBy { it.dateTaken }, "Date Taken Asc")
    data object DateTakenDesc : PhotoSort("${MediaStore.Images.Media.DATE_TAKEN} DESC", compareByDescending { it.dateTaken }, "Date Taken Desc")
    data object All : PhotoSort("${MediaStore.Images.Media.DATE_TAKEN} DESC", compareByDescending { it.dateTaken }, "All")
    data object Weekly : PhotoSort("${MediaStore.Images.Media.DATE_TAKEN} DESC", compareByDescending { it.dateTaken }, "Weekly")
    data object Monthly : PhotoSort("${MediaStore.Images.Media.DATE_TAKEN} DESC", compareByDescending { it.dateTaken }, "Monthly")
    data object Yearly : PhotoSort("${MediaStore.Images.Media.DATE_TAKEN} DESC", compareByDescending { it.dateTaken }, "Yearly")
}
val PhotoSort.name: String
    get() = when (this) {
        is PhotoSort.DateTakenAsc -> "Date Taken Asc"
        is PhotoSort.DateTakenDesc -> "Date Taken Desc"
        is PhotoSort.All -> "All"
        is PhotoSort.Weekly -> "Weekly"
        is PhotoSort.Monthly -> "Monthly"
        is PhotoSort.Yearly -> "Yearly"
    }
