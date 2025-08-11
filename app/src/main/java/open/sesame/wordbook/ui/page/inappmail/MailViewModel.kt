package open.sesame.wordbook.ui.page.inappmail

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

// Shared State for Unread Mail
class MailViewModel(application: Application) : ViewModel() {
    private val _mailItem = mutableStateListOf<MailItem>()
    val mailItem: List<MailItem> get() = _mailItem

    private val _hasUnreadMail = MutableStateFlow(false)
    val hasUnreadMail: StateFlow<Boolean> = _hasUnreadMail

    fun updateUnreadStatus(mailItem: List<MailItem>) {
        _hasUnreadMail.value = mailItem.any { !it.read }
    }

    // Now, another JsonHelper.kt wkwkwk
    fun loadMails(context: Context, fileName: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val file = File(context.filesDir, fileName)

            // Load existing saved mails if file exists
            val existing = if (file.exists()) {
                readMailItemsFromFile(context, fileName)
            } else {
                emptyList()
            }

            // Always copy asset file to temp location
            copyJsonFileToStorage(context, fileName, "temp_$fileName")
            val newFromAsset = readMailItemsFromFile(context, "temp_$fileName")

            // Merge: preserve read status, avoid duplicates
            val merged = newFromAsset.map { newItem ->
                existing.find { it.id == newItem.id } ?: newItem
            }

            withContext(Dispatchers.Main) {
                _mailItem.clear()
                _mailItem.addAll(merged)
                updateUnreadStatus(_mailItem)
                Log.d("MailViewModel", "Merged mail count: ${merged.size}")
            }

            // Save merged result back to file
            writeMailItemsToFile(context, fileName, merged)
        }
    }


    fun markAsRead(index: Int) {
        if (!_mailItem[index].read) {
            _mailItem[index] = _mailItem[index].copy(read = true)
            updateUnreadStatus(_mailItem)
        }
    }
    fun saveJsonMails(context: Context, fileName: String) {
        viewModelScope.launch(Dispatchers.IO) {
            writeMailItemsToFile(context, fileName, _mailItem)
        }
    }
}

class MailViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MailViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MailViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
