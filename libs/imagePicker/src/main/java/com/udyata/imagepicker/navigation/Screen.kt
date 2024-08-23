package com.udyata.imagepicker.navigation

sealed class Screen(val route: String) {
    data object GalleryPhotoScreen : Screen("gallery_photo_screen")
    data object GalleryAlbumScreen : Screen("GalleryAlbumScreen")

    data object RemainingPhotosScreen : Screen("remaining_photos_screen/{groupId}/{groupName}") {
        fun createRoute(groupId: Int,groupName: String) = "remaining_photos_screen/$groupId/$groupName"
    }
    data object RemainingAlbumsScreen : Screen("remainingAlbumsScreen/{albumId}/{albumName}") {
        fun createRoute(albumId: String, albumName: String): String {
            return "remainingAlbumsScreen/$albumId/$albumName"
        }
    }
}