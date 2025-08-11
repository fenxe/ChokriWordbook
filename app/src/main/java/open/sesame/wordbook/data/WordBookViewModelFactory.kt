package open.sesame.wordbook.data

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

@Suppress("UNCHECKED_CAST")
class WordBookViewModelFactory(
    private val application: Application
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T =
        when {
            modelClass.isAssignableFrom(WordBookViewModel::class.java) ->
                WordBookViewModel(application) as T
            else ->
                throw IllegalArgumentException("Unknown ViewModel class: $modelClass")
        }
}
