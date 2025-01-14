package gg.mineral.practice.util.messages

import org.bukkit.ChatColor
import java.util.*

object StringUtil {
    fun toNiceString(string: String): String {
        val newString = ChatColor.stripColor(string).replace('_', ' ').lowercase(Locale.getDefault())
        val sb = StringBuilder()
        for (i in newString.toCharArray().indices) {
            var c = newString.toCharArray()[i]
            if (i > 0) {
                val prev = newString.toCharArray()[i - 1]
                if ((prev == ' ' || prev == '[' || prev == '(') && (i == newString.toCharArray().size - 1 || c != 'x' || !Character.isDigit(
                        newString.toCharArray()[i + 1]
                    ))
                ) c = c.uppercaseChar()
            } else if (c != 'x' || !Character.isDigit(newString.toCharArray()[i + 1])) c = c.uppercaseChar()

            sb.append(c)
        }
        return sb.toString()
    }
}
