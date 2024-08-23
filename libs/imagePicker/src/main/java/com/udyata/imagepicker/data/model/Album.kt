package com.udyata.imagepicker.data.model

import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Immutable
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Date

sealed class AlbumSection {
    data class Header(val title: String) : AlbumSection()
    data class Body(val album: Album) : AlbumSection()
    data class Footer(val remainingCount: Int) : AlbumSection()
}

data class AlbumGroup(
    val groupId: Int,
    val header: AlbumSection.Header,
    val body: List<AlbumSection.Body>,
    val footer: AlbumSection.Footer? = null,
    val allAlbums: List<Album>
)

@Immutable
data class Album(
    val id: Long,
    val name: String,
    val coverUri: Uri?,
    val dateCreated: Date,
    val photoCount: Int
) {
    val dateOnly: String
        @RequiresApi(Build.VERSION_CODES.O)
        get() {
            val localDate = dateCreated.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
            return localDate.format(DateTimeFormatter.ISO_LOCAL_DATE)
        }
}

sealed class AlbumSort(val sortOrder: String, val comparator: Comparator<Album>, val name: String) {
    data object DateCreatedAsc : AlbumSort("date_created ASC", compareBy { it.dateCreated }, "Date Created Asc")
    data object DateCreatedDesc : AlbumSort("date_created DESC", compareByDescending { it.dateCreated }, "Date Created Desc")
    data object All : AlbumSort("date_created DESC", compareByDescending { it.dateCreated }, "All")
    data object Weekly : AlbumSort("date_created DESC", compareByDescending { it.dateCreated }, "Weekly")
    data object Monthly : AlbumSort("date_created DESC", compareByDescending { it.dateCreated }, "Monthly")
    data object Yearly : AlbumSort("date_created DESC", compareByDescending { it.dateCreated }, "Yearly")
}
