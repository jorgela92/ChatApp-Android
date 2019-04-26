package com.example.jorgelapenaanton.chatapp.filesuport

import android.annotation.SuppressLint
import android.graphics.Bitmap
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class ChatUtility {
    companion object {
        fun trimUser(user: String?) : String? {
            return user?.substringBeforeLast("@")
        }

        fun resize(image: Bitmap): Bitmap{
            var width: Int = image.width
            var height: Int = image.height
            val sizeMax = 250
            val bitmapRatio: Float = (width / height).toFloat()
            if (bitmapRatio > 1){
                width = sizeMax
                height = (width / bitmapRatio).toInt()
            } else {
                height = sizeMax
                width = (height * bitmapRatio).toInt()
            }
            return Bitmap.createScaledBitmap(image, width, height, true)
        }

        @SuppressLint("SimpleDateFormat")
        fun getDateTime(s: String): String? {
            return try {
                val formatDate = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
                formatDate.timeZone = TimeZone.getTimeZone("GMT")
                val formatHour = SimpleDateFormat("HH:mm")
                formatHour.timeZone = TimeZone.getTimeZone("GMT")
                formatHour.format(formatDate.parse(s))
            } catch (_: ParseException) {
                val formatHour = SimpleDateFormat("HH:mm")
                formatHour.timeZone = TimeZone.getTimeZone("GMT")
                formatHour.format(Date())
            }
        }

        fun randomStringNameImage() : String {
            val source = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"
            return (1..19)
                .map { source.random() }
                .joinToString("")
        }
    }
}