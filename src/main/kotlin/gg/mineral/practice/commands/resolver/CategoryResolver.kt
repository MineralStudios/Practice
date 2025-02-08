package gg.mineral.practice.commands.resolver

import dev.rollczi.litecommands.argument.Argument
import dev.rollczi.litecommands.argument.parser.ParseResult
import dev.rollczi.litecommands.argument.resolver.ArgumentResolver
import dev.rollczi.litecommands.invocation.Invocation
import dev.rollczi.litecommands.suggestion.SuggestionContext
import dev.rollczi.litecommands.suggestion.SuggestionResult
import gg.mineral.practice.category.Category
import gg.mineral.practice.managers.CategoryManager
import gg.mineral.practice.util.messages.impl.ErrorMessages
import org.bukkit.command.CommandSender
import java.util.*

class CategoryResolver : ArgumentResolver<CommandSender, Category>() {
    override fun parse(
        invocation: Invocation<CommandSender>,
        argument: Argument<Category>,
        string: String
    ): ParseResult<Category> {
        val category = CategoryManager.getCategoryByName(string)
            ?: return ParseResult.failure(ErrorMessages.CATEGORY_DOES_NOT_EXIST.toString())

        return ParseResult.success(category)
    }

    override fun suggest(
        invocation: Invocation<CommandSender>,
        argument: Argument<Category>,
        context: SuggestionContext
    ): SuggestionResult = CategoryManager.categories.values.stream().filter(Objects::nonNull)
        .map { it!!.name }.collect(SuggestionResult.collector())
}
