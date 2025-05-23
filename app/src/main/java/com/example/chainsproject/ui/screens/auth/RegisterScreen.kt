package com.example.chainsproject.ui.screens.auth

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.chainsproject.domain.model.RegistrationData
import com.example.chainsproject.domain.model.UserType
import com.example.chainsproject.navigation.NavRoutes

@Composable
fun RegisterScreen(
    navController: NavController,
    viewModel: AuthViewModel = hiltViewModel()
) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var selectedUserType by remember { mutableStateOf<UserType?>(null) }
    var organization by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }

    val authState by viewModel.authState.collectAsState()

    LaunchedEffect(authState.user) {
        authState.user?.let { user ->
            when (user.userType) {
                UserType.CONSUMER -> navController.navigate(NavRoutes.ConsumerHome.route)
                UserType.SUPPLY_CHAIN -> navController.navigate(NavRoutes.SupplyChainHome.route)
                UserType.REGULATORY -> navController.navigate(NavRoutes.RegulatoryHome.route)
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "注册账号",
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier.padding(vertical = 32.dp)
        )

        // 用户类型选择
        Text(
            text = "请选择用户类型",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        UserTypeSelector(
            selectedType = selectedUserType,
            onTypeSelected = { selectedUserType = it }
        )

        Spacer(modifier = Modifier.height(24.dp))

        // 注册表单
        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("用户名") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        )

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("密码") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        )

        OutlinedTextField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            label = { Text("确认密码") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        )

        OutlinedTextField(
            value = phone,
            onValueChange = { phone = it },
            label = { Text("手机号码") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        )

        if (selectedUserType != UserType.CONSUMER) {
            OutlinedTextField(
                value = organization,
                onValueChange = { organization = it },
                label = { Text("组织名称") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            )
        }

        // 错误提示
        authState.error?.let { error ->
            Text(
                text = error,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }

        Button(
            onClick = {
                if (password != confirmPassword) {
                    // 显示密码不匹配错误
                    return@Button
                }
                
                selectedUserType?.let { userType ->
                    viewModel.register(
                        RegistrationData(
                            username = username,
                            password = password,
                            userType = userType,
                            organization = if (userType != UserType.CONSUMER) organization else null,
                            phone = phone
                        )
                    )
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            enabled = !authState.isLoading
        ) {
            if (authState.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = MaterialTheme.colorScheme.onPrimary
                )
            } else {
                Text("注册")
            }
        }

        TextButton(
            onClick = { navController.navigateUp() },
            modifier = Modifier.padding(top = 16.dp)
        ) {
            Text("已有账号？返回登录")
        }
    }
}

@Composable
private fun UserTypeSelector(
    selectedType: UserType?,
    onTypeSelected: (UserType) -> Unit
) {
    Column {
        UserType.values().forEach { userType ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .selectable(
                        selected = (userType == selectedType),
                        onClick = { onTypeSelected(userType) },
                        role = Role.RadioButton
                    )
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = (userType == selectedType),
                    onClick = null
                )
                Text(
                    text = when (userType) {
                        UserType.CONSUMER -> "消费者"
                        UserType.SUPPLY_CHAIN -> "供应链参与者"
                        UserType.REGULATORY -> "监管机构"
                    },
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
        }
    }
} 