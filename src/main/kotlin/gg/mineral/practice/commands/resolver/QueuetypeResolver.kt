package gg.mineral.practice.commands.resolver

import dev.rollczi.litecommands.argument.Argument
import dev.rollczi.litecommands.argument.parser.ParseResult
import dev.rollczi.litecommands.argument.resolver.ArgumentResolver
import dev.rollczi.litecommands.invocation.Invocation
import dev.rollczi.litecommands.suggestion.SuggestionContext
import dev.rollczi.litecommands.suggestion.SuggestionResult
import gg.mineral.practice.managers.QueuetypeManager
import gg.mineral.practice.queue.Queuetype
import gg.mineral.practice.util.messages.impl.ErrorMessages
import org.bukkit.command.CommandSender
import java.util.*

class QueuetypeResolver : ArgumentResolver<CommandSender, Queuetype>() {
    override fun parse(
        invocation: Invocation<CommandSender>,
        argument: Argument<Queuetype>,
        string: String
    ): ParseResult<Queuetype> {
        val queuetype = QueuetypeManager.getQueuetypeByName(string)
            ?: return ParseResult.failure(ErrorMessages.QUEUETYPE_DOES_NOT_EXIST.toString())

        return ParseResult.success(queuetype)
    }

    override fun suggest(
        invocation: Invocation<CommandSender>,
        argument: Argument<Queuetype>,
        context: SuggestionContext
    ): SuggestionResult = QueuetypeManager.queuetypes.values.stream().filter(Objects::nonNull)
        .map { it!!.name }.collect(SuggestionResult.collector())
}