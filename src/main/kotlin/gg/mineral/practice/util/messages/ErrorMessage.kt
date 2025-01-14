package gg.mineral.practice.util.messages

class ErrorMessage(s: String) : Message(s) {
    init {
        messageBuilder.insert(0, CC.D_RED + "✖ Error ✖ " + CC.RED)
    }

    override fun clone() = ErrorMessage(messageBuilder.toString())
}
