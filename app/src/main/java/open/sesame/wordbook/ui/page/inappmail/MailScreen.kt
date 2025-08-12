package open.sesame.wordbook.ui.page.inappmail

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MailOutline
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import kotlinx.coroutines.delay
import open.sesame.wordbook.R.drawable
import open.sesame.wordbook.R.string
import open.sesame.wordbook.data.dummy.ListCardHeader
import open.sesame.wordbook.data.dummy.ProfilePic
import java.util.concurrent.TimeUnit

/** Mainly for showing in-app mail updates */

@Composable
fun MailScreenViewDialog(
    mailViewModel: MailViewModel, onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss, properties = DialogProperties(
            decorFitsSystemWindows = false, usePlatformDefaultWidth = false
        )
    ) {
        MailScreenView(
            mailViewModel = mailViewModel
        )
    }
}

// Useful timestamp converter
fun formatTimeAgo(timestampMillis: Long): String {
    val now = System.currentTimeMillis()
    val diff = now - timestampMillis

    val seconds = TimeUnit.MILLISECONDS.toSeconds(diff)
    val minutes = TimeUnit.MILLISECONDS.toMinutes(diff)
    val hours = TimeUnit.MILLISECONDS.toHours(diff)
    val days = TimeUnit.MILLISECONDS.toDays(diff)

    return when {
        seconds < 60 -> "$seconds seconds ago"
        minutes < 60 -> {
//            val sec = seconds % 60
            "$minutes minutes ago" // $sec seconds ago"
        }

        hours < 24 -> {
            val min = minutes % 60
//            val sec = seconds % 60
            "$hours hours $min minutes ago" // $sec seconds ago"
        }

        else -> "$days days ago"
    }
}


@Composable
fun MailScreenView(
    mailViewModel: MailViewModel
) {
    val ctx = LocalContext.current
    val mailItems = mailViewModel.mailItem
    // dialog passthrough
    var selectedMail by remember { mutableStateOf<MailItem?>(null) }
    // some tweaks i swear!
    var refreshReadLabel by remember { mutableIntStateOf(0) }

    // to pass unread items to mail icon as red dot
//    val hasUnread = mailItems.any { !it.read }
    // viewmodel mode
    fun markAsRead(index: Int) {
        mailViewModel.markAsRead(index)
        mailViewModel.saveJsonMails(ctx, "mail.json")
    }
    //Direct passthrough mode
    /*    fun markAsRead(index: Int) {
            if (!mailItems[index].read) {
                mailItems = mailItems.toMutableList().also {
                    it[index] = it[index].copy(read = true)
                }
            }
        }*/


    // top app bar
    Column(modifier = Modifier
//        .padding(start = 8.dp, end = 8.dp)
        .background(MaterialTheme.colorScheme.inversePrimary)) {
        MailboxToolbar(
        )


        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 8.dp, end = 8.dp),
        ) {
            itemsIndexed(mailItems) { index, mail ->
// minor click state adjustment
                var delayReadLabel by remember { mutableStateOf("[ Unread ]") }
                val readLabelTextColor = if (mail.read) Color.Black else Color.White
                var readLabelBgColor by remember { mutableStateOf(Color.Red) }

                LaunchedEffect(refreshReadLabel, mail.read) {
                    if (mail.read) {
                        delay(100) // Delay ms,500
                        delayReadLabel = if (mail.read) " Read ✓✓ " else delayReadLabel
                        readLabelBgColor = Color.Green
                    } else {
                        delayReadLabel
                        readLabelBgColor = Color.Red
                    }
                }

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .clickable {
                            markAsRead(index)
                            selectedMail = mail // open dialog
                        },
                    colors = if (mail.read) CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    else CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(
                                20.dp
                            )
                    ) {
                        ListCardHeader(
                            drawableRes = drawable.admin_b,
                            sender = mail.sender,
                            timestamp = mail.timestamp?.let { formatTimeAgo(it) } ?: "Unknown",
                            onClick = { TODO() },
                            isRead = {Text(text =  delayReadLabel,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = readLabelTextColor,
                                ),
                                     modifier = Modifier
                                         .background(readLabelBgColor))},
                            mailNumber = (mail.id.toString()),
                            mNuberBgStyle = MaterialTheme.colorScheme.primary,

                        )
                        Text(
                            mail.subject,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier
                        )
                        Text(
                            mail.content,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.outline
                        )
                    }

                }

            }
        }

    }
    selectedMail?.let { mailItem ->
        MailDetailReadViewDialog(
            mail = mailItem,
            timestamp = mailItem.timestamp?.let { formatTimeAgo(it) },
            onDismiss = {
                selectedMail = null
                refreshReadLabel++  // force recomposition for read label
            })
    }
}


@Composable
private fun MailboxToolbar() {
    val ctx = LocalContext.current
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
//            .padding(horizontal = 8.dp, vertical = 54.dp) // disabled for fullscreen aesthetic purpose
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.tertiaryContainer)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(0.5f), verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Default.MailOutline,
                modifier = Modifier.padding(start = 4.dp, top = 42.dp),
                contentDescription = ""
            )
            Text(
                text = stringResource(string.mailbox).uppercase(),
                modifier = Modifier.padding(start = 4.dp, top = 42.dp),
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 8.sp,
                )
            )
        }

        ProfilePic(
            drawableRes = drawable.admin_a,
            modifier = Modifier.padding(end = 16.dp, top = 54.dp, bottom = 24.dp),
            onClick = { Toast.makeText(ctx, "You alerted the android ninja get ready", Toast.LENGTH_SHORT).show() })
    }
}



