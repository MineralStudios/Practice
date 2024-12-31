package gg.mineral.practice.commands.config;

import gg.mineral.practice.category.Category;
import gg.mineral.practice.commands.PlayerCommand;
import gg.mineral.practice.gametype.Gametype;
import gg.mineral.practice.managers.CategoryManager;
import gg.mineral.practice.managers.GametypeManager;
import gg.mineral.practice.managers.QueuetypeManager;
import gg.mineral.practice.queue.Queuetype;
import gg.mineral.practice.util.messages.CC;
import gg.mineral.practice.util.messages.impl.ChatMessages;
import gg.mineral.practice.util.messages.impl.ErrorMessages;
import gg.mineral.practice.util.messages.impl.UsageMessages;
import lombok.val;
import org.bukkit.entity.Player;

import java.util.Locale;

public class CategoryCommand extends PlayerCommand {

    public CategoryCommand() {
        super("category", "practice.config");
    }

    @Override
    public void execute(Player player, String[] args) {

        Category category;
        Gametype gametype;
        String categoryName, gametypeName;
        StringBuilder sb;

        switch (args.length > 0 ? args[0].toLowerCase(Locale.ROOT) : "") {
            default:
                ChatMessages.CATEGORY_COMMANDS.send(player);
                ChatMessages.CATEGORY_CREATE.send(player);
                ChatMessages.CATEGORY_DISPLAY.send(player);
                ChatMessages.CATEGORY_QUEUE.send(player);
                ChatMessages.CATEGORY_LIST.send(player);
                ChatMessages.CATEGORY_ADD.send(player);
                ChatMessages.CATEGORY_REMOVE.send(player);
                ChatMessages.CATEGORY_DELETE.send(player);
                return;
            case "create":
                if (args.length < 2) {
                    UsageMessages.CATEGORY_CREATE.send(player);
                    return;
                }

                categoryName = args[1];

                if (CategoryManager.getCategoryByName(categoryName) != null) {
                    ErrorMessages.ARENA_ALREADY_EXISTS.send(player);
                    return;
                }

                category = new Category(categoryName);
                category.setDefaults();
                CategoryManager.registerCategory(category);
                ChatMessages.CATEGORY_CREATED.clone().replace("%category%", categoryName).send(player);
                return;
            case "setdisplay":
                if (args.length < 2) {
                    UsageMessages.CATEGORY_DISPLAY.send(player);
                    return;
                }

                categoryName = args[1];
                category = CategoryManager.getCategoryByName(categoryName);

                if (category == null) {
                    ErrorMessages.CATEGORY_DOES_NOT_EXIST.send(player);
                    return;
                }

                category.setDisplayItem(player.getItemInHand());

                if (args.length > 2)
                    category.setDisplayName(args[2].replace("&", "§"));

                ChatMessages.CATEGORY_DISPLAY_SET.clone().replace("%category%", categoryName).send(player);

                return;
            case "queue":
                if (args.length < 4) {
                    UsageMessages.CATEGORY_QUEUE.send(player);
                    return;
                }

                categoryName = args[1];
                category = CategoryManager.getCategoryByName(categoryName);

                Queuetype queuetype = QueuetypeManager.getQueuetypeByName(args[2]);

                if (category == null) {
                    ErrorMessages.CATEGORY_DOES_NOT_EXIST.send(player);
                    return;
                }

                if (queuetype == null) {
                    ErrorMessages.QUEUETYPE_DOES_NOT_EXIST.send(player);
                    return;
                }

                val slotName = args[3];

                if (slotName.equalsIgnoreCase("false")) {
                    category.removeFromQueuetype(queuetype);
                } else {
                    int slot;

                    try {
                        slot = Integer.parseInt(slotName);
                    } catch (Exception e) {
                        ErrorMessages.INVALID_SLOT.send(player);
                        return;
                    }

                    category.addToQueuetype(queuetype, slot);
                }

                ChatMessages.CATEGORY_SLOT.clone().replace("%category%", categoryName).replace("%slot%",
                        slotName).send(player);

                return;
            case "add":
                if (args.length < 3) {
                    UsageMessages.CATEGORY_ADD.send(player);
                    return;
                }

                categoryName = args[1];
                category = CategoryManager.getCategoryByName(categoryName);

                if (category == null) {
                    ErrorMessages.CATEGORY_DOES_NOT_EXIST.send(player);
                    return;
                }

                gametypeName = args[2];
                gametype = GametypeManager.getGametypeByName(gametypeName);

                if (gametype == null) {
                    ErrorMessages.GAMETYPE_DOES_NOT_EXIST.send(player);
                    return;
                }

                gametype.addToCategory(category);
                ChatMessages.CATEGORY_ADDED.clone().replace("%gametype%", gametypeName)
                        .replace("%category%", categoryName).send(player);

                return;
            case "remove":
                if (args.length < 3) {
                    UsageMessages.CATEGORY_REMOVE.send(player);
                    return;
                }

                categoryName = args[1];
                category = CategoryManager.getCategoryByName(categoryName);

                if (category == null) {
                    ErrorMessages.CATEGORY_DOES_NOT_EXIST.send(player);
                    return;
                }

                gametypeName = args[2];
                gametype = GametypeManager.getGametypeByName(gametypeName);

                if (gametype == null) {
                    ErrorMessages.GAMETYPE_DOES_NOT_EXIST.send(player);
                    return;
                }

                gametype.removeFromCategory(category);
                ChatMessages.CATEGORY_REMOVED.clone().replace("%gametype%", gametypeName)
                        .replace("%category%", categoryName).send(player);

                return;
            case "list":
                sb = new StringBuilder(CC.GRAY + "[");

                val categoryIter = CategoryManager.getCategories().iterator();

                while (categoryIter.hasNext()) {
                    val c = categoryIter.next();
                    sb.append(CC.GREEN + c.getName());

                    if (categoryIter.hasNext())
                        sb.append(CC.GRAY + ", ");
                }

                sb.append(CC.GRAY + "]");

                player.sendMessage(sb.toString());

                return;
            case "delete":
                if (args.length < 2) {
                    UsageMessages.CATEGORY_DELETE.send(player);
                    return;
                }

                categoryName = args[1];
                category = CategoryManager.getCategoryByName(categoryName);

                if (category == null) {
                    ErrorMessages.CATEGORY_DOES_NOT_EXIST.send(player);
                    return;
                }

                CategoryManager.remove(category);
                ChatMessages.CATEGORY_DELETED.clone().replace("%category%", categoryName).send(player);

        }
    }
}
