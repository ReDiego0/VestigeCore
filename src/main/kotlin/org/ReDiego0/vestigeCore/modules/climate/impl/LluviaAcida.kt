package org.ReDiego0.vestigeCore.modules.climate.impl

import org.ReDiego0.vestigeCore.modules.climate.Climate
import org.ReDiego0.vestigeCore.modules.climate.ClimateType
import org.bukkit.Color
import org.bukkit.Particle
import org.bukkit.Sound
import org.bukkit.World
import org.bukkit.entity.Player
import kotlin.random.Random

object LluviaAcida : Climate {
    override val name: String = "Lluvia Ácida"
    override val displayName: String = "§2Lluvia Ácida"
    override val type: ClimateType = ClimateType.HOSTILE
    override val duration: Int = 300

    override fun applyVisuals(world: World) {
        world.setStorm(true)
        world.isThundering = false
    }

    override fun applyEffects(player: Player) {
        val loc = player.location
        val highestY = player.world.getHighestBlockYAt(loc)

        if (loc.blockY >= highestY) {
            player.damage(1.0)

            val dust = Particle.DustOptions(Color.fromRGB(50, 205, 50), 1.0f)
            player.world.spawnParticle(Particle.DUST, loc.add(0.0, 1.2, 0.0), 5, 0.5, 0.5, 0.5, dust)

            if (Random.nextInt(100) < 5) {
                player.playSound(loc, Sound.BLOCK_LAVA_EXTINGUISH, 0.2f, 1.5f)
            }
        }
    }
}