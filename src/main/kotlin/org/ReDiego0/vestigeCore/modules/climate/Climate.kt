package org.ReDiego0.vestigeCore.modules.climate

import org.bukkit.World
import org.bukkit.entity.Player

interface Climate {
    val name: String
    val displayName: String
    val type: ClimateType
    val duration: Int

    fun applyVisuals(world: World)
    fun applyEffects(player: Player)
}

enum class ClimateType {
    HOSTILE,
    NEUTRAL,
    BENEFICIAL
}