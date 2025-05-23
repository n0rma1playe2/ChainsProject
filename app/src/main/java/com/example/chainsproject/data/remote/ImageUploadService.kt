package com.example.chainsproject.data.remote

import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import java.io.File

interface ImageUploadService {
    @Multipart
    @POST("upload/image")
    suspend fun uploadImage(
        @Part image: MultipartBody.Part
    ): ApiResponse<String>
}

class ImageUploadRepository @Inject constructor(
    private val imageUploadService: ImageUploadService
) {
    suspend fun uploadImage(imageFile: File): Result<String> {
        return try {
            // 创建文件请求体
            val requestFile = imageFile.asRequestBody("image/*".toMediaTypeOrNull())
            
            // 创建 MultipartBody.Part
            val imagePart = MultipartBody.Part.createFormData(
                "image",
                imageFile.name,
                requestFile
            )

            // 上传图片
            val response = imageUploadService.uploadImage(imagePart)
            
            if (response.code == 200 && response.data != null) {
                Result.success(response.data)
            } else {
                Result.failure(Exception(response.message ?: "上传失败"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
} 