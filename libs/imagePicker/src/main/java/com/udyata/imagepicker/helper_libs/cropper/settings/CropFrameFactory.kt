package com.udyata.imagepicker.helper_libs.cropper.settings


import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.graphics.ImageBitmap
import com.udyata.imagepicker.helper_libs.cropper.model.CropFrame
import com.udyata.imagepicker.helper_libs.cropper.model.CropOutline
import com.udyata.imagepicker.helper_libs.cropper.model.CropOutlineContainer
import com.udyata.imagepicker.helper_libs.cropper.model.CustomOutlineContainer
import com.udyata.imagepicker.helper_libs.cropper.model.CustomPathOutline
import com.udyata.imagepicker.helper_libs.cropper.model.CutCornerCropShape
import com.udyata.imagepicker.helper_libs.cropper.model.CutCornerRectOutlineContainer
import com.udyata.imagepicker.helper_libs.cropper.model.ImageMaskOutline
import com.udyata.imagepicker.helper_libs.cropper.model.ImageMaskOutlineContainer
import com.udyata.imagepicker.helper_libs.cropper.model.OutlineType
import com.udyata.imagepicker.helper_libs.cropper.model.OvalCropShape
import com.udyata.imagepicker.helper_libs.cropper.model.OvalOutlineContainer
import com.udyata.imagepicker.helper_libs.cropper.model.PolygonCropShape
import com.udyata.imagepicker.helper_libs.cropper.model.PolygonOutlineContainer
import com.udyata.imagepicker.helper_libs.cropper.model.PolygonProperties
import com.udyata.imagepicker.helper_libs.cropper.model.RectCropShape
import com.udyata.imagepicker.helper_libs.cropper.model.RectOutlineContainer
import com.udyata.imagepicker.helper_libs.cropper.model.RoundedCornerCropShape
import com.udyata.imagepicker.helper_libs.cropper.model.RoundedRectOutlineContainer
import com.udyata.imagepicker.helper_libs.cropper.util.createPolygonShape

class CropFrameFactory(private val defaultImages: List<ImageBitmap>) {

    private val cropFrames = mutableStateListOf<CropFrame>()

    fun getCropFrames(): List<CropFrame> {
        if (cropFrames.isEmpty()) {
            val temp = mutableListOf<CropFrame>()
            OutlineType.values().forEach {
                temp.add(getCropFrame(it))
            }
            cropFrames.addAll(temp)
        }
        return cropFrames
    }

    fun getCropFrame(outlineType: OutlineType): CropFrame {
        return cropFrames
            .firstOrNull { it.outlineType == outlineType } ?: createDefaultFrame(outlineType)
    }

    private fun createDefaultFrame(outlineType: OutlineType): CropFrame {
        return when (outlineType) {
            OutlineType.Rect -> {
                CropFrame(
                    outlineType = outlineType,
                    editable = false,
                    cropOutlineContainer = createCropOutlineContainer(outlineType)
                )
            }

            OutlineType.RoundedRect -> {
                CropFrame(
                    outlineType = outlineType,
                    editable = true,
                    cropOutlineContainer = createCropOutlineContainer(outlineType)
                )
            }

            OutlineType.CutCorner -> {
                CropFrame(
                    outlineType = outlineType,
                    editable = true,
                    cropOutlineContainer = createCropOutlineContainer(outlineType)
                )
            }

            OutlineType.Oval -> {
                CropFrame(
                    outlineType = outlineType,
                    editable = true,
                    cropOutlineContainer = createCropOutlineContainer(outlineType)
                )
            }

            OutlineType.Polygon -> {
                CropFrame(
                    outlineType = outlineType,
                    editable = true,
                    cropOutlineContainer = createCropOutlineContainer(outlineType)
                )
            }


            OutlineType.Custom -> {
                CropFrame(
                    outlineType = outlineType,
                    editable = true,
                    cropOutlineContainer = createCropOutlineContainer(outlineType)
                )
            }

            OutlineType.ImageMask -> {
                CropFrame(
                    outlineType = outlineType,
                    editable = true,
                    cropOutlineContainer = createCropOutlineContainer(outlineType)
                )
            }
        }
    }

    private fun createCropOutlineContainer(
        outlineType: OutlineType
    ): CropOutlineContainer<out CropOutline> {
        return when (outlineType) {
            OutlineType.Rect -> {
                RectOutlineContainer(
                    outlines = listOf(RectCropShape(id = 0, title = "Rect"))
                )
            }

            OutlineType.RoundedRect -> {
                RoundedRectOutlineContainer(
                    outlines = listOf(RoundedCornerCropShape(id = 0, title = "Rounded"))
                )
            }

            OutlineType.CutCorner -> {
                CutCornerRectOutlineContainer(
                    outlines = listOf(CutCornerCropShape(id = 0, title = "CutCorner"))
                )
            }

            OutlineType.Oval -> {
                OvalOutlineContainer(
                    outlines = listOf(OvalCropShape(id = 0, title = "Oval"))
                )
            }

            OutlineType.Polygon -> {
                PolygonOutlineContainer(
                    outlines = listOf(
                        PolygonCropShape(
                            id = 0,
                            title = "Polygon"
                        ),
                        PolygonCropShape(
                            id = 1,
                            title = "Pentagon",
                            polygonProperties = PolygonProperties(sides = 5, 0f),
                            shape = createPolygonShape(5, 0f)
                        ),
                        PolygonCropShape(
                            id = 2,
                            title = "Heptagon",
                            polygonProperties = PolygonProperties(sides = 7, 0f),
                            shape = createPolygonShape(7, 0f)
                        ),
                        PolygonCropShape(
                            id = 3,
                            title = "Octagon",
                            polygonProperties = PolygonProperties(sides = 8, 0f),
                            shape = createPolygonShape(8, 0f)
                        )
                    )
                )
            }

            OutlineType.Custom -> {
                CustomOutlineContainer(
                    outlines = listOf(
                        CustomPathOutline(id = 0, title = "Custom", path = Paths.Favorite),
                        CustomPathOutline(id = 1, title = "Star", path = Paths.Star)
                    )
                )
            }

            OutlineType.ImageMask -> {

                val outlines = defaultImages.mapIndexed { index, image ->
                    ImageMaskOutline(id = index, title = "ImageMask", image = image)

                }
                ImageMaskOutlineContainer(
                    outlines = outlines
                )
            }
        }
    }

    fun editCropFrame(cropFrame: CropFrame) {
        val indexOf = cropFrames.indexOfFirst { it.outlineType == cropFrame.outlineType }
        cropFrames[indexOf] = cropFrame
    }
}