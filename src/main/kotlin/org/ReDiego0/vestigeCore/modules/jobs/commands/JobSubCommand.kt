package org.ReDiego0.vestigeCore.modules.jobs.commands

import org.ReDiego0.vestigeCore.commands.VestigeSubCommand
import org.ReDiego0.vestigeCore.modules.jobs.JobManager
import org.ReDiego0.vestigeCore.modules.jobs.JobType
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender

class JobSubCommand(private val manager: JobManager) : VestigeSubCommand {

    override fun getName(): String = "jobs"
    override fun getPermission(): String = "vestige.admin.jobs"

    override fun execute(sender: CommandSender, args: List<String>) {
        if (args.isEmpty()) {
            sendHelp(sender)
            return
        }

        val action = args[0].lowercase()

        if (action == "refresh") {
            if (args.size < 2) {
                sender.sendMessage("${ChatColor.RED}Uso: /vcore jobs refresh <player|all>")
                return
            }

            val targetName = args[1]

            if (targetName.lowercase() == "all") {
                val onlinePlayers = Bukkit.getOnlinePlayers()
                onlinePlayers.forEach { manager.refreshJobBuffs(it) }
                sender.sendMessage("${ChatColor.GREEN}Se han actualizado los trabajos de ${ChatColor.YELLOW}${onlinePlayers.size} ${ChatColor.GREEN}jugadores.")
                return
            }

            val target = Bukkit.getPlayer(targetName)
            if (target == null) {
                sender.sendMessage("${ChatColor.RED}Jugador no encontrado o desconectado.")
                return
            }

            manager.refreshJobBuffs(target)
            val currentJob = JobType.getJob(target)
            sender.sendMessage("${ChatColor.GREEN}Trabajo de ${target.name} actualizado. Rol actual: ${ChatColor.GOLD}${currentJob.displayName}")
            return
        }

        sendHelp(sender)
    }

    override fun tabComplete(sender: CommandSender, args: List<String>): List<String> {
        if (args.size == 1) return listOf("refresh").filter { it.startsWith(args[0].lowercase()) }
        if (args.size == 2 && args[0].lowercase() == "refresh") {
            val names = Bukkit.getOnlinePlayers().map { it.name }.toMutableList()
            names.add("all")
            return names.filter { it.startsWith(args[1], true) }
        }
        return emptyList()
    }

    private fun sendHelp(sender: CommandSender) {
        sender.sendMessage("${ChatColor.YELLOW}/vcore jobs refresh <player|all> ${ChatColor.GRAY}- Recargar buffs de trabajos.")
    }
}