package open.sesame.wordbook.ui.page.inappmail

import android.content.Context
import kotlinx.serialization.json.Json
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/** Helper functions to copy, read, and write JSON file */

fun mergeMailItems(
    assetItems: List<MailItem>,
    storedItems: List<MailItem>
): List<MailItem> {
    val storedMap = storedItems.associateBy { it.id }
    val dateFormat = SimpleDateFormat("yyyy-MMM-dd", Locale.getDefault())
    val resultMap = storedMap.toMutableMap()

    for (assetItem in assetItems) {
        val storedItem = storedMap[assetItem.id]

        if (storedItem != null) {
            val contentChanged =
                assetItem.subject != storedItem.subject || assetItem.content != storedItem.content

            if (contentChanged) {
                val updatedItem = assetItem.copy(
                    read = storedItem.read,
                    date = storedItem.date.ifBlank {
                        assetItem.date.ifBlank { dateFormat.format(Date()) }
                    },
                    timestamp = storedItem.timestamp
                )
                resultMap[assetItem.id] = updatedItem
            }
            // else: keep storedItem as-is
        } else {
            val newItem = assetItem.copy(
                date = assetItem.date.ifBlank { dateFormat.format(Date()) },
                timestamp = assetItem.timestamp ?: System.currentTimeMillis()
            )
            resultMap[assetItem.id] = newItem
        }
    }

    return resultMap.values.toList()
}

fun copyJsonFileToStorage(context: Context, assetFileName: String, destFileName: String) {
    val file = File(context.filesDir, destFileName)

    // Read asset items
    val assetItems: List<MailItem> =
        context.assets.open(assetFileName).bufferedReader().use { reader ->
            Json.decodeFromString(reader.readText())
        }

    // Read stored items if exist
    val storedItems =
        if (file.exists()) readMailItemsFromFile(context, destFileName) else emptyList()

    // Merge items
    val mergedItems = mergeMailItems(assetItems, storedItems)

    // Always write merged items to file
    writeMailItemsToFile(context, destFileName, mergedItems)
}


fun readMailItemsFromFile(ctx: Context, fileName: String): List<MailItem> {
    val file = File(ctx.filesDir, fileName)
    return try {
        if (!file.exists()) return emptyList()
        val jsonString = file.readText()
        Json.decodeFromString(jsonString)
    } catch (e: Exception) {
        e.printStackTrace()
        emptyList()
    }
}

fun writeMailItemsToFile(ctx: Context, fileName: String, items: List<MailItem>) {
    val file = File(ctx.filesDir, fileName)
    val jsonString = Json.encodeToString(items)
    file.writeText(jsonString)
}