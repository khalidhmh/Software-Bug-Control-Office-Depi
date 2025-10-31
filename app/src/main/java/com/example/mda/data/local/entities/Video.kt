package com.example.mda.data.local.entities

/**
 * نموذج بيانات الفيديو (Trailer/Teaser)
 * يستخدم في عرض التريلرات والفيديوهات الخاصة بالفيلم
 */
data class Video(
    val key: String,        // YouTube video key
    val name: String,       // اسم الفيديو
    val site: String,       // "YouTube" or other
    val type: String        // "Trailer", "Teaser", "Clip", etc.
) {
    /**
     * الحصول على رابط YouTube
     */
    fun getYouTubeUrl(): String {
        return "https://www.youtube.com/watch?v=$key"
    }
    
    /**
     * الحصول على رابط thumbnail من YouTube
     */
    fun getThumbnailUrl(): String {
        return "https://img.youtube.com/vi/$key/hqdefault.jpg"
    }
    
    /**
     * التحقق من أن الفيديو من YouTube
     */
    fun isYouTube(): Boolean {
        return site.equals("YouTube", ignoreCase = true)
    }
    
    /**
     * التحقق من أن الفيديو هو Trailer
     */
    fun isTrailer(): Boolean {
        return type.equals("Trailer", ignoreCase = true)
    }
}
