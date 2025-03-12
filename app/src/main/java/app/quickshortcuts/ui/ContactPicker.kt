import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.ContactsContract
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContract
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import kotlinx.serialization.Serializable

@Serializable
data class ContactInfo(
    val number: String? = "",
    val displayName: String? = "",
    val normalizedNumber: String? = ""
) {
    val dial: String
        get() {
            var number1 = normalizedNumber
            if (normalizedNumber.isNullOrEmpty()) {
                number1 = number
            }
            return number1.toString()
        }
}


class PickContact : ActivityResultContract<Unit, Uri?>() {
    override fun createIntent(context: Context, input: Unit) =
        Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI).also {
            it.type = ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE
        }


    override fun parseResult(resultCode: Int, intent: Intent?): Uri? {
        return if (resultCode == RESULT_OK) intent?.data else null
    }
}

@Composable
fun ContactPicker(isContactsAdded: Boolean, onContactSelected: (ContactInfo?) -> Unit) {
    val context = LocalContext.current

    val getPerson = rememberLauncherForActivityResult(PickContact()) { contactUri ->
        contactUri?.also {
            context.contentResolver.query(
                it, arrayOf(
                    ContactsContract.CommonDataKinds.Phone.NUMBER,
                    ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                    ContactsContract.CommonDataKinds.Phone.NORMALIZED_NUMBER
                ), null, null, null
            )?.use { cursor ->
                if (cursor.moveToFirst()) {
                    val contact = ContactInfo(
                        cursor.getString(0),
                        cursor.getString(1),
                        cursor.getString(2)
                    )
                    onContactSelected(contact)
                } else {
                    onContactSelected(null) // Handle no contact selected
                }
            } ?: run { onContactSelected(null) } // Handle query failure

        } ?: run { onContactSelected(null) } //Handle null contactUri
    }

    androidx.compose.material3.ElevatedButton(

        onClick = { getPerson.launch(Unit) },
        enabled = !isContactsAdded
    ) {
        Icon(Icons.Filled.Person, contentDescription = "Pick Contacts")
        Spacer(modifier = Modifier.width(4.dp))
        androidx.compose.material3.Text("Pick Contact")
    }
}