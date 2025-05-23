package com.example.chainsproject.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.os.Build
import androidx.core.content.FileProvider
import androidx.exifinterface.media.ExifInterface
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import kotlin.math.min

object ImageCompressor {
    private const val MAX_WIDTH = 1280
    private const val MAX_HEIGHT = 1280
    private const val QUALITY = 80

    fun compressImage(context: Context, imageUri: Uri): File? {
        return try {
            // 创建临时文件
            val compressedFile = createTempFile(context)
            
            // 获取图片信息
            val inputStream = context.contentResolver.openInputStream(imageUri)
            val options = BitmapFactory.Options().apply {
                inJustDecodeBounds = true
            }
            BitmapFactory.decodeStream(inputStream, null, options)
            inputStream?.close()

            // 计算采样率
            val sampleSize = calculateSampleSize(options.outWidth, options.outHeight)
            
            // 重新打开输入流
            val newInputStream = context.contentResolver.openInputStream(imageUri)
            val newOptions = BitmapFactory.Options().apply {
                inSampleSize = sampleSize
            }
            
            // 解码图片
            var bitmap = BitmapFactory.decodeStream(newInputStream, null, newOptions)
            newInputStream?.close()

            // 处理图片方向
            bitmap = handleImageOrientation(context, imageUri, bitmap)

            // 压缩图片
            val outputStream = FileOutputStream(compressedFile)
            bitmap.compress(Bitmap.CompressFormat.JPEG, QUALITY, outputStream)
            outputStream.close()
            bitmap.recycle()

            compressedFile
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun createTempFile(context: Context): File {
        val timeStamp = System.currentTimeMillis()
        val storageDir = context.getExternalFilesDir("Pictures")
        return File.createTempFile(
            "JPEG_${timeStamp}_",
            ".jpg",
            storageDir
        )
    }

    private fun calculateSampleSize(width: Int, height: Int): Int {
        var sampleSize = 1
        if (width > MAX_WIDTH || height > MAX_HEIGHT) {
            val widthRatio = width.toFloat() / MAX_WIDTH
            val heightRatio = height.toFloat() / MAX_HEIGHT
            sampleSize = min(widthRatio, heightRatio).toInt()
        }
        return sampleSize
    }

    private fun handleImageOrientation(context: Context, imageUri: Uri, bitmap: Bitmap): Bitmap {
        var inputStream: InputStream? = null
        try {
            inputStream = context.contentResolver.openInputStream(imageUri)
            val exif = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                ExifInterface(inputStream!!)
            } else {
                ExifInterface(imageUri.path!!)
            }

            val orientation = exif.getAttributeInt(
                ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_NORMAL
            )

            val matrix = Matrix()
            when (orientation) {
                ExifInterface.ORIENTATION_ROTATE_90 -> matrix.postRotate(90f)
                ExifInterface.ORIENTATION_ROTATE_180 -> matrix.postRotate(180f)
                ExifInterface.ORIENTATION_ROTATE_270 -> matrix.postRotate(270f)
            }

            return Bitmap.createBitmap(
                bitmap,
                0,
                0,
                bitmap.width,
                bitmap.height,
                matrix,
                true
            )
        } catch (e: IOException) {
            e.printStackTrace()
            return bitmap
        } finally {
            inputStream?.close()
        }
    }
} 