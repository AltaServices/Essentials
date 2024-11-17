package dev.alta.essentials.ranks.command

import co.aikar.commands.BaseCommand
import co.aikar.commands.annotation.*
import co.aikar.commands.bukkit.contexts.OnlinePlayer
import dev.alta.essentials.ranks.manager.RankManager
import dev.alta.essentials.utils.chat.ChatUtils.sendMiniMessage
import dev.alta.essentials.utils.annotations.register.AutoRegister
import org.bukkit.command.CommandSender

@AutoRegister
@CommandAlias("rank|ranks")
@CommandPermission("essentials.command.ranks")
class RankCommand : BaseCommand() {

    @Subcommand("create")
    @CommandPermission("essentials.command.ranks.create")
    @Description("Create a new rank")
    @Syntax("<name> [weight]")
    @CommandCompletion("@nothing @range:0-100")
    fun onCreate(sender: CommandSender, name: String, @Optional weight: Int = 0) {
        RankManager.createRank(name, weight).thenAccept { success ->
            if (success) {
                sender.sendMiniMessage("<prefix> <green>Successfully created rank <white>$name</white> with weight <white>$weight</white>")
            } else {
                sender.sendMiniMessage("<prefix> <red>A rank with that name already exists!")
            }
        }
    }

    @Subcommand("delete")
    @CommandPermission("essentials.command.ranks.delete")
    @Description("Delete a rank")
    @Syntax("<name>")
    @CommandCompletion("@ranks")
    fun onDelete(sender: CommandSender, name: String) {
        RankManager.deleteRank(name).thenAccept { success ->
            if (success) {
                sender.sendMiniMessage("<prefix> <green>Successfully deleted rank <white>$name</white>")
            } else {
                sender.sendMiniMessage("<prefix> <red>That rank doesn't exist!")
            }
        }
    }

    @Subcommand("setweight")
    @CommandPermission("essentials.command.ranks.setweight")
    @Description("Set a rank's weight")
    @Syntax("<name> <weight>")
    @CommandCompletion("@ranks @range:0-100")
    fun onSetWeight(sender: CommandSender, name: String, weight: Int) {
        RankManager.setRankWeight(name, weight).thenAccept { success ->
            if (success) {
                sender.sendMiniMessage("<prefix> <green>Set weight of <white>$name</white> to <white>$weight</white>")
            } else {
                sender.sendMiniMessage("<prefix> <red>That rank doesn't exist!")
            }
        }
    }

    @Subcommand("setprefix")
    @CommandPermission("essentials.command.ranks.setprefix")
    @Description("Set a rank's prefix")
    @Syntax("<name> <prefix>")
    @CommandCompletion("@ranks @nothing")
    fun onSetPrefix(sender: CommandSender, name: String, prefix: String) {
        RankManager.setRankPrefix(name, prefix).thenAccept { success ->
            if (success) {
                sender.sendMiniMessage("<prefix> <green>Set prefix of <white>$name</white> to <white>$prefix</white>")
            } else {
                sender.sendMiniMessage("<prefix> <red>That rank doesn't exist!")
            }
        }
    }

    @Subcommand("addperm")
    @CommandPermission("essentials.command.ranks.addperm")
    @Description("Add a permission to a rank")
    @Syntax("<name> <permission>")
    @CommandCompletion("@ranks @permissions")
    fun onAddPermission(sender: CommandSender, name: String, permission: String) {
        RankManager.addRankPermission(name, permission).thenAccept { success ->
            if (success) {
                sender.sendMiniMessage("<prefix> <green>Added permission <white>$permission</white> to <white>$name</white>")
            } else {
                sender.sendMiniMessage("<prefix> <red>That rank doesn't exist!")
            }
        }
    }

    @Subcommand("removeperm")
    @CommandPermission("essentials.command.ranks.removeperm")
    @Description("Remove a permission from a rank")
    @Syntax("<name> <permission>")
    @CommandCompletion("@ranks @rankperms")
    fun onRemovePermission(sender: CommandSender, name: String, permission: String) {
        RankManager.removeRankPermission(name, permission).thenAccept { success ->
            if (success) {
                sender.sendMiniMessage("<prefix> <green>Removed permission <white>$permission</white> from <white>$name</white>")
            } else {
                sender.sendMiniMessage("<prefix> <red>That rank doesn't exist!")
            }
        }
    }

    @Subcommand("setparent")
    @CommandPermission("essentials.command.ranks.setparent")
    @Description("Set a rank's parent")
    @Syntax("<name> <parent>")
    @CommandCompletion("@ranks @ranks")
    fun onSetParent(sender: CommandSender, name: String, parent: String) {
        RankManager.setRankParent(name, parent).thenAccept { success ->
            if (success) {
                sender.sendMiniMessage("<prefix> <green>Set parent of <white>$name</white> to <white>$parent</white>")
            } else {
                sender.sendMiniMessage("<prefix> <red>One of those ranks doesn't exist!")
            }
        }
    }

    @Subcommand("info")
    @CommandPermission("essentials.command.ranks.info")
    @Description("View information about a rank")
    @Syntax("<name>")
    @CommandCompletion("@ranks")
    fun onInfo(sender: CommandSender, name: String) {
        RankManager.getRankInfo(name).thenAccept { info ->
            if (info != null) {
                sender.sendMiniMessage("""
                    <prefix> <yellow>Rank Information:
                    <gray>Name: <white>${info.name}
                    <gray>Weight: <white>${info.weight}
                    <gray>Prefix: <white>${info.prefix ?: "None"}
                    <gray>Parents: <white>${info.parents.joinToString(", ") { it } ?: "None"}
                    <gray>Permissions: <white>${info.permissions.size}
                """.trimIndent())
            } else {
                sender.sendMiniMessage("<prefix> <red>That rank doesn't exist!")
            }
        }
    }

    @Subcommand("list")
    @CommandPermission("essentials.command.ranks.list")
    @Description("List all ranks")
    fun onList(sender: CommandSender) {
        RankManager.getAllRanks().thenAccept { ranks ->
            if (ranks.isEmpty()) {
                sender.sendMiniMessage("<prefix> <red>No ranks found!")
                return@thenAccept
            }

            val rankList = ranks.joinToString("\n") { rank ->
                "<gray>- <white>${rank.name} <gray>(Weight: <white>${rank.weight.orElse(0)}<gray>)"
            }

            sender.sendMiniMessage("""
                <prefix> <yellow>Available Ranks:
                $rankList
            """.trimIndent())
        }
    }
} 