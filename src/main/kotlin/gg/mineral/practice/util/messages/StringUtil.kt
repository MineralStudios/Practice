package gg.mineral.practice.util.messages

import org.bukkit.ChatColor
import java.util.*

object StringUtil {
    fun toNiceString(string: String): String {
        val newString = ChatColor.stripColor(string)
            .replace('_', ' ')
            .lowercase(Locale.getDefault())
        val chars = newString.toCharArray()
        val length = chars.size
        val sb = StringBuilder()

        for (i in 0 until length) {
            var c = chars[i]
            if (i > 0) {
                val prev = chars[i - 1]
                val spaceOrBracket = prev in setOf(' ', '[', '(')
                if (spaceOrBracket) {
                    val isLast = i == length - 1
                    val nextIsDigit = if (i < length - 1) Character.isDigit(chars[i + 1]) else false
                    if (isLast || c != 'x' || !nextIsDigit) {
                        c = c.uppercaseChar()
                    }
                }
            } else {
                val isX = c == 'x'
                val nextIsDigit = if (length - 1 > 0) Character.isDigit(chars[1]) else false
                if (!isX || !nextIsDigit) c = c.uppercaseChar()
            }

            sb.append(c)
        }

        return sb.toString()
    }


}
