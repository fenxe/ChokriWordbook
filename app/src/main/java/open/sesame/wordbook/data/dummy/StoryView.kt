package open.sesame.wordbook.data.dummy

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

// A Page that may come in the future, fr
@Composable
fun StoryView() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val gradientColors = listOf(Color.Red, Color.Yellow, Color.Green, Color.Blue, Color.Magenta)
        val gradientColors2 = listOf(Color.Cyan, Color.Blue, Color.Red /*...*/)

        Text(
            text = "Coming soon...",
            style = MaterialTheme.typography.displayLarge.copy(
                brush = Brush.linearGradient(
                    colors = gradientColors
                )
            ), fontWeight = Bold
        )
        SquigglyDivider()
        Text(
            text = "Legends, mortals, folklore, stories passed down to your grandma, how your grandpa went to school 100 miles away, why it was called Japan Riba,...uh! and many more...",
            style = MaterialTheme.typography.displaySmall.copy(
                brush = Brush.linearGradient(
                    colors = gradientColors2
                )
            )
        )
    }
}



@Preview
@Composable
fun Preview() {
    StoryView()
}