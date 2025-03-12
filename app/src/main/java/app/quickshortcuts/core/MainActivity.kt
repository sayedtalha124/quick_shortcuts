package app.quickshortcuts.core

import android.os.Bundle
import android.provider.ContactsContract
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts.PickContact
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import app.quickshortcuts.ui.theme.QuickShortcutsTheme

class MainActivity : ComponentActivity() {
    val getPerson = registerForActivityResult(PickContact()) {
        it?.also { contactUri ->
            val projection = arrayOf(
                ContactsContract.CommonDataKinds.Phone.LOOKUP_KEY,
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                ContactsContract.CommonDataKinds.Phone.NORMALIZED_NUMBER
            )

            contentResolver?.query(contactUri, projection, null, null, null)?.apply {
                moveToFirst()
                //val id = viewModel.addPerson(DefaultPerson(getString(0), getString(1), getString(2)))
                close()
            }
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            QuickShortcutsTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    NavigationStack()
                }
            }
        }



    }


}