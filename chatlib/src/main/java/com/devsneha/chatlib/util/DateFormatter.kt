package com.devsneha.chatlib.util

import java.text.SimpleDateFormat
import java.util.*

object DateFormatter {
    private val timeFormat = SimpleDateFormat("h:mm a", Locale.getDefault())
    private val dateFormat = SimpleDateFormat("MMM d, yyyy", Locale.getDefault())
    private val dayFormat = SimpleDateFormat("EEEE", Locale.getDefault())
    private val todayFormat = SimpleDateFormat("'Today'", Locale.getDefault())
    private val yesterdayFormat = SimpleDateFormat("'Yesterday'", Locale.getDefault())
    
    fun formatMessageTime(timestamp: Long): String {
        val date = Date(timestamp)
        val calendar = Calendar.getInstance()
        val messageCalendar = Calendar.getInstance().apply { time = date }
        
        return when {
            isToday(messageCalendar, calendar) -> timeFormat.format(date)
            isYesterday(messageCalendar, calendar) -> "Yesterday ${timeFormat.format(date)}"
            isThisWeek(messageCalendar, calendar) -> "${dayFormat.format(date)} ${timeFormat.format(date)}"
            else -> "${dateFormat.format(date)} ${timeFormat.format(date)}"
        }
    }
    
    fun formatSeparatorDate(timestamp: Long): String {
        val date = Date(timestamp)
        val calendar = Calendar.getInstance()
        val messageCalendar = Calendar.getInstance().apply { time = date }
        
        return when {
            isToday(messageCalendar, calendar) -> "Today"
            isYesterday(messageCalendar, calendar) -> "Yesterday"
            isThisWeek(messageCalendar, calendar) -> dayFormat.format(date)
            else -> dateFormat.format(date)
        }
    }
    
    fun formatTimeOnly(timestamp: Long): String {
        return timeFormat.format(Date(timestamp))
    }
    
    private fun isToday(messageCal: Calendar, todayCal: Calendar): Boolean {
        return messageCal.get(Calendar.YEAR) == todayCal.get(Calendar.YEAR) &&
                messageCal.get(Calendar.DAY_OF_YEAR) == todayCal.get(Calendar.DAY_OF_YEAR)
    }
    
    private fun isYesterday(messageCal: Calendar, todayCal: Calendar): Boolean {
        val yesterday = Calendar.getInstance().apply {
            time = todayCal.time
            add(Calendar.DAY_OF_YEAR, -1)
        }
        return messageCal.get(Calendar.YEAR) == yesterday.get(Calendar.YEAR) &&
                messageCal.get(Calendar.DAY_OF_YEAR) == yesterday.get(Calendar.DAY_OF_YEAR)
    }
    
    private fun isThisWeek(messageCal: Calendar, todayCal: Calendar): Boolean {
        val weekAgo = Calendar.getInstance().apply {
            time = todayCal.time
            add(Calendar.DAY_OF_YEAR, -7)
        }
        return messageCal.after(weekAgo) && !isToday(messageCal, todayCal) && !isYesterday(messageCal, todayCal)
    }
}