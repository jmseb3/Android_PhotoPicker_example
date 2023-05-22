package com.wonddak.photopicker

import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import coil.ImageLoader
import coil.compose.rememberAsyncImagePainter
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import coil.decode.VideoFrameDecoder
import coil.request.ImageRequest
import coil.request.videoFrameMillis
import coil.size.Dimension
import coil.size.Size

@Composable
fun MultipleMediaPicker() {
    var selectedUriList: List<Uri> by remember {
        mutableStateOf(emptyList())
    }
    val pickMultipleMedia =
        rememberLauncherForActivityResult(ActivityResultContracts.PickMultipleVisualMedia(5)) { uris ->
            if (uris.isNotEmpty()) {
                uris.forEach {uri ->
                    Log.d("JWH", "Selected URI: $uri")
                }
                selectedUriList = uris
            } else {
                Log.d("JWH", "No media selected")
            }
        }
    Column() {

        Button(
            onClick = {
                pickMultipleMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageAndVideo))
            }
        ) {
            Text(text = "Image And Video")
        }
        LazyColumn() {
            items(selectedUriList) {uri ->
                val context = LocalContext.current
                val imageLoader = ImageLoader.Builder(context)
                    .components {
                        add(VideoFrameDecoder.Factory())
                        if (Build.VERSION.SDK_INT >= 28) {
                            add(ImageDecoderDecoder.Factory())
                        } else {
                            add(GifDecoder.Factory())
                        }
                    }
                    .build()
                val painter = rememberAsyncImagePainter(
                    model = ImageRequest.Builder(context)
                        .data(uri)
                        .size(Size(Dimension(300),Dimension(300)))
                        .videoFrameMillis(1000)
                        .build(),
                    imageLoader = imageLoader
                )
                Image(
                    painter = painter,
                    contentDescription = null,
                )
            }
        }
    }
}
