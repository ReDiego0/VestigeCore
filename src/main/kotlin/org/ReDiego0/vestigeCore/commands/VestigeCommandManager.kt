package org.ReDiego0.vestigeCore.commands

import org.ReDiego0.vestigeCore.VestigeCore
import org.bukkit.ChatColor
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter

class VestigeCommandManager(private val plugin: VestigeCore) : CommandExecutor, TabCompleter {
    private val subCommands = HashMap<String, VestigeSubCommand>()

    fun register(cmd: VestigeSubCommand) {
        subCommands[cmd.getName()] = cmd
    }

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (args.isEmpty()) {
            sendHelp(sender)
            return true
        }

        val subName = args[0].lowercase()
        val subCmd = subCommands[subName]

        if (subCmd == null) {
            sender.sendMessage("${ChatColor.RED}Módulo desconocida. Usa /vcore help")
            return true
        }

        if (!sender.hasPermission(subCmd.getPermission())) {
            sender.sendMessage("${ChatColor.RED}No tienes permiso para acceder al módulo: $subName")
            return true
        }

        subCmd.execute(sender, args.drop(1))
        return true
    }

    override fun onTabComplete(sender: CommandSender, command: Command, alias: String, args: Array<out String>): MutableList<String> {
        if (args.size == 1) {
            return subCommands.keys
                .filter { it.startsWith(args[0].lowercase()) }
                .toMutableList()
        }


        val subName = args[0].lowercase()
        val subCmd = subCommands[subName] ?: return mutableListOf()

        return subCmd.tabComplete(sender, args.drop(1)).toMutableList()
    }

    private fun sendHelp(sender: CommandSender) {
        sender.sendMessage("${ChatColor.DARK_GRAY}--------- [ ${ChatColor.GOLD}Vestige Core ${ChatColor.DARK_GRAY}] ---------")
        subCommands.keys.forEach { name ->
            sender.sendMessage("${ChatColor.YELLOW}/vcore $name ${ChatColor.GRAY}- Gestionar módulo $name")
        }
    }
}