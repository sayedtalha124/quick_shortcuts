package app.quickshortcuts.ui

import ContactInfo
import ContactPicker
import android.Manifest
import android.os.Build
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.quickshortcuts.SpeedDialNotification.find
import app.quickshortcuts.SpeedDialNotification.notificationManager
import app.quickshortcuts.SpeedDialNotification.requestNotificationsPermission
import app.quickshortcuts.SpeedDialNotification.toggle
import app.quickshortcuts.core.Utils
import app.quickshortcuts.core.clearUserPreference
import app.quickshortcuts.core.getPreference
import app.quickshortcuts.core.getUserPreference
import app.quickshortcuts.core.onDeleteContact
import app.quickshortcuts.core.saveContactDetails
import app.quickshortcuts.ui.theme.QuickShortcutsTheme
import dev.shreyaspatil.permissionflow.compose.rememberPermissionState
import kotlinx.coroutines.launch


@Composable
fun SpeedDialScreen() {

    val state by rememberPermissionState(Manifest.permission.CALL_PHONE)


    Column(
        modifier = Modifier
            .systemBarsPadding()
            .padding(10.dp)
    ) {
        if (state.isGranted) {
            ContactInfoScreen()
        } else {
            ShowInstructions(state.isRationaleRequired == true)

        }


    }

}


@Composable
fun ContactInfoScreen() {
    val context = LocalContext.current
    var isNotificationVisible = context.find() != null
    val coroutineScope = rememberCoroutineScope()
    var isChecked by remember { mutableStateOf(false) }
    var isSpeedDialAdded by remember { mutableStateOf(false) }
    val contactList by getUserPreference(context).collectAsState(initial = emptyList())
    LaunchedEffect(key1 = "ab") {
        coroutineScope.launch {
            getPreference(context).collect {
                isSpeedDialAdded = it
            }
        }
    }
    Column(
        modifier = Modifier
            .systemBarsPadding()
            .padding(10.dp)
    ) {

        if (contactList.size == 3) {
            if (!isNotificationVisible) {
                ElevatedButton(
                    onClick = {
                        context.apply {
                            when (notificationManager()?.areNotificationsEnabled()) {
                                true -> toggle(contactList)
                                else -> requestNotificationsPermission()
                            }
                        }
                    },
                ) {
                    Icon(Icons.Filled.Notifications, contentDescription = null)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Enable Notification")
                }
                HorizontalDivider(Modifier.padding(10.dp))
            }
            if (!isSpeedDialAdded && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                Text(text = "Add speed dial tile in Quick Settings")
                Switch(
                    checked = isChecked,
                    onCheckedChange = {
                        isChecked = it
                        if (isChecked) {
                            Utils.requestAddTileService(context)
                        }

                    }
                )
                HorizontalDivider(Modifier.padding(10.dp))
            }
        }

        Text(text = "Add three contacts for your speed dial.")

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            ContactPicker(contactList.size == 3, onContactSelected = {
                if (it != null) {
                    /*  arrayList.add(it)*/
                    val list = ArrayList(contactList)
                    list.add(it)
                    coroutineScope.launch {
                        saveContactDetails(context, list)
                    }
                }
            })
            ElevatedButton(
                onClick = {
                    coroutineScope.launch {
                        clearUserPreference(context)
                    }
                },
            ) {
                Icon(Icons.Filled.Clear, contentDescription = "Clear Contacts")
                Spacer(modifier = Modifier.width(4.dp))
                Text("Clear All")
            }

        }
        LazyColumn(modifier = Modifier.fillMaxWidth()) {
            items(contactList) { contact ->
                ContactListItem(
                    contact = contact,
                    onDelete = {
                        coroutineScope.launch {
                            onDeleteContact(
                                contact,
                                context,
                                contactList
                            )
                        }
                    }
                )
            }
        }


    }

}

@Composable
fun ContactListItem(contact: ContactInfo, onDelete: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = contact.displayName.toString(),
                fontWeight = FontWeight.SemiBold,
                fontSize = 18.sp
            )
            Text(text = contact.dial, color = Color.Gray)
        }
        IconButton(onClick = onDelete) {
            Icon(
                Icons.Filled.Delete, contentDescription = "Delete Contact",
                tint = Color(0xFFF6F1F1)
            )
        }
    }
    HorizontalDivider()
}

@Preview(showBackground = true)
@Composable
fun ToggleSwitchPreview() {
    QuickShortcutsTheme { ContactInfoScreen() }
}
