package app.quickshortcuts

import androidx.compose.ui.graphics.vector.ImageVector

data class TilesListItem(val title: String,
                         val subTitle: String,
                         val  icon: ImageVector,
                         val  click: () -> Unit,
                         val  isShortcutAdded: Boolean)
