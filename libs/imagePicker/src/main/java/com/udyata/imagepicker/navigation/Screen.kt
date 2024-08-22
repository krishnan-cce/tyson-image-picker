package com.udyata.imagepicker.navigation

sealed class Screen(val route: String) {
    data object GalleryPhotoScreen : Screen("gallery_photo_screen")
    data object RemainingPhotosScreen : Screen("remaining_photos_screen/{groupId}/{groupName}") {
        fun createRoute(groupId: Int,groupName: String) = "remaining_photos_screen/$groupId/$groupName"
    }
}
