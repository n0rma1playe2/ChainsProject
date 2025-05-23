package com.example.chainsproject.data.remote

import com.example.chainsproject.data.remote.model.ApiResponse
import com.example.chainsproject.data.remote.model.LoginRequest
import com.example.chainsproject.data.remote.model.RegisterRequest
import com.example.chainsproject.data.remote.model.UserResponse
import retrofit2.http.*

interface ApiService {
    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): ApiResponse<UserResponse>

    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequest): ApiResponse<UserResponse>

    @GET("users/profile")
    suspend fun getUserProfile(): ApiResponse<UserResponse>

    @PUT("users/profile")
    suspend fun updateUserProfile(@Body user: UserResponse): ApiResponse<UserResponse>

    @POST("auth/logout")
    suspend fun logout(): ApiResponse<Unit>
} 