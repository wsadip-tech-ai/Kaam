package com.kaam.app.ui.components

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage

@Composable
fun ImagePicker(
    currentImageUri: Uri?,
    onImageSelected: (Uri) -> Unit,
    modifier: Modifier = Modifier,
    size: Dp = 100.dp,
) {
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
    ) { uri -> uri?.let { onImageSelected(it) } }

    val shape = RoundedCornerShape(12.dp)

    Box(
        modifier = modifier
            .size(size)
            .clip(shape)
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, shape)
            .clickable { launcher.launch("image/*") },
        contentAlignment = Alignment.Center,
    ) {
        if (currentImageUri != null) {
            AsyncImage(
                model = currentImageUri,
                contentDescription = "Selected image",
                contentScale = ContentScale.Crop,
                modifier = Modifier.size(size),
            )
        } else {
            Icon(
                imageVector = Icons.Default.AddAPhoto,
                contentDescription = "Pick image",
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}
