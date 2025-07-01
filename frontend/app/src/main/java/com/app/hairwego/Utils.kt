package com.app.hairwego

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.app.hairwego.data.local.FaceScanEntity
import com.app.hairwego.data.local.RecommendationEntity
import com.app.hairwego.data.model.HistoryResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.net.URL
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private const val MAXIMAL_SIZE = 1000000 //1 MB
private const val FILENAME_FORMAT = "yyyyMMdd_HHmmss"
private val timeStamp: String = SimpleDateFormat(FILENAME_FORMAT, Locale.US).format(Date())


fun saveImageToInternalStorage(context: Context, uri: Uri, fileName: String): String? {
    return try {
        val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
        val file = File(context.filesDir, fileName)
        FileOutputStream(file).use { outputStream ->
            inputStream?.copyTo(outputStream)
        }
        file.absolutePath
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}


@RequiresApi(Build.VERSION_CODES.Q)
fun File.reduceFileImage(): File {
    val file = this
    val bitmap = BitmapFactory.decodeFile(file.path).getRotatedBitmap(file)
    var compressQuality = 100
    var streamLength: Int
    do {
        val bmpStream = ByteArrayOutputStream()
        bitmap?.compress(Bitmap.CompressFormat.JPEG, compressQuality, bmpStream)
        val bmpPicByteArray = bmpStream.toByteArray()
        streamLength = bmpPicByteArray.size
        compressQuality -= 5
    } while (streamLength > MAXIMAL_SIZE)
    bitmap?.compress(Bitmap.CompressFormat.JPEG, compressQuality, FileOutputStream(file))
    return file
}

@RequiresApi(Build.VERSION_CODES.Q)
fun Bitmap.getRotatedBitmap(file: File): Bitmap? {
    val orientation = ExifInterface(file).getAttributeInt(
        ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED
    )
    return when (orientation) {
        ExifInterface.ORIENTATION_ROTATE_90 -> rotateImage(this, 90F)
        ExifInterface.ORIENTATION_ROTATE_180 -> rotateImage(this, 180F)
        ExifInterface.ORIENTATION_ROTATE_270 -> rotateImage(this, 270F)
        ExifInterface.ORIENTATION_NORMAL -> this
        else -> this
    }
}


fun rotateImage(source: Bitmap, angle: Float): Bitmap {
    val matrix = Matrix()
    matrix.postRotate(angle)
    return Bitmap.createBitmap(
        source, 0, 0, source.width, source.height, matrix, true
    )
}

fun formatToIndonesianTime(timestamp: Long): String{
    val dateFormat = SimpleDateFormat("dd MMMM yyyy", Locale("id", "ID"))
    return dateFormat.format(timestamp)
}

fun createCustomTempFile(context: Context): File {
    val filesDir = context.externalCacheDir
    return File.createTempFile(timeStamp, ".jpg", filesDir)
}

suspend fun mapToEntities(context: Context, response: HistoryResponse): Pair<List<FaceScanEntity>, List<RecommendationEntity>> {
    val scanEntities = mutableListOf<FaceScanEntity>()
    val recommendationEntities = mutableListOf<RecommendationEntity>()


    response.forEach { (timestamp, scanList) ->
        scanList.forEach { dto ->
            val localPathScanImage = downloadAndSaveImage(context, "http://192.168.1.5:5000/${dto.scanImage}")
            val localPathScanImageCropped = downloadAndSaveImage(context, "http://192.168.1.5:5000/${dto.scanImageCropped}")
            val scanEntity = FaceScanEntity(
                faceScanId = dto.faceScanId,
                faceShape = dto.faceShape,
                scanImage = localPathScanImage,
                scanImageCropped = localPathScanImageCropped,
                scanDate = dto.scanDate
            )
            scanEntities.add(scanEntity)

            dto.recommendations.forEach { rec ->
                val localPath = downloadAndSaveImage(context, "http://192.168.1.5:5000/${rec.image}")
                val recommendationEntity = RecommendationEntity(
                    faceScanId = dto.faceScanId,
                    haircutName = rec.haircutName,
                    description = rec.description,
                    image = localPath
                )
                recommendationEntities.add(recommendationEntity)
            }
        }
    }

    return Pair(scanEntities, recommendationEntities)
}

suspend fun downloadAndSaveImage(context: Context, imageUrl: String): String {
    return withContext(Dispatchers.IO) {
        try {
            val url = URL(imageUrl)
            val connection = url.openConnection()
            connection.connect()

            val input = connection.getInputStream()
            val fileName = imageUrl.substringAfterLast("/")
            val file = File(context.filesDir, fileName)

            val output = FileOutputStream(file)
            input.copyTo(output)
            output.close()
            input.close()

            file.absolutePath
        } catch (e: Exception) {
            Log.e("ImageDownload", "Gagal download: $imageUrl", e)
            e.printStackTrace()
            "" // fallback, atau bisa return null
        }
    }
}





