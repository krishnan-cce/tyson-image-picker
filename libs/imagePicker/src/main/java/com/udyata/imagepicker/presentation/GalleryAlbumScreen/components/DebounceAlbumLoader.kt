package com.udyata.imagepicker.presentation.GalleryAlbumScreen.components

import android.util.Log
import com.udyata.imagepicker.data.model.Album
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil3.compose.EqualityDelegate
import coil3.compose.LocalPlatformContext
import coil3.compose.rememberAsyncImagePainter
import coil3.request.CachePolicy
import coil3.request.ImageRequest
import coil3.size.Scale
import com.udyata.imagepicker.data.model.Photo
import com.udyata.imagepicker.navigation.Screen
import com.udyata.imagepicker.presentation.GalleryPicker.components.CheckBox

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DebounceAlbumLoader(
    modifier: Modifier = Modifier,
    album: Album,
    navController: NavHostController,
    groupName: String
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = modifier
                .aspectRatio(1f)
                .padding(2.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.surfaceColorAtElevation(2.dp))
                .clickable {
                    Log.d("DebounceAlbumLoader", "DebounceAlbumLoader: $album")
                    navController.navigate(
                        Screen.RemainingAlbumsScreen.createRoute(
                            album.id.toString(),
                            groupName
                        )
                    )
                },
            contentAlignment = Alignment.Center
        ) {

            Image(
                painter = rememberAsyncImagePainter(album.coverUri),
                contentDescription = album.name,
                modifier = Modifier.aspectRatio(1f),
                contentScale = ContentScale.Crop,
            )

        }

        Text(
            text = album.name,
            modifier = Modifier,
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
            overflow = TextOverflow.Ellipsis,
            maxLines = 1
        )
    }
}
