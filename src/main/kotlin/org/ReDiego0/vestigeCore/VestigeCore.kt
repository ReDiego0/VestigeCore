package org.ReDiego0.vestigeCore

import net.milkbowl.vault.economy.Economy
import org.ReDiego0.vestigeCore.modules.economy.EconomyManager
import org.ReDiego0.vestigeCore.modules.economy.VestigeEconomy
import org.ReDiego0.vestigeCore.commands.VestigeCommandManager
import org.ReDiego0.vestigeCore.modules.economy.commands.EconomySubCommand
import org.ReDiego0.vestigeCore.modules.jobs.JobManager
import org.ReDiego0.vestigeCore.modules.jobs.JobListener
import org.ReDiego0.vestigeCore.modules.jobs.commands.JobSubCommand
import org.ReDiego0.vestigeCore.modules.aqua.AquaManager
import org.ReDiego0.vestigeCore.modules.aqua.AquaTaxManager
import org.ReDiego0.vestigeCore.modules.aqua.TownyAquaManager
import org.ReDiego0.vestigeCore.modules.aqua.commands.AquaSubCommand
import org.ReDiego0.vestigeCore.modules.aqua.commands.TownyBankCommand
import org.ReDiego0.vestigeCore.modules.aqua.data.AquaDatabase



import org.bukkit.Bukkit
import org.bukkit.plugin.ServicePriority
import org.bukkit.plugin.java.JavaPlugin

class VestigeCore : JavaPlugin() {

    private lateinit var economyManager: EconomyManager
    private lateinit var commandManager: VestigeCommandManager
    private lateinit var jobManager: JobManager
    private lateinit var aquaManager: AquaManager
    private lateinit var aquaTaxManager: AquaTaxManager
    private lateinit var townyAquaManager: TownyAquaManager
    private lateinit var aquaDatabase: AquaDatabase

    override fun onEnable() {
        saveDefaultConfig()
        aquaDatabase = AquaDatabase(this)

        economyManager = EconomyManager(this)
        commandManager = VestigeCommandManager(this)
        jobManager = JobManager(this)
        aquaManager = AquaManager(this)
        aquaTaxManager = AquaTaxManager(this, aquaDatabase)
        townyAquaManager = TownyAquaManager(this, aquaManager, aquaDatabase)

        server.pluginManager.registerEvents(JobListener(this, jobManager), this)

        commandManager.register(EconomySubCommand(economyManager))
        commandManager.register(JobSubCommand(jobManager))
        commandManager.register(AquaSubCommand(aquaManager))
        commandManager.register(TownyBankCommand(townyAquaManager))

        aquaTaxManager.startScheduler()
        val cmd = getCommand("vcore")
        if (cmd != null) {
            cmd.setExecutor(commandManager)
            cmd.setTabCompleter(commandManager)
            logger.info("Comando /vcore registrado correctamente.")
        } else {
            logger.severe("ERROR FATAL: No se encontró 'vcore' en plugin.yml. Desactivando plugin.")
            server.pluginManager.disablePlugin(this)
            return
        }

        if (server.pluginManager.getPlugin("Vault") != null) {
            val provider = VestigeEconomy(this, economyManager)
            server.servicesManager.register(
                Economy::class.java,
                provider,
                this,
                ServicePriority.Highest
            )
            logger.info("Modulo Economía cargada y linkeada a Vault.")
        } else {
            logger.warning("Vault no encontrado. La economía funcionará internamente pero no con otros plugins.")
        }

        val saveInterval = config.getLong("economy.settings.save-interval", 300) * 20L
        Bukkit.getScheduler().runTaskTimerAsynchronously(this, Runnable {
            economyManager.saveAccounts()
        }, saveInterval, saveInterval)
    }

    override fun onDisable() {
        if (::economyManager.isInitialized) {
            economyManager.saveAccounts()
        }
    }
}