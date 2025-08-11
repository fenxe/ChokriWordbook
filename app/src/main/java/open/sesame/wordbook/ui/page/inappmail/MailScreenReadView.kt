package open.sesame.wordbook.ui.page.inappmail

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import open.sesame.wordbook.R.drawable
import open.sesame.wordbook.data.dummy.HorizontalLine
import open.sesame.wordbook.data.dummy.SquigglyDivider
import androidx.core.net.toUri

@Composable
fun MailDetailReadViewDialog(
    mail: MailItem,
    timestamp: String?,
    onDismiss: () -> Unit,
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            decorFitsSystemWindows = false
        )
    ) {
        MailDetailReadView(
            mail = mail,
            timestamp = timestamp,
            onDismiss = onDismiss
        )

    }
}

@Composable
fun MailDetailReadView(
    mail: MailItem,
    timestamp: String?,
    onDismiss: () -> Unit,
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colorScheme.surfaceDim)
    ) {
        Column(
            modifier = Modifier
                .padding(48.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onDismiss // onDismiss,
                    ,
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.tertiary, shape = CircleShape)
                )
                {
                    Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, contentDescription = "BACK!")
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = mail.subject,
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.tertiary
                        )
                    )
                }    // Subject
            }

        }

        Card(
            modifier = Modifier
                .padding(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface,
            )
        ) {
            val scrollState = rememberScrollState()
            Column(
                modifier = Modifier
                    .verticalScroll(scrollState)
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                ListCardHeader(
                    drawableRes = drawable.admin_a,
                    sender = mail.sender,
                    timestamp = mail.date,
                    onClick = { TODO() },
                    isRead = null,// if (mail.read) null else null,   // Pass nothing to do nothing
                    mailNumber = null
                )
                Text(
                    text = "Received: ${timestamp}\nSent by: ${mail.sender}",
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontStyle = FontStyle.Italic,
                        color = MaterialTheme.colorScheme.outline
                    )
                )
                HorizontalLine()
                Text(
                    "Subject:",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = MaterialTheme.colorScheme.outline,
                        fontWeight = FontWeight.ExtraLight
                    ),
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                )
                Text(
                    mail.subject,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                )
                SquigglyDivider(
                    modifier = Modifier
                        .fillMaxWidth(0.4f)
                )
                ContentText(mail.content, mail.sender)
            }
        }
    }
}

//
@Composable
fun ContentText(content: String, sender: String?) {
    val context = LocalContext.current
    var textLayoutResult by remember { mutableStateOf<TextLayoutResult?>(null) }

    val annotatedContent = remember(content) {
        buildAnnotatedString {
            val urlRegex = Regex("(https?://[\\w./?=&%-]+)")
            val matches = urlRegex.findAll(content)

            var lastIndex = 0
            for (match in matches) {
                val start = match.range.first
                val end = match.range.last + 1

                append(content.substring(lastIndex, start))

                pushStringAnnotation(tag = "URL", annotation = match.value)
                withStyle(style = SpanStyle(color = Color.Blue, textDecoration = TextDecoration.Underline)) {
                    append(match.value)
                }
                pop()

                lastIndex = end
            }

            if (lastIndex < content.length) {
                append(content.substring(lastIndex))
            }

            append("\n\nSigning off,\n@${sender?.lowercase()}")
        }
    }

    Text(
        text = annotatedContent,
        style = MaterialTheme.typography.bodyMedium,
        modifier = Modifier
            .pointerInput(Unit) {
                detectTapGestures { tapOffset ->
                    textLayoutResult?.let { layout ->
                        val position = layout.getOffsetForPosition(tapOffset)
                        annotatedContent.getStringAnnotations("URL", position, position)
                            .firstOrNull()?.let { annotation ->
                                val intent = Intent(Intent.ACTION_VIEW, annotation.item.toUri())
                                context.startActivity(intent)
                            }
                    }
                }
            },
        onTextLayout = { layoutResult ->
            textLayoutResult = layoutResult
        }
    )
}
