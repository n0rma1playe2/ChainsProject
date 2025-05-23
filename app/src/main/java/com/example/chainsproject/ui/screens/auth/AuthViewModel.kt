package com.example.chainsproject.ui.screens.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chainsproject.domain.model.RegistrationData
import com.example.chainsproject.domain.model.User
import com.example.chainsproject.domain.model.UserType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AuthState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val user: User? = null
)

@HiltViewModel
class AuthViewModel @Inject constructor() : ViewModel() {
    private val _authState = MutableStateFlow(AuthState())
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    fun register(registrationData: RegistrationData) {
        viewModelScope.launch {
            _authState.value = _authState.value.copy(isLoading = true, error = null)
            try {
                // TODO: 实现实际的注册逻辑
                // 1. 验证输入数据
                validateRegistrationData(registrationData)
                
                // 2. 根据用户类型执行不同的注册流程
                when (registrationData.userType) {
                    UserType.CONSUMER -> registerConsumer(registrationData)
                    UserType.SUPPLY_CHAIN -> registerSupplyChain(registrationData)
                    UserType.REGULATORY -> registerRegulatory(registrationData)
                }
            } catch (e: Exception) {
                _authState.value = _authState.value.copy(
                    isLoading = false,
                    error = e.message ?: "注册失败"
                )
            }
        }
    }

    private fun validateRegistrationData(data: RegistrationData) {
        when {
            data.username.isBlank() -> throw IllegalArgumentException("用户名不能为空")
            data.password.length < 6 -> throw IllegalArgumentException("密码长度不能少于6位")
            data.phone.isBlank() -> throw IllegalArgumentException("手机号码不能为空")
            data.userType != UserType.CONSUMER && data.organization.isNullOrBlank() ->
                throw IllegalArgumentException("组织名称不能为空")
        }
    }

    private suspend fun registerConsumer(data: RegistrationData) {
        // TODO: 实现消费者注册逻辑
        // 1. 创建钱包地址
        // 2. 保存用户信息
        // 3. 更新认证状态
        _authState.value = _authState.value.copy(
            isLoading = false,
            user = User(
                id = "temp_id",
                username = data.username,
                userType = data.userType,
                phone = data.phone
            )
        )
    }

    private suspend fun registerSupplyChain(data: RegistrationData) {
        // TODO: 实现供应链参与者注册逻辑
        // 1. 验证组织信息
        // 2. 创建钱包地址
        // 3. 保存用户信息
        // 4. 更新认证状态
        _authState.value = _authState.value.copy(
            isLoading = false,
            user = User(
                id = "temp_id",
                username = data.username,
                userType = data.userType,
                organization = data.organization,
                phone = data.phone
            )
        )
    }

    private suspend fun registerRegulatory(data: RegistrationData) {
        // TODO: 实现监管机构注册逻辑
        // 1. 验证监管机构资质
        // 2. 创建钱包地址
        // 3. 保存用户信息
        // 4. 更新认证状态
        _authState.value = _authState.value.copy(
            isLoading = false,
            user = User(
                id = "temp_id",
                username = data.username,
                userType = data.userType,
                organization = data.organization,
                phone = data.phone
            )
        )
    }
} 