package com.example.mda.ui.actordetails

import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import java.time.LocalDate
import java.time.Period
import java.time.format.DateTimeFormatter

// فتح رابط خارجي
@Composable
fun openUrl(url: String) {
    val context = LocalContext.current
    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
    context.startActivity(intent)
}

// حساب العمر من تاريخ الميلاد
@RequiresApi(Build.VERSION_CODES.O)
fun calculateAge(birthday: String?): Int? {
    return try {
        if (birthday.isNullOrEmpty()) return null
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val birthDate = LocalDate.parse(birthday, formatter)
        Period.between(birthDate, LocalDate.now()).years
    } catch (e: Exception) {
        null
    }
}
