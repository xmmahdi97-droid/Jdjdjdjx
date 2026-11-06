package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ServerProfile
import com.example.data.settings.AppLanguage
import com.example.parser.ConfigParser
import com.example.qr.QrCodeGenerator
import com.example.ui.Strings

@Composable
fun QrDialog(
    server: ServerProfile,
    language: AppLanguage,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val shareLink = remember(server) { ConfigParser.toShareLink(server) }
    val qrBitmap = remember(shareLink) { QrCodeGenerator.generateQrBitmap(shareLink, 600) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = server.name,
                fontWeight = FontWeight.Bold,
                fontSize = 17.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        },
        text = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                // QR Canvas image with white border
                Box(
                    modifier = Modifier
                        .size(240.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color.White)
                        .padding(12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        bitmap = qrBitmap,
                        contentDescription = "QR Code",
                        modifier = Modifier.size(216.dp)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Short link preview
                Text(
                    text = shareLink,
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .padding(8.dp)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    clipboard.setPrimaryClip(ClipData.newPlainText("VPN Config", shareLink))
                    Toast.makeText(context, Strings.get("copied_to_clipboard", language), Toast.LENGTH_SHORT).show()
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.size(6.dp))
                Text("کپی لینک / Copy", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(Strings.get("cancel", language))
            }
        },
        containerColor = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(24.dp)
    )
}
