// =============================================================================
// MainActivity.kt
// =============================================================================
// The main entry point of the CodePal app with navigation.
// =============================================================================

package com.kilo.companion

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Web
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.core.content.ContextCompat
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.kilo.companion.ui.screens.ConfigManagerScreen
import com.kilo.companion.ui.screens.HomeScreen
import com.kilo.companion.ui.screens.WebViewScreen
import com.kilo.companion.ui.screens.RuntimeScreen
import com.kilo.companion.ui.theme.KiloCompanionTheme
import com.kilo.companion.data.SharedStorageManager

data class NavDestination(val route: String, val label: String, val icon: ImageVector)

val navDestinations = listOf(
    NavDestination("home", "Home", Icons.Default.Home),
    NavDestination("runtime", "Runtime", Icons.Default.PlayArrow),
    NavDestination("config", "Config", Icons.Default.Edit),
    NavDestination("webview", "WebView", Icons.Default.Web)
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            KiloCompanionTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    MainApp()
                }
            }
        }
    }
}

@Composable
fun MainApp() {
    val navController = rememberNavController()
    val context = androidx.compose.ui.platform.LocalContext.current
    val storageManager = remember { SharedStorageManager(context) }
    var hasPermissions by remember { mutableStateOf(false) }
    
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        hasPermissions = permissions.all { it.value }
        if (!hasPermissions) {
            Toast.makeText(context, "Storage permissions required for file access", Toast.LENGTH_LONG).show()
        }
    }
    
    LaunchedEffect(Unit) {
        val permissionsToRequest = mutableListOf<String>()
        
        // Storage permissions
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissionsToRequest.add(Manifest.permission.READ_MEDIA_IMAGES)
        } else {
            permissionsToRequest.add(Manifest.permission.READ_EXTERNAL_STORAGE)
            permissionsToRequest.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }
        
        // Notification permission (Android 13+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissionsToRequest.add(Manifest.permission.POST_NOTIFICATIONS)
        }
        
        val allGranted = permissionsToRequest.all {
            ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
        }
        
        if (!allGranted) {
            permissionLauncher.launch(permissionsToRequest.toTypedArray())
        } else {
            hasPermissions = true
        }
    }
    
    Scaffold(
        bottomBar = { BottomNavigationBar(navController) }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "home",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("home") { HomeScreen(storageManager) }
            composable("runtime") { RuntimeScreen() }
            composable("config") { ConfigManagerScreen(storageManager) }
            composable("webview") { WebViewScreen() }
        }
    }
}

@Composable
fun BottomNavigationBar(navController: NavHostController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    
    NavigationBar {
        navDestinations.forEach { destination ->
            NavigationBarItem(
                icon = { Icon(destination.icon, contentDescription = destination.label) },
                label = { Text(destination.label) },
                selected = currentRoute == destination.route,
                onClick = {
                    navController.navigate(destination.route) {
                        popUpTo(navController.graph.startDestinationId) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    }
}
