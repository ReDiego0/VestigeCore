package org.ReDiego0.vestigeCore.modules.aqua

import com.palmergames.bukkit.towny.TownyAPI
import org.ReDiego0.vestigeCore.VestigeCore
import org.ReDiego0.vestigeCore.modules.aqua.data.AquaDatabase
import org.bukkit.Bukkit
import java.util.concurrent.TimeUnit

class AquaTaxManager(
    private val plugin: VestigeCore,
    private val database: AquaDatabase
) {
    private val BASE_UPKEEP_COST = 10
    private val INTERVAL_MILLIS = TimeUnit.HOURS.toMillis(24)

    fun startScheduler() {
        val now = System.currentTimeMillis()
        var nextRun = database.getNextTaxTime()

        if (nextRun == 0L) {
            nextRun = now + INTERVAL_MILLIS
            database.setNextTaxTime(nextRun)
            plugin.logger.info("Sistema de Impuestos Aqua iniciado por primera vez. Cobro en 24h.")
        }

        val delayMillis = nextRun - now

        if (delayMillis <= 0) {
            plugin.logger.warning("El servidor estuvo apagado durante el cobro de Aqua. Ejecutando ahora...")
            collectTaxes()
        } else {
            val delayTicks = delayMillis / 50
            plugin.logger.info("Próximo cobro de Aqua programado en ${TimeUnit.MILLISECONDS.toHours(delayMillis)} horas.")

            Bukkit.getScheduler().runTaskLater(plugin, Runnable {
                collectTaxes()
            }, delayTicks)
        }
    }

    private fun collectTaxes() {
        plugin.logger.info("=== INICIANDO RECAUDACIÓN DE AQUA ===")

        val towny = TownyAPI.getInstance()
        val towns = towny.towns

        var collectedCount = 0
        var failedCount = 0

        for (town in towns) {
            val cost = BASE_UPKEEP_COST

            if (database.removeBalance(town.uuid, cost)) {
                collectedCount++
                town.residents.forEach { res -> res.player?.sendMessage("§b[Impuestos] Se han descontado $cost Aqua.") }
            } else {
                failedCount++
                plugin.logger.warning("La ciudad ${town.name} NO pudo pagar su mantenimiento de Aqua ($cost).")
                Bukkit.broadcastMessage("§c[ATENCIÓN] La ciudad §4${town.name} §cno ha pagado su mantenimiento de Aqua.")
            }
        }

        plugin.logger.info("Recaudación finalizada. Pagaron: $collectedCount. Morosos: $failedCount")
        scheduleNextRun()
    }

    private fun scheduleNextRun() {
        val now = System.currentTimeMillis()
        val nextRun = now + INTERVAL_MILLIS
        database.setNextTaxTime(nextRun)

        val delayTicks = INTERVAL_MILLIS / 50
        Bukkit.getScheduler().runTaskLater(plugin, Runnable {
            collectTaxes()
        }, delayTicks)

        plugin.logger.info("Siguiente ciclo de cobro guardado y programado.")
    }
}