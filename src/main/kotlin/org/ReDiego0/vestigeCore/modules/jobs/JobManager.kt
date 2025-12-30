package org.ReDiego0.vestigeCore.modules.jobs

import org.ReDiego0.vestigeCore.VestigeCore
import org.bukkit.NamespacedKey
import org.bukkit.attribute.Attribute
import org.bukkit.attribute.AttributeModifier
import org.bukkit.entity.Player
import org.bukkit.inventory.EquipmentSlotGroup
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType

class JobManager(private val plugin: VestigeCore) {
    private val keyArchitectReach = NamespacedKey(plugin, "architect_reach")
    private val keyArchitectStep = NamespacedKey(plugin, "architect_step")

    fun refreshJobBuffs(player: Player) {
        val job = JobType.getJob(player)

        player.getAttribute(Attribute.PLAYER_BLOCK_INTERACTION_RANGE)?.removeModifier(keyArchitectReach)
        player.getAttribute(Attribute.GENERIC_STEP_HEIGHT)?.removeModifier(keyArchitectStep)

        player.removePotionEffect(PotionEffectType.HASTE)
        player.removePotionEffect(PotionEffectType.CONDUIT_POWER)

        when (job) {
            JobType.ARCHITECT -> {
                // Alcance +2
                val reachMod = AttributeModifier(
                    keyArchitectReach,
                    2.0,
                    AttributeModifier.Operation.ADD_NUMBER,
                    EquipmentSlotGroup.HAND
                )
                player.getAttribute(Attribute.PLAYER_BLOCK_INTERACTION_RANGE)?.addModifier(reachMod)

                // Paso +0.5
                val stepMod = AttributeModifier(
                    keyArchitectStep,
                    0.5,
                    AttributeModifier.Operation.ADD_NUMBER,
                    EquipmentSlotGroup.FEET
                )
                player.getAttribute(Attribute.GENERIC_STEP_HEIGHT)?.addModifier(stepMod)
            }
            JobType.PROSPECTOR -> {
                player.addPotionEffect(PotionEffect(PotionEffectType.HASTE, PotionEffect.INFINITE_DURATION, 1, false, false, false))
                player.addPotionEffect(PotionEffect(PotionEffectType.CONDUIT_POWER, PotionEffect.INFINITE_DURATION, 0, false, false, false))
            }
            else -> { /* Nada */ }
        }
    }
}