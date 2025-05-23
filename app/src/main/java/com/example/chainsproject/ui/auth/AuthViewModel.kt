package com.example.chainsproject.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chainsproject.data.local.dao.UserDao
import com.example.chainsproject.data.remote.ApiService
import com.example.chainsproject.data.remote.model.LoginRequest
import com.example.chainsproject.data.remote.model.RegisterRequest
import com.example.chainsproject.domain.model.UserRole
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val apiService: ApiService,
    private val userDao: UserDao
) : ViewModel() {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Initial)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    fun login(username: String, password: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                val response = apiService.login(LoginRequest(username, password))
                if (response.code == 200 && response.data != null) {
                    val user = response.data
                    _authState.value = AuthState.Authenticated(
                        userId = user.id,
                        username = user.username,
                        role = user.role,
                        walletAddress = user.walletAddress
                    )
                } else {
                    _authState.value = AuthState.Error(response.message)
                }
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.message ?: "登录失败")
            }
        }
    }

    fun register(
        username: String,
        password: String,
        role: UserRole,
        walletAddress: String?
    ) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                val response = apiService.register(
                    RegisterRequest(username, password, role, walletAddress)
                )
                if (response.code == 200 && response.data != null) {
                    val user = response.data
                    _authState.value = AuthState.Authenticated(
                        userId = user.id,
                        username = user.username,
                        role = user.role,
                        walletAddress = user.walletAddress
                    )
                } else {
                    _authState.value = AuthState.Error(response.message)
                }
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.message ?: "注册失败")
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                val response = apiService.logout()
                if (response.code == 200) {
                    _authState.value = AuthState.Initial
                } else {
                    _authState.value = AuthState.Error(response.message)
                }
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.message ?: "登出失败")
            }
        }
    }

    fun checkAuthState() {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                val response = apiService.getUserProfile()
                if (response.code == 200 && response.data != null) {
                    val user = response.data
                    _authState.value = AuthState.Authenticated(
                        userId = user.id,
                        username = user.username,
                        role = user.role,
                        walletAddress = user.walletAddress
                    )
                } else {
                    _authState.value = AuthState.Initial
                }
            } catch (e: Exception) {
                _authState.value = AuthState.Initial
            }
        }
    }
} 