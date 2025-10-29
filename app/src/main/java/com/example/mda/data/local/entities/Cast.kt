package com.example.mda.data.local.entities

/**
 * نموذج بيانات الممثل (Cast Member)
 * يستخدم في عرض طاقم التمثيل في صفحة تفاصيل الفيلم
 */
data class Cast(
    val id: Int,
    val name: String,
    val character: String,
    val profilePath: String?
) {
    /**
     * الحصول على رابط الصورة الكاملة
     */
    fun getProfileImageUrl(): String? {
        return profilePath?.let { "https://image.tmdb.org/t/p/w185$it" }
    }
    
    /**
     * الحصول على الأحرف الأولى من الاسم (للـ placeholder)
     */
    fun getInitials(): String {
        return name.split(" ")
            .take(2)
            .joinToString("") { it.firstOrNull()?.uppercase() ?: "" }
    }
}
