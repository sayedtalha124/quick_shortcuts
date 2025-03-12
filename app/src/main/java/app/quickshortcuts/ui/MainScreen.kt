package app.quickshortcuts.ui

import android.os.Build
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Call
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import app.quickshortcuts.R
import app.quickshortcuts.TilesListItem
import app.quickshortcuts.core.Screen
import app.quickshortcuts.core.getPreference
import app.quickshortcuts.ui.theme.PrimaryBlue
import app.quickshortcuts.ui.theme.QuickShortcutsTheme
import kotlinx.coroutines.launch

@Composable
fun MainScreen(navController: NavHostController) {
    var click = {
        navController.navigate(route = Screen.SpeedDialIntro.route)

    }
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var isTimerAdded by remember { mutableStateOf(false) }

    val timer = TilesListItem(
        "Timer",
        "Add a quick timer.",
        Icons.Rounded.Notifications,
        click,
        isTimerAdded
    )
    var speedDial = TilesListItem(
        "Speed Dial", "Add your fav contacts for speed dial",
        Icons.Rounded.Call, click, isTimerAdded
    )
    LaunchedEffect (key1 = "ab"){
        coroutineScope.launch {
            getPreference(context).collect {
                isTimerAdded = it
            }
        }
    }
    Column(
        modifier = Modifier.systemBarsPadding()
    ) {
        HorizontalDivider(thickness = 1.dp)
        Item(
            speedDial
        )
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            HorizontalDivider(thickness = 1.dp)
            Item(
                timer
            )
        } else {
            Text(text = "Pull down notification bar to search for quick tiles and add.")
            //todo add proper tile

        }


    }


}

@Composable
fun Item(
    tilesListItem: TilesListItem
) {

    Column(
        Modifier.fillMaxWidth()
            .clickable(true, onClick = tilesListItem.click)
            .padding(12.dp)
    ) {

        Row {
            Icon(
                tilesListItem.icon, tint = PrimaryBlue,
                modifier = Modifier.size(15.dp, 25.dp),
                contentDescription = stringResource(id = R.string.app_name)
            )
            Spacer(Modifier.width(8.dp))
            Text(text = tilesListItem.title)

        }
        Text(text = tilesListItem.subTitle)
    }

}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    QuickShortcutsTheme {
        MainScreen(NavHostController(LocalContext.current))
    }
}