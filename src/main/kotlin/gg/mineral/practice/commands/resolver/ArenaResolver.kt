package gg.mineral.practice.commands.resolver

import dev.rollczi.litecommands.argument.Argument
import dev.rollczi.litecommands.argument.parser.ParseResult
import dev.rollczi.litecommands.argument.resolver.ArgumentResolver
import dev.rollczi.litecommands.invocation.Invocation
import dev.rollczi.litecommands.suggestion.SuggestionContext
import dev.rollczi.litecommands.suggestion.SuggestionResult
import gg.mineral.practice.arena.Arena
import gg.mineral.practice.managers.ArenaManager
import gg.mineral.practice.util.messages.impl.ErrorMessages
import org.bukkit.command.CommandSender


class ArenaResolver : ArgumentResolver<CommandSender, Arena>() {
    override fun parse(
        invocation: Invocation<CommandSender>,
        argument: Argument<Arena>,
        string: String
    ): ParseResult<Arena> {
        val arena = ArenaManager.getArenaByName(string)
            ?: return ParseResult.failure(ErrorMessages.ARENA_DOES_NOT_EXIST.toString())

        return ParseResult.success(arena)
    }

    override fun suggest(
        invocation: Invocation<CommandSender>,
        argument: Argument<Arena>,
        context: SuggestionContext
    ): SuggestionResult = ArenaManager.arenas.values.stream()
        .map { it.name }.collect(SuggestionResult.collector())
}