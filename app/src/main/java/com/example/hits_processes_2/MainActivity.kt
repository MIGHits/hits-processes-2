package com.example.hits_processes_2

import android.Manifest
import android.content.ClipData
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.LaunchedEffect
import androidx.core.content.ContextCompat
import androidx.navigation.compose.rememberNavController
import com.example.hits_processes_2.common.navigation.AppNavGraph
import com.example.hits_processes_2.common.navigation.ScreenRoute
import com.example.hits_processes_2.feature.authorization.data.TokenStorage
import com.example.hits_processes_2.feature.authorization.domain.SessionExpiredNotifier
import com.example.hits_processes_2.ui.theme.Hitsprocesses2Theme
import org.koin.android.ext.android.inject

class MainActivity : ComponentActivity() {

    private val tokenStorage: TokenStorage by inject()
    private val sessionExpiredNotifier: SessionExpiredNotifier by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        requestNotificationPermissionIfNeeded()
        handleOpenFileIntent(intent)

        val startDestination = if (tokenStorage.getTokens() != null) {
            ScreenRoute.Courses.route
        } else {
            ScreenRoute.Authorization.route
        }

        setContent {
            Hitsprocesses2Theme {
                val navController = rememberNavController()

                LaunchedEffect(Unit) {
                    sessionExpiredNotifier.sessionExpiredEvents.collect {
                        navController.navigate(ScreenRoute.Authorization.route) {
                            popUpTo(0) { inclusive = true }
                            launchSingleTop = true
                        }
                    }
                }

                AppNavGraph(
                    navController = navController,
                    startDestination = startDestination,
                )
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleOpenFileIntent(intent)
    }

    private fun handleOpenFileIntent(intent: Intent?) {
        if (intent?.action != ACTION_OPEN_DOWNLOADED_FILE) return

        val fileUri = intent.getStringExtra(EXTRA_OPEN_FILE_URI)?.let(Uri::parse) ?: return
        val mimeType = intent.getStringExtra(EXTRA_OPEN_FILE_MIME) ?: DEFAULT_MIME_TYPE

        try {
            val viewIntent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(fileUri, mimeType)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_NEW_TASK)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    clipData = ClipData.newRawUri("", fileUri)
                }
            }
            val chooserIntent = Intent.createChooser(viewIntent, null).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            startActivity(chooserIntent)
        } catch (_: Exception) {
            Toast.makeText(
                this,
                getString(R.string.file_attachment_download_error_title),
                Toast.LENGTH_SHORT,
            ).show()
        } finally {
            intent.removeExtra(EXTRA_OPEN_FILE_URI)
            intent.removeExtra(EXTRA_OPEN_FILE_MIME)
        }
    }

    private fun requestNotificationPermissionIfNeeded() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS,
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            registerForActivityResult(ActivityResultContracts.RequestPermission()) { }
                .launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    companion object {
        const val ACTION_OPEN_DOWNLOADED_FILE = "com.example.hits_processes_2.action.OPEN_DOWNLOADED_FILE"
        const val EXTRA_OPEN_FILE_URI = "extra_open_file_uri"
        const val EXTRA_OPEN_FILE_MIME = "extra_open_file_mime"
        private const val DEFAULT_MIME_TYPE = "application/octet-stream"
    }
}
