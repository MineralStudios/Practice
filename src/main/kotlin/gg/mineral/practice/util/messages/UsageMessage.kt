package gg.mineral.practice.util.messages

class UsageMessage(s: String) : Message(s) {
    override fun clone() = UsageMessage(messageBuilder.toString())

    override fun prepend(builder: StringBuilder): StringBuilder =
        messageBuilder.insert(0, CC.D_RED + "Usage: " + CC.RED).append(".")
}
