package open.sesame.wordbook.ui.page.inappmail

import kotlinx.serialization.Serializable

@Serializable
data class MailItem(
    val id: Int,
    val circlePic: Int? = null,     // profile pic
    val subject: String,
    val sender: String? = "Admin",  // default fallback
    val content: String,
    var read: Boolean = false,
    var date: String = "",            // default empty string
    var timestamp: Long? = null       // nullable timestamp in epoch millis
)
