package org.ReDiego0.vestigeCore.modules.climate

import com.palmergames.bukkit.towny.TownyAPI
import org.ReDiego0.vestigeCore.VestigeCore
import org.ReDiego0.vestigeCore.modules.aqua.data.AquaDatabase
import org.ReDiego0.vestigeCore.modules.climate.impl.*
import org.bukkit.Bukkit
import org.bukkit.World
import org.bukkit.boss.BarColor
import org.bukkit.boss.BarStyle
import org.bukkit.boss.BossBar
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerChangedWorldEvent
import org.bukkit.event.player.PlayerQuitEvent
import java.util.UUID

class ClimateManager(
    private val plugin: VestigeCore,
    private val aquaDatabase: AquaDatabase
) : Listener {

    private val worldAllowedClimates: Map<String, List<Climate>> = mapOf(
        "Cinder-VI" to listOf(LluviaAcida, InviernoNuclear, SobrecargaOxigeno, BajaGravedad)
    )

    private val activeWorldClimates: MutableMap<String, Climate> = mutableMapOf()
    private val worldClimateStartTimes: MutableMap<String, Long> = mutableMapOf()
    private val activeBossBars: MutableMap<UUID, BossBar> = mutableMapOf()

    // Intervalo de cambio (configurable), por defecto 10 min
    private var changeIntervalTicks: Long = 12000L

    init {
        plugin.server.pluginManager.registerEvents(this, plugin)
        changeIntervalTicks = 12000L
        pickNewClimates()
    }

    fun start() {
        plugin.server.scheduler.runTaskTimer(plugin, Runnable {
            pickNewClimates()
        }, changeIntervalTicks, changeIntervalTicks)

        // Efectos cada segundo (20 ticks)
        plugin.server.scheduler.runTaskTimer(plugin, Runnable {
            applyClimateEffectsToPlayers()
        }, 0L, 20L)
    }

    private fun pickNewClimates() {
        val currentTime = System.currentTimeMillis()
        for ((worldName, allowedList) in worldAllowedClimates) {
            val world = Bukkit.getWorld(worldName) ?: continue
            val newClimate = allowedList.random()

            activeWorldClimates[worldName] = newClimate
            worldClimateStartTimes[worldName] = currentTime

            newClimate.applyVisuals(world)
            announceClimateChange(world, newClimate)
        }
    }

    private fun announceClimateChange(world: World, climate: Climate) {
        val msg = when (climate.type) {
            ClimateType.HOSTILE -> "§c[ALERTA] Anomalía atmosférica detectada: §l${climate.displayName}§c."
            ClimateType.NEUTRAL -> "§a[SISTEMA] Atmósfera estable: §l${climate.displayName}§a."
            ClimateType.BENEFICIAL -> "§e[EVENTO] Flujo de energía positivo: §l${climate.displayName}§e."
        }
        world.players.forEach { it.sendMessage(msg) }
    }

    private fun applyClimateEffectsToPlayers() {
        val townyAPI = TownyAPI.getInstance()
        val totalDurationMs = changeIntervalTicks * 50L

        for (player in plugin.server.onlinePlayers) {
            val worldName = player.world.name
            val currentClimate = activeWorldClimates[worldName]

            if (currentClimate == null) {
                removeBossBar(player)
                continue
            }

            val startTime = worldClimateStartTimes[worldName] ?: System.currentTimeMillis()
            val elapsedTime = System.currentTimeMillis() - startTime
            val progress = (1.0 - (elapsedTime.toDouble() / totalDurationMs.toDouble())).coerceIn(0.0, 1.0)

            val townBlock = try { townyAPI.getTownBlock(player.location) } catch (e: Exception) { null }
            val isInWilderness = (townBlock == null || !townBlock.hasTown())
            var isProtected = false

            if (!isInWilderness) {
                try {
                    val town = townBlock!!.town
                    // Checkeamos saldo de Aqua. Si > 0, está protegido.
                    if (aquaDatabase.getBalance(town.uuid) > 0) {
                        isProtected = true
                    }
                } catch (e: Exception) { }
            }

            // Si es hostil, afecta solo a NO protegidos.
            // Si es beneficioso, afecta a todos (o solo a wilderness, según prefieras. Aquí copio tu lógica anterior).
            val shouldBeAffected = when (currentClimate.type) {
                ClimateType.HOSTILE -> !isProtected
                ClimateType.BENEFICIAL -> !isProtected // Normalmente los beneficios son globales, pero tu lógica previa lo restringía. Ajustar si quieres.
                ClimateType.NEUTRAL -> false
            }

            if (shouldBeAffected) {
                currentClimate.applyEffects(player)
            }

            updateBossBar(player, currentClimate, progress, shouldBeAffected)
        }
    }

    private fun updateBossBar(player: org.bukkit.entity.Player, climate: Climate, progress: Double, isAffected: Boolean) {
        val playerUUID = player.uniqueId
        if (climate.type == ClimateType.NEUTRAL) {
            removeBossBar(player)
            return
        }

        val (barTitle, barColor) = when (climate.type) {
            ClimateType.HOSTILE -> "§c☠ CLIMA: §l${climate.displayName}" to BarColor.RED
            ClimateType.BENEFICIAL -> "§e★ CLIMA: §l${climate.displayName}" to BarColor.GREEN
            else -> "" to BarColor.WHITE
        }

        var bar = activeBossBars[playerUUID]
        if (bar == null) {
            bar = Bukkit.createBossBar(barTitle, barColor, BarStyle.SOLID)
            bar.addPlayer(player)
            activeBossBars[playerUUID] = bar
        }

        bar.setTitle(barTitle)
        bar.color = barColor
        bar.progress = progress
        bar.style = if (isAffected) BarStyle.SOLID else BarStyle.SEGMENTED_6
    }

    private fun removeBossBar(player: org.bukkit.entity.Player) {
        activeBossBars.remove(player.uniqueId)?.removePlayer(player)
    }

    @EventHandler
    fun onPlayerQuit(event: PlayerQuitEvent) {
        removeBossBar(event.player)
    }

    @EventHandler
    fun onWorldChange(event: PlayerChangedWorldEvent) {
        plugin.server.scheduler.runTask(plugin, Runnable {
            removeBossBar(event.player)
        })
    }
}