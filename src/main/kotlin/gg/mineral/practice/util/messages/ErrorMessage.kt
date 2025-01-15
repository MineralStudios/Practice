package gg.mineral.practice.util.messages

class ErrorMessage(s: String) : Message(s) {
    override fun clone() = ErrorMessage(messageBuilder.toString())

    override fun prepend(builder: StringBuilder): StringBuilder = builder.insert(0, CC.D_RED + "✖ Error ✖ " + CC.RED)
}
