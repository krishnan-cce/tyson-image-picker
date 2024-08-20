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
    object Footer : PhotoSection()
}

@Immutable
data class PhotoGroup(
    val header: PhotoSection.Header,
    val body: List<PhotoSection.Body>,
    val footer: PhotoSection.Footer
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

sealed class PhotoSort(val sortOrder: String, val comparator: Comparator<Photo>) {
    data object DateTakenAsc : PhotoSort("${MediaStore.Images.Media.DATE_TAKEN} ASC", compareBy { it.dateTaken })
    data object DateTakenDesc : PhotoSort("${MediaStore.Images.Media.DATE_TAKEN} DESC", compareByDescending { it.dateTaken })
    data object All : PhotoSort("${MediaStore.Images.Media.DATE_TAKEN} DESC", compareByDescending { it.dateTaken })
    data object Weekly : PhotoSort("${MediaStore.Images.Media.DATE_TAKEN} DESC", compareByDescending { it.dateTaken })
    data object Monthly : PhotoSort("${MediaStore.Images.Media.DATE_TAKEN} DESC", compareByDescending { it.dateTaken })
    data object Yearly : PhotoSort("${MediaStore.Images.Media.DATE_TAKEN} DESC", compareByDescending { it.dateTaken })
}
