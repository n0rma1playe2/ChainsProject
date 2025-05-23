package com.example.chainsproject.data.remote

import com.example.chainsproject.data.remote.retrofit.RetrofitClient
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ImageUploadRepository @Inject constructor(
    private val retrofitClient: RetrofitClient
) {
    suspend fun uploadImage(
        imageFile: File,
        onProgress: (Float) -> Unit
    ): Result<String> {
        return try {
            // 创建请求体
            val requestBody = imageFile.asRequestBody("image/*".toMediaTypeOrNull())
            val part = MultipartBody.Part.createFormData("file", imageFile.name, requestBody)

            // 创建进度监听器
            val progressListener = object : okhttp3.Interceptor {
                override fun intercept(chain: okhttp3.Interceptor.Chain): okhttp3.Response {
                    val originalResponse = chain.proceed(chain.request())
                    val responseBody = originalResponse.body
                    val contentLength = responseBody?.contentLength() ?: 0L
                    var bytesRead = 0L

                    val source = responseBody?.source()
                    source?.request(Long.MAX_VALUE)
                    val buffer = source?.buffer

                    val newResponseBody = object : okhttp3.ResponseBody() {
                        override fun contentType() = responseBody?.contentType()
                        override fun contentLength() = contentLength
                        override fun source() = buffer?.let {
                            okio.ForwardingSource(it) {
                                val bytesRead = super.read(sink, byteCount)
                                if (bytesRead != -1L) {
                                    onProgress(bytesRead.toFloat() / contentLength)
                                }
                                bytesRead
                            }
                        } ?: throw IllegalStateException("Buffer is null")
                    }

                    return originalResponse.newBuilder()
                        .body(newResponseBody)
                        .build()
                }
            }

            // 添加上传进度监听器
            val client = retrofitClient.okHttpClient.newBuilder()
                .addInterceptor(progressListener)
                .build()

            val apiService = retrofitClient.createApiService(client)
            val response = apiService.uploadImage(part)

            if (response.code == 200) {
                Result.success(response.data)
            } else {
                Result.failure(Exception(response.message ?: "图片上传失败"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
} 