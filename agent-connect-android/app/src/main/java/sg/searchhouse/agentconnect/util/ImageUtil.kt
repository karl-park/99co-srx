package sg.searchhouse.agentconnect.util

import android.content.ContentValues
import android.content.Context
import android.content.IntentSender
import android.graphics.*
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.annotation.WorkerThread
import androidx.core.content.FileProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.RequestOptions
import sg.searchhouse.agentconnect.BuildConfig
import sg.searchhouse.agentconnect.R
import sg.searchhouse.agentconnect.constant.AppConstant
import java.io.*
import java.util.concurrent.CancellationException
import java.util.concurrent.ExecutionException
import kotlin.math.min


object ImageUtil {

    fun saveImageFromBitmap(
        context: Context,
        bitmap: Bitmap,
        count: Int? = null,
        fileName: String? = null
    ): String {
        val bytes = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        //create file directory
        val fileDirectory =
            File(context.getExternalFilesDir(null)?.absoluteFile.toString() + "/images")
        //file not exit -> create directory
        if (!fileDirectory.exists()) {
            fileDirectory.mkdirs()
        }

        try {
            val indicator = count ?: 0
            val file = if (fileName?.isNotEmpty() == true) {
                File(fileDirectory, "$fileName.jpg")
            } else {
                File(fileDirectory, "${DateTimeUtil.getCurrentFileNameTimeStamp()}_$indicator.jpg")
            }

            file.createNewFile()
            val fileOutputStream = FileOutputStream(file)
            fileOutputStream.write(bytes.toByteArray())
            MediaScannerConnection.scanFile(
                context,
                arrayOf(file.path),
                arrayOf("image/jpeg"),
                null
            )
            fileOutputStream.close()
            return file.absolutePath
        } catch (e: IOException) {
            ErrorUtil.handleError(context, R.string.exception_io, e)
        }

        return ""
    }

    fun generateImageFile(context: Context, count: Int? = null, fileName: String? = null): Uri {
        //create file directory
        val fileDirectory =
            File(context.getExternalFilesDir(null)?.absoluteFile.toString() + "/images")
        //file not exit -> create directory
        if (!fileDirectory.exists()) {
            fileDirectory.mkdirs()
        }

        val indicator = count ?: 0
        val file = if (fileName?.isNotEmpty() == true) {
            File(fileDirectory, "$fileName.jpg")
        } else {
            File(fileDirectory, "${DateTimeUtil.getCurrentFileNameTimeStamp()}_$indicator.jpg")
        }

        return FileProvider.getUriForFile(
            context,
            "${BuildConfig.APPLICATION_ID}.fileProvider",
            file
        )
    }

    fun getFileFromBitmap(context: Context, bitmap: Bitmap): File {
        return File(saveImageFromBitmap(context, bitmap))
    }

    fun getFileFromUri(context: Context, uri: Uri, count: Int? = null): File? {
        var file: File? = null
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val source = ImageDecoder.createSource(context.contentResolver, uri)
            val bitmap = ImageDecoder.decodeBitmap(source)
            file = File(saveImageFromBitmap(context, bitmap, count))
        } else {
            MediaStore.Images.Media.getBitmap(context.contentResolver, uri)?.let { bitmap ->
                file = File(saveImageFromBitmap(context, bitmap, count))
            }
        }
        return file
    }

    fun createListingImageUri(context: Context): Uri? {
        val file = File(
            context.getExternalFilesDir(AppConstant.DIR_LISTING_IMAGE),
            "${DateTimeUtil.getCurrentFileNameTimeStamp()}.jpg"
        )
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            FileProvider.getUriForFile(context, "${context.packageName}.fileProvider", file)
        } else {
            Uri.fromFile(file)
        }
    }

    fun rotateBitmap(inputBitmap: Bitmap, angle: Float): Bitmap {
        val mat = Matrix()
        mat.postRotate(angle)
        return Bitmap.createBitmap(
            inputBitmap,
            0,
            0,
            inputBitmap.width,
            inputBitmap.height,
            mat,
            true
        )
    }

    /**
     * `cropParams`: x, y, width, height of crop area
     */
    @Throws(IllegalArgumentException::class)
    @WorkerThread
    fun getSquareCroppedImageFile(
        applicationContext: Context,
        imageUri: Uri,
        coordinate: Pair<Int, Int>? = null,
        cropSize: Int
    ): File? {
        val bitmap = getBitmapFromImageUrl(
            applicationContext,
            imageUri.toString()
        ) ?: return null
        val cropped = squareCropBitmap(bitmap, coordinate, cropSize)
        return getFileFromBitmap(context = applicationContext, bitmap = cropped)
    }

    @Throws(IllegalArgumentException::class)
    @WorkerThread
    private fun squareCropBitmap(
        inputBitmap: Bitmap,
        coordinate: Pair<Int, Int>? = null,
        cropSize: Int
    ): Bitmap {
        val originalHeight = inputBitmap.height
        val originalWidth = inputBitmap.width
        val smallerDimension = min(originalHeight, originalWidth)
        val x = coordinate?.first?.run {
            if (cropSize <= 0) return@run null
            this * smallerDimension / cropSize
        } ?: (originalWidth - smallerDimension) / 2
        val y = coordinate?.second?.run {
            if (cropSize <= 0) return@run null
            this * smallerDimension / cropSize
        } ?: (originalHeight - smallerDimension) / 2
        val newWidth = if (x + smallerDimension > originalWidth) {
            originalWidth - x
        } else {
            smallerDimension
        }
        val newHeight = if (y + smallerDimension > originalHeight) {
            originalHeight - y
        } else {
            smallerDimension
        }
        return Bitmap.createBitmap(inputBitmap, x, y, newWidth, newHeight)
    }

    @WorkerThread
    fun getBitmapFromImageUrl(
        applicationContext: Context,
        imageUrl: String
    ): Bitmap? {
        return try {
            Glide.with(applicationContext)
                .asBitmap()
                .load(imageUrl)
                .submit()
                .get()
        } catch (e: CancellationException) {
            e.printStackTrace()
            null
        } catch (e: ExecutionException) {
            e.printStackTrace()
            null
        } catch (e: InterruptedException) {
            e.printStackTrace()
            null
        }
    }

    @WorkerThread
    fun getCircularBitmapFromImageUrl(
        applicationContext: Context,
        imageUrl: String
    ): Bitmap? {
        //TODO: to refine. possible exception. don't know what exception will come out.
        //TODO: may need to check again after adding image to notification
        return try {
            Glide.with(applicationContext)
                .asBitmap()
                .load(imageUrl)
                .apply(RequestOptions.bitmapTransform(CircleCrop()))
                .submit()
                .get()
        } catch (e: CancellationException) {
            e.printStackTrace()
            null
        } catch (e: ExecutionException) {
            e.printStackTrace()
            null
        } catch (e: InterruptedException) {
            e.printStackTrace()
            null
        }
    }

    fun getUriFromFile(context: Context, file: File): Uri? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            FileProvider.getUriForFile(context, "${context.packageName}.fileProvider", file)
        } else {
            Uri.fromFile(file)
        }
    }

    fun maybeAppendBaseUrl(context: Context, url: String): String {
        return when {
            url.startsWith("/") -> "${ApiUtil.getBaseUrl(context)}$url"
            else -> url
        }
    }


    fun saveImageToGallery(
        context: Context,
        bitmap: Bitmap,
        fileName: String,
        displayName: String,
        onFinished: (() -> Unit)? = null
    ) {
        val values = ContentValues().apply {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis())
            }
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
            put(MediaStore.Images.ImageColumns.DISPLAY_NAME, displayName)
            put(MediaStore.Images.ImageColumns.TITLE, fileName)
        }
        val uri: Uri? = context.contentResolver.insert(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            values
        )
        uri?.let {
            context.contentResolver.openOutputStream(it)?.let { stream ->
                val outputStream = BufferedOutputStream(stream)
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
                outputStream.close()
            }
        }
        onFinished?.invoke()
    }

}