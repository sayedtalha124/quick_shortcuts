package app.quickshortcuts.ui

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import dev.shreyaspatil.permissionflow.compose.rememberPermissionFlowRequestLauncher

@Composable
fun PermisssionLauncherScreen() {
    val permissionLauncher = rememberPermissionFlowRequestLauncher()

    ElevatedButton(onClick = {
        permissionLauncher.launch(arrayOf(Manifest.permission.CALL_PHONE))
    }) {
        Text("Request Permissions")
    }
}
@Composable
fun ShowInstructions(
    shouldShowRationale: Boolean
) {
    val context= LocalContext.current
    Column {
        if (shouldShowRationale) {
            // If the user has denied the permission but the rationale can be shown,
            // then gently explain why the app requires this permission
            val textToShow =   "Permission is important for this app. Please grant the permission."
            Text(textToShow)
            ElevatedButton(onClick = {
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                    data = Uri.fromParts("package", context.packageName, null)
                }
                context.startActivity(intent)
            }) {
                Text("Request Permissions")
            }
        } else {
            // If it's the first time the user lands on this feature, or the user
            // doesn't want to be asked again for this permission, explain that the
            // permission is required
            val textToShow =  "Call permission required for this feature to be available. " +
                    "Please grant the permission"
            Text(textToShow)
            PermisssionLauncherScreen()
        }

    }
}

