package com.udyata.imagepicker.presentation.GalleryPicker

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.core.view.WindowCompat
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import coil3.annotation.ExperimentalCoilApi
import coil3.compose.setSingletonImageLoaderFactory
import com.udyata.composegalley.presentation.GalleryViewModel
import com.udyata.imagepicker.di.AppModule
import com.udyata.imagepicker.navigation.Screen
import com.udyata.imagepicker.presentation.EditActivity.EditActivity
import com.udyata.imagepicker.presentation.RemainingPhotosScreen.RemainingPhotosScreen
import com.udyata.imagepicker.theme.ComposeCropperTheme
import com.udyata.imagepicker.utils.newImageLoader


@RequiresApi(Build.VERSION_CODES.Q)
class GalleryPickerActivity : ComponentActivity() {

    private var isMultiSelection = false

    @OptIn(ExperimentalCoilApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getIntentData()
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            enableEdgeToEdge()
            setSingletonImageLoaderFactory(::newImageLoader)
            ComposeCropperTheme(darkTheme = false) {
                val context = LocalContext.current
                val navController = rememberNavController()

                val editActivityLauncher =
                    rememberLauncherForActivityResult(contract = ActivityResultContracts.StartActivityForResult()) { result ->
                        if (result.resultCode == AppCompatActivity.RESULT_OK) {
                            val item = result.data?.getStringExtra("edited_image_uri")
                            item?.let { uri ->
                                sendResult(uri)
                                val intent = Intent()
                                intent.putExtra("edited_image_uri", uri)
                                setResult(RESULT_OK, intent)
                                finish()

                            }
                        }
                    }

                val viewModel = remember {
                    val contentResolver = AppModule.provideContentResolver(context)
                    val photoRepository = AppModule.providePhotoRepository(contentResolver)
                    val getPhotosUseCase = AppModule.provideGetPhotosUseCase(photoRepository)
                    GalleryViewModel(getPhotosUseCase)
                }

                // UI content
                NavHost(
                    navController = navController,
                    startDestination = Screen.GalleryPhotoScreen.route
                ) {
                    composable(Screen.GalleryPhotoScreen.route) {
                        GalleryPhotoScreen(
                            viewModel = viewModel,
                            onSelectImage = { uri ->
                                val intent =
                                    Intent(context, EditActivity::class.java).apply {
                                        putExtra("imageUri", uri.toString())
                                    }
                                editActivityLauncher.launch(intent)
                            },
                            isMultiSelection = isMultiSelection,
                            navController = navController
                        )
                    }

                    composable(
                        route = Screen.RemainingPhotosScreen.route,
                        arguments = listOf(navArgument("groupId") { type = NavType.IntType })
                    ) { backStackEntry ->
                        val groupName = backStackEntry.arguments?.getString("groupName") ?: return@composable
                        val groupId = backStackEntry.arguments?.getInt("groupId") ?: return@composable
                        RemainingPhotosScreen(
                            groupId = groupId,
                            groupName = groupName,
                            viewModel = viewModel,
                            onSelectImage = { uri ->
                                val intent =
                                    Intent(context, EditActivity::class.java).apply {
                                        putExtra("imageUri", uri.toString())
                                    }
                                editActivityLauncher.launch(intent)
                            },
                            navController = navController
                        )
                    }
                }
//                GalleryPhotoScreen(
//                    viewModel = viewModel,
//                    onSelectImage = { uri ->
//                        val intent =
//                            Intent(context, EditActivity::class.java).apply {
//                                putExtra("imageUri", uri.toString())
//                            }
//                        editActivityLauncher.launch(intent)
//                    },
//                    isMultiSelection=isMultiSelection
//                )
            }
        }
    }

    private fun sendResult(uri: String) {

        val intent = Intent()
        intent.putExtra("edited_image_uri", uri)
        setResult(RESULT_OK, intent)
        finish()
    }

    private fun getIntentData() {
        isMultiSelection = intent.getBooleanExtra("isMultiSelection",false)
    }

}

