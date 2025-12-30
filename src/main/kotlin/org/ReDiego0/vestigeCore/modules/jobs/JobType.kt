package org.ReDiego0.vestigeCore.modules.jobs

import org.bukkit.Material
import org.bukkit.entity.Player

enum class JobType(
    val id: String,
    val displayName: String,
    val permission: String,
    val icon: Material
) {
    PROSPECTOR("prospector", "Prospector", "vestige.job.prospector", Material.GOLDEN_PICKAXE),
    BIOCHEMIST("biochemist", "Bio-Químico", "vestige.job.biochemist", Material.BREWING_STAND),
    FABRICATOR("fabricator", "Fabricador", "vestige.job.fabricator", Material.ANVIL),
    ARCHITECT("architect", "Arquitecto", "vestige.job.architect", Material.SCAFFOLDING),
    SYNTHESIZER("synthesizer", "Sintetizador", "vestige.job.synthesizer", Material.COOKED_BEEF),
    NONE("none", "Desempleado", "", Material.BARRIER);

    companion object {
        fun getJob(player: Player): JobType {
            return entries.firstOrNull { it != NONE && player.hasPermission(it.permission) } ?: NONE
        }
    }
}