package org.ReDiego0.vestigeCore.modules.climate.impl

import org.ReDiego0.vestigeCore.modules.climate.Climate
import org.ReDiego0.vestigeCore.modules.climate.ClimateType
import org.bukkit.Particle
import org.bukkit.Sound
import org.bukkit.World
import org.bukkit.entity.Player
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import kotlin.random.Random

object BajaGravedad : Climate {
    override val name: String = "Baja Gravedad"
    override val displayName: String = "§5Baja Gravedad"
    override val type: ClimateType = ClimateType.BENEFICIAL
    override val duration: Int = 240

    override fun applyVisuals(world: World) {
        world.setStorm(false)
        world.isThundering = false
    }

    override fun applyEffects(player: Player) {
        player.addPotionEffect(PotionEffect(PotionEffectType.JUMP_BOOST, 40, 2, false, false))
        player.addPotionEffect(PotionEffect(PotionEffectType.SLOW_FALLING, 40, 0, false, false))

        player.world.spawnParticle(Particle.END_ROD, player.location.add(0.0, 0.5, 0.0), 2, 0.3, 0.5, 0.3, 0.01)

        if (Random.nextInt(100) < 1) {
            player.playSound(player.location, Sound.BLOCK_AMETHYST_BLOCK_CHIME, 0.5f, 0.5f)
        }
    }
}