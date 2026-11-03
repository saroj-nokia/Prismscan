package com.example.ui.result

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.CalendarContract
import android.provider.ContactsContract
import android.provider.Settings
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Sms
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SplitButtonLayout
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.model.BarcodeType
import com.example.domain.model.ParsedBarcode
import com.example.ui.theme.SafetyDangerColor
import com.example.ui.theme.SafetySecureColor
import com.example.ui.theme.SafetyWarningColor
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ResultScreen(
    barcode: ParsedBarcode,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    BackHandler {
        onNavigateBack()
    }

    Scaffold(
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier
                            .testTag("back_button")
                            .size(48.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back to camera"
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Scan Details",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        },
        bottomBar = {
            // Persistent bottom primary actions
            Surface(
                tonalElevation = 6.dp,
                shadowElevation = 8.dp,
                color = MaterialTheme.colorScheme.surface
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 14.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = { copyToClipboard(context, barcode.rawValue) },
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("copy_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.ContentCopy,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Copy")
                    }

                    OutlinedButton(
                        onClick = { shareContent(context, barcode.rawValue) },
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("share_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Share")
                    }
                }
            }
        },
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(scrollState)
                .padding(horizontal = 20.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Header Badge: Format & Type
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = getTypeIcon(barcode.type),
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = barcode.type.label,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                }

                Text(
                    text = barcode.formatName,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // URL SAFETY SECTION (Mandatory Explicit Check)
            if (barcode.type == BarcodeType.URL) {
                UrlSafetyInspectionCard(
                    barcode = barcode,
                    onOpenUrl = {
                        val safeUrl = barcode.url ?: barcode.rawValue
                        val finalUrl = if (!safeUrl.startsWith("http://") && !safeUrl.startsWith("https://")) {
                            "https://$safeUrl"
                        } else {
                            safeUrl
                        }
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(finalUrl))
                        context.startActivity(intent)
                    },
                    onCopyUrl = {
                        copyToClipboard(context, barcode.url ?: barcode.rawValue)
                    }
                )
            } else {
                // Type-Specific Content Cards for Wi-Fi, Contact, Phone, etc.
                TypeSpecificContentCard(
                    barcode = barcode,
                    onExecutePrimary = { executeTypeAction(context, barcode) }
                )
            }

            // Raw Value Inspection Card
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(18.dp)
                ) {
                    Text(
                        text = "RAW PAYLOAD",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = barcode.rawValue,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 18.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Scanned on ${SimpleDateFormat("MMM d, yyyy • h:mm a", Locale.getDefault()).format(Date(barcode.timestamp))}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.outline
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun UrlSafetyInspectionCard(
    barcode: ParsedBarcode,
    onOpenUrl: () -> Unit,
    onCopyUrl: () -> Unit,
    modifier: Modifier = Modifier
) {
    val safety = barcode.safetyInfo

    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Safety Status Indicator
            val statusColor = when {
                safety.isDangerous -> SafetyDangerColor
                !safety.isHttps -> SafetyWarningColor
                else -> SafetySecureColor
            }
            val statusText = when {
                safety.isDangerous -> "Suspicious or Potentially Dangerous Link"
                !safety.isHttps -> "Insecure Connection (HTTP)"
                else -> "Encrypted Connection (HTTPS)"
            }
            val statusIcon = when {
                safety.isDangerous -> Icons.Default.Warning
                !safety.isHttps -> Icons.Default.LockOpen
                else -> Icons.Default.Lock
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(statusColor.copy(alpha = 0.12f))
                    .padding(horizontal = 14.dp, vertical = 10.dp)
            ) {
                Icon(
                    imageVector = statusIcon,
                    contentDescription = null,
                    tint = statusColor,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = statusText,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = statusColor
                )
            }

            // Warning details if any
            if (safety.warnings.isNotEmpty()) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(SafetyWarningColor.copy(alpha = 0.08f))
                        .padding(14.dp)
                ) {
                    safety.warnings.forEach { warning ->
                        Row(verticalAlignment = Alignment.Top) {
                            Text("• ", color = SafetyWarningColor, fontWeight = FontWeight.Bold)
                            Text(
                                text = warning,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }

            // Prominent Domain Highlight (To prevent phishing disguises)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .background(MaterialTheme.colorScheme.surface)
                    .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(18.dp))
                    .padding(16.dp)
            ) {
                Text(
                    text = "DESTINATION DOMAIN",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = safety.domain.ifEmpty { "External Link" },
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                if (safety.pathAndQuery.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = safety.pathAndQuery,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Text(
                text = "Safety Note: PrismScan never auto-opens web links. Review the destination carefully before tapping open.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            // Expressive SplitButton Layout for Open with Copy alternative
            SplitButtonLayout(
                leadingButton = {
                    Button(
                        onClick = onOpenUrl,
                        shape = RoundedCornerShape(topStart = 16.dp, bottomStart = 16.dp, topEnd = 4.dp, bottomEnd = 4.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (safety.isDangerous) SafetyDangerColor else MaterialTheme.colorScheme.primary
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("open_link_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.OpenInNew,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (safety.isDangerous) "Open Anyway" else "Open Link",
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                },
                trailingButton = {
                    FilledTonalButton(
                        onClick = onCopyUrl,
                        shape = RoundedCornerShape(topStart = 4.dp, bottomStart = 4.dp, topEnd = 16.dp, bottomEnd = 16.dp),
                        modifier = Modifier.testTag("copy_link_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.ContentCopy,
                            contentDescription = "Copy Link",
                            modifier = Modifier.size(18.dp)
                        )
                    }
                },
                spacing = 4.dp,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun TypeSpecificContentCard(
    barcode: ParsedBarcode,
    onExecutePrimary: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(22.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            when (barcode.type) {
                BarcodeType.WIFI -> {
                    Text(
                        text = "Wi-Fi Network",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    DetailRow(label = "Network Name (SSID)", value = barcode.wifiSsid ?: "Unknown")
                    DetailRow(label = "Security Encryption", value = barcode.wifiType ?: "WPA")
                    barcode.wifiPassword?.let {
                        DetailRow(label = "Password", value = it, isMonospace = true)
                    }
                    Button(
                        onClick = onExecutePrimary,
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("primary_action_button")
                    ) {
                        Icon(imageVector = Icons.Default.Wifi, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Connect / Open Wi-Fi Settings")
                    }
                }

                BarcodeType.CONTACT -> {
                    Text(
                        text = "Contact Information",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    DetailRow(label = "Name", value = barcode.contactName ?: barcode.displayValue)
                    barcode.contactPhone?.let { DetailRow(label = "Phone", value = it) }
                    barcode.contactEmail?.let { DetailRow(label = "Email", value = it) }
                    barcode.contactOrg?.let { DetailRow(label = "Organization", value = it) }
                    Button(
                        onClick = onExecutePrimary,
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("primary_action_button")
                    ) {
                        Icon(imageVector = Icons.Default.Person, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Add to Contacts")
                    }
                }

                BarcodeType.PHONE -> {
                    Text(
                        text = "Phone Number",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    DetailRow(label = "Number", value = barcode.phoneNumber ?: barcode.displayValue, isMonospace = true)
                    Button(
                        onClick = onExecutePrimary,
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("primary_action_button")
                    ) {
                        Icon(imageVector = Icons.Default.Phone, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Dial Number")
                    }
                }

                BarcodeType.SMS -> {
                    Text(
                        text = "SMS Message",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    DetailRow(label = "Recipient", value = barcode.smsNumber ?: barcode.displayValue)
                    barcode.smsMessage?.let { DetailRow(label = "Message", value = it) }
                    Button(
                        onClick = onExecutePrimary,
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("primary_action_button")
                    ) {
                        Icon(imageVector = Icons.Default.Sms, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Send SMS")
                    }
                }

                BarcodeType.EMAIL -> {
                    Text(
                        text = "Email Message",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    DetailRow(label = "To", value = barcode.emailAddress ?: barcode.displayValue)
                    barcode.emailSubject?.let { DetailRow(label = "Subject", value = it) }
                    barcode.emailBody?.let { DetailRow(label = "Body", value = it) }
                    Button(
                        onClick = onExecutePrimary,
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("primary_action_button")
                    ) {
                        Icon(imageVector = Icons.Default.Email, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Send Email")
                    }
                }

                BarcodeType.GEO -> {
                    Text(
                        text = "Geographic Coordinates",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    DetailRow(label = "Latitude", value = "${barcode.geoLat ?: 0.0}")
                    DetailRow(label = "Longitude", value = "${barcode.geoLng ?: 0.0}")
                    Button(
                        onClick = onExecutePrimary,
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("primary_action_button")
                    ) {
                        Icon(imageVector = Icons.Default.LocationOn, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Navigate on Map")
                    }
                }

                BarcodeType.CALENDAR -> {
                    Text(
                        text = "Calendar Event",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    DetailRow(label = "Event", value = barcode.calendarTitle ?: barcode.displayValue)
                    barcode.calendarDescription?.let { DetailRow(label = "Description", value = it) }
                    barcode.calendarLocation?.let { DetailRow(label = "Location", value = it) }
                    Button(
                        onClick = onExecutePrimary,
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("primary_action_button")
                    ) {
                        Icon(imageVector = Icons.Default.CalendarMonth, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Add to Calendar")
                    }
                }

                BarcodeType.PRODUCT -> {
                    Text(
                        text = "Product Barcode",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    DetailRow(label = "Barcode Number", value = barcode.rawValue, isMonospace = true)
                    Button(
                        onClick = onExecutePrimary,
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("primary_action_button")
                    ) {
                        Icon(imageVector = Icons.Default.Search, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Search Product on Google")
                    }
                }

                BarcodeType.TEXT -> {
                    Text(
                        text = "Text Content",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = barcode.displayValue,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Button(
                        onClick = onExecutePrimary,
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("primary_action_button")
                    ) {
                        Icon(imageVector = Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Copy Text")
                    }
                }

                else -> {
                    DetailRow(label = "Content", value = barcode.displayValue)
                }
            }
        }
    }
}

@Composable
private fun DetailRow(
    label: String,
    value: String,
    isMonospace: Boolean = false
) {
    Column {
        Text(
            text = label.uppercase(),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = value,
            style = if (isMonospace) MaterialTheme.typography.bodyMedium.copy(fontFamily = FontFamily.Monospace) else MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.Medium
        )
    }
}

private fun executeTypeAction(context: Context, barcode: ParsedBarcode) {
    try {
        when (barcode.type) {
            BarcodeType.WIFI -> {
                barcode.wifiPassword?.let { copyToClipboard(context, it) }
                val intent = Intent(Settings.ACTION_WIFI_SETTINGS)
                context.startActivity(intent)
                Toast.makeText(context, "Password copied! Select network in settings.", Toast.LENGTH_LONG).show()
            }
            BarcodeType.CONTACT -> {
                val intent = Intent(Intent.ACTION_INSERT, ContactsContract.Contacts.CONTENT_URI).apply {
                    putExtra(ContactsContract.Intents.Insert.NAME, barcode.contactName)
                    putExtra(ContactsContract.Intents.Insert.PHONE, barcode.contactPhone)
                    putExtra(ContactsContract.Intents.Insert.EMAIL, barcode.contactEmail)
                    putExtra(ContactsContract.Intents.Insert.COMPANY, barcode.contactOrg)
                }
                context.startActivity(intent)
            }
            BarcodeType.PHONE -> {
                val num = barcode.phoneNumber ?: barcode.rawValue
                val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$num"))
                context.startActivity(intent)
            }
            BarcodeType.SMS -> {
                val num = barcode.smsNumber ?: barcode.rawValue
                val intent = Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:$num")).apply {
                    barcode.smsMessage?.let { putExtra("sms_body", it) }
                }
                context.startActivity(intent)
            }
            BarcodeType.EMAIL -> {
                val email = barcode.emailAddress ?: barcode.rawValue
                val intent = Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:$email")).apply {
                    barcode.emailSubject?.let { putExtra(Intent.EXTRA_SUBJECT, it) }
                    barcode.emailBody?.let { putExtra(Intent.EXTRA_TEXT, it) }
                }
                context.startActivity(intent)
            }
            BarcodeType.GEO -> {
                val uri = "geo:${barcode.geoLat},${barcode.geoLng}?q=${barcode.geoLat},${barcode.geoLng}"
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
                context.startActivity(intent)
            }
            BarcodeType.CALENDAR -> {
                val intent = Intent(Intent.ACTION_INSERT, CalendarContract.Events.CONTENT_URI).apply {
                    putExtra(CalendarContract.Events.TITLE, barcode.calendarTitle)
                    putExtra(CalendarContract.Events.DESCRIPTION, barcode.calendarDescription)
                    putExtra(CalendarContract.Events.EVENT_LOCATION, barcode.calendarLocation)
                    barcode.calendarStart?.let { putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, it) }
                    barcode.calendarEnd?.let { putExtra(CalendarContract.EXTRA_EVENT_END_TIME, it) }
                }
                context.startActivity(intent)
            }
            BarcodeType.PRODUCT -> {
                val searchUrl = "https://www.google.com/search?q=${Uri.encode(barcode.rawValue)}"
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(searchUrl))
                context.startActivity(intent)
            }
            BarcodeType.TEXT -> {
                copyToClipboard(context, barcode.displayValue)
            }
            else -> {
                copyToClipboard(context, barcode.rawValue)
            }
        }
    } catch (_: Exception) {
        copyToClipboard(context, barcode.rawValue)
    }
}

private fun copyToClipboard(context: Context, text: String) {
    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
    clipboard?.setPrimaryClip(ClipData.newPlainText("PrismScan", text))
    Toast.makeText(context, "Copied to clipboard", Toast.LENGTH_SHORT).show()
}

private fun shareContent(context: Context, text: String) {
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, text)
    }
    context.startActivity(Intent.createChooser(intent, "Share via"))
}

private fun getTypeIcon(type: BarcodeType): ImageVector {
    return when (type) {
        BarcodeType.URL -> Icons.AutoMirrored.Filled.OpenInNew
        BarcodeType.WIFI -> Icons.Default.Wifi
        BarcodeType.CONTACT -> Icons.Default.Person
        BarcodeType.EMAIL -> Icons.Default.Email
        BarcodeType.PHONE -> Icons.Default.Phone
        BarcodeType.SMS -> Icons.Default.Sms
        BarcodeType.GEO -> Icons.Default.LocationOn
        BarcodeType.CALENDAR -> Icons.Default.CalendarMonth
        BarcodeType.PRODUCT -> Icons.Default.Search
        BarcodeType.TEXT -> Icons.Default.QrCode
    }
}
