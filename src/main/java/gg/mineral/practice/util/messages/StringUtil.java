package gg.mineral.practice.util.messages;

import org.bukkit.ChatColor;

public class StringUtil {
    public static String toNiceString(String string) {
        string = ChatColor.stripColor(string).replace('_', ' ').toLowerCase();
        final StringBuilder sb = new StringBuilder();
        for (int i = 0; i < string.toCharArray().length; ++i) {
            char c = string.toCharArray()[i];
            if (i > 0) {
                final char prev = string.toCharArray()[i - 1];
                if ((prev == ' ' || prev == '[' || prev == '(') && (i == string.toCharArray().length - 1 || c != 'x'
                        || !Character.isDigit(string.toCharArray()[i + 1]))) {
                    c = Character.toUpperCase(c);
                }
            } else if (c != 'x' || !Character.isDigit(string.toCharArray()[i + 1])) {
                c = Character.toUpperCase(c);
            }
            sb.append(c);
        }
        return sb.toString();
    }
}
