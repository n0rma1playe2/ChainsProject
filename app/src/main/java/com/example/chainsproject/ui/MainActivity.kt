package com.example.chainsproject.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.navigation.compose.rememberNavController
import com.example.chainsproject.navigation.AuthNavigation
import com.example.chainsproject.navigation.MainNavigation
import com.example.chainsproject.ui.auth.AuthState
import com.example.chainsproject.ui.auth.AuthViewModel
import com.example.chainsproject.ui.components.BottomNavBar
import com.example.chainsproject.ui.theme.ChainsProjectTheme
import dagger.hilt.android.AndroidEntryPoint
import androidx.hilt.navigation.compose.hiltViewModel

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ChainsProjectTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainScreen()
                }
            }
        }
    }
}

@Composable
fun MainScreen(
    authViewModel: AuthViewModel = hiltViewModel()
) {
    val authState by authViewModel.authState.collectAsState()
    val navController = rememberNavController()

    LaunchedEffect(Unit) {
        authViewModel.checkAuthState()
    }

    when (authState) {
        is AuthState.Initial -> {
            AuthNavigation(
                navController = navController,
                onAuthSuccess = {
                    authViewModel.checkAuthState()
                }
            )
        }
        is AuthState.Loading -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = androidx.compose.ui.Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
        is AuthState.Authenticated -> {
            Scaffold(
                bottomBar = {
                    BottomNavBar(navController = navController)
                }
            ) { paddingValues ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                ) {
                    MainNavigation(navController = navController)
                }
            }
        }
        is AuthState.Error -> {
            // 显示错误信息并允许重试
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = (authState as AuthState.Error).message,
                    color = MaterialTheme.colorScheme.error
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = { authViewModel.checkAuthState() }) {
                    Text("重试")
                }
            }
        }
    }
} 