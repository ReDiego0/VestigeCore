package org.ReDiego0.vestigeCore.commands

import org.bukkit.command.CommandSender

interface VestigeSubCommand {
    fun getName(): String
    fun getPermission(): String
    fun execute(sender: CommandSender, args: List<String>)
    fun tabComplete(sender: CommandSender, args: List<String>): List<String>
}