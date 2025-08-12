package open.sesame.wordbook

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.ViewModelProvider
import open.sesame.wordbook.data.WordBookViewModel
import open.sesame.wordbook.data.WordBookViewModelFactory
import open.sesame.wordbook.ui.MainScreen
import open.sesame.wordbook.ui.page.inappmail.MailViewModel
import open.sesame.wordbook.ui.page.inappmail.MailViewModelFactory
import open.sesame.wordbook.ui.theme.ChokriWordbookTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val factory = WordBookViewModelFactory(application)
        val viewModel = ViewModelProvider(this, factory)[WordBookViewModel::class.java]

        //dbug direct testing
//        val factory1 = DebugViewModelFactory(application)
//        val viewModel1 = ViewModelProvider(this, factory1)[DebugViewModel::class.java]

        //mail viewmodel
        val factory2 = MailViewModelFactory(application)
        val viewModel2 = ViewModelProvider(this, factory2)[MailViewModel::class.java]

        setContent {
            ChokriWordbookTheme {
                MainScreen(viewModel, viewModel2)
//                DeBuggy(viewModel1)
//                MailScreenView()
//                MailDetailReadView()
            }
        }
    }
}

