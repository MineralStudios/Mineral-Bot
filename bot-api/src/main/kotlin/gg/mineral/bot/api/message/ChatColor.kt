package gg.mineral.bot.api.message

object ChatColor {
    // ANSI escape codes
    const val RESET: String = "\u001b[0m" // Text Reset
    const val STRIKETHROUGH: String = "\u001b[9m" // STRIKETHROUGH
    const val UNDERLINE: String = "\u001b[4m" // UNDERLINE

    // Regular Colors
    const val GRAY: String = "\u001b[1;30m" // GRAY
    const val BLACK: String = "\u001b[0;30m" // BLACK
    const val RED: String = "\u001b[0;31m" // RED
    const val GREEN: String = "\u001b[0;32m" // GREEN
    const val YELLOW: String = "\u001b[0;33m" // YELLOW
    const val BLUE: String = "\u001b[0;34m" // BLUE
    const val PURPLE: String = "\u001b[0;35m" // PURPLE
    const val CYAN: String = "\u001b[0;36m" // CYAN
    const val WHITE: String = "\u001b[0;37m" // WHITE
}
