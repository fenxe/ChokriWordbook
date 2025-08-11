package open.sesame.wordbook.data

import android.app.Application
import android.content.Context
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import kotlin.getValue

class DebugViewModel(ctx: Context): ViewModel() {
    //de bug purpose only
    private val dao = ChokriDatabase.create(ctx).wordDao()
    private val _items = mutableStateListOf<WordBookClass>()

    val items: List<WordBookClass> get() = _items
    init {
        viewModelScope.launch {
            _items.addAll(dao.getAllWords())
        }
    }
}

class DebugViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DebugViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return DebugViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
