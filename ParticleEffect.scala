import java.lang.reflect.{Field, Method, Constructor}

import org.bukkit.entity.Player
import org.bukkit.util.Vector
import org.bukkit.{Bukkit, Location, Color, Material}

import scala.collection.mutable
import scala.collection.JavaConversions._
import scala.util.{Success, Failure, Try}

/**
  * '''ParticleEffect Library'''
  *
  * This library was created by @DarkBlade12 and allows you to display all Minecraft particle effects on a Bukkit server
  *
  * You are welcome to use it, modify it and redistribute it under the following conditions:
  *  - Don't claim this class as your own
  *  - Don't remove this disclaimer
  *
  * Special thanks:
  *  - @microgeek (original idea, names and packet parameters)
  *  - @ShadyPotato (1.8 names, ids and packet parameters)
  *  - @RingOfStorms (particle behavior)
  *  - @Cybermaxke (particle behavior)
  *  - @JamieSinn (hosting a jenkins server and documentation for particleeffect)
  *
  * ''It would be nice if you provide credit to me if you use this class in a published project''
  *
  * Rewritten in Scala by TheReturningVoid
  *
  * @author DarkBlade12, TheReturningVoid
  * @version 1.7
  */
object ParticleEffect extends Enumeration {

  /**
    * A particle effect which is displayed by exploding tnt and creepers:
    *  - It looks like a white cloud
    *  - The speed value influences the velocity at which the particle flies off
    */
  final val EXPLOSION_NORMAL = PEValue("explode", 0, -1, ParticleProperty.DIRECTIONAL)

  /**
    * A particle effect which is displayed by exploding ghast fireballs and wither skulls:
    *  - It looks like a gray ball which is fading away
    *  - The speed value slightly influences the size of this particle effect
    */
  final val EXPLOSION_LARGE = PEValue("largeexplode", 1, -1)

  /**
    * A particle effect which is displayed by exploding tnt and creepers:
    *  - It looks like a crowd of gray balls which are fading away
    *  - The speed value has no influence on this particle effect
    */
  final val EXPLOSION_HUGE = PEValue("hugeexplosion", 2, -1)

  /**
    * A particle effect which is displayed by launching fireworks:
    *  - It looks like a white star which is sparkling
    *  - The speed value influences the velocity at which the particle flies off
    */
  final val FIREWORKS_SPARK = PEValue("fireworksSpark", 3, -1, ParticleProperty.DIRECTIONAL)

  /**
    * A particle effect which is displayed by swimming entities and arrows in water:
    *  - It looks like a bubble
    *  - The speed value influences the velocity at which the particle flies off
    */
  final val WATER_BUBBLE = PEValue("bubble", 4, -1, ParticleProperty.DIRECTIONAL, ParticleProperty.REQUIRES_WATER)

  /**
    * A particle effect which is displayed by swimming entities and shaking wolves:
    *  - It looks like a blue drop
    *  - The speed value has no influence on this particle effect
    */
  final val WATER_SPLASH = PEValue("splash", 5, -1, ParticleProperty.DIRECTIONAL)

  /**
    * A particle effect which is displayed on water when fishing:
    *  - It looks like a blue droplet
    *  - The speed value influences the velocity at which the particle flies off
    */
  final val WATER_WAKE = PEValue("wake", 6, 7, ParticleProperty.DIRECTIONAL)

  /**
    * A particle effect which is displayed by water:
    *  - It looks like a tiny blue square
    *  - The speed value has no influence on this particle effect
    */
  final val SUSPENDED = PEValue("suspended", 7, -1, ParticleProperty.REQUIRES_WATER)

  /**
    * A particle effect which is displayed by air when close to bedrock and the in the void:
    *  - It looks like a tiny gray square
    *  - The speed value has no influence on this particle effect
    */
  final val SUSPENDED_DEPTH = PEValue("depthSuspend", 8, -1, ParticleProperty.DIRECTIONAL)

  /**
    * A particle effect which is displayed when landing a critical hit and by arrows:
    *  - It looks like a light brown cross
    *  - The speed value influences the velocity at which the particle flies off
    */
  final val CRIT = PEValue("crit", 9, -1, ParticleProperty.DIRECTIONAL)

  /**
    * A particle effect which is displayed when landing a hit with an enchanted weapon:
    *  - It looks like a cyan star
    *  - The speed value influences the velocity at which the particle flies off
    */
  final val CRIT_MAGIC = PEValue("magicCrit", 10, -1, ParticleProperty.DIRECTIONAL)

  /**
    * A particle effect which is displayed by primed tnt, torches, droppers, dispensers, end portals, brewing stands and
    * monster spawners:
    *  - It looks like a little gray cloud
    *  - The speed value influences the velocity at which the particle flies off
    */
  final val SMOKE_NORMAL = PEValue("smoke", 11, -1, ParticleProperty.DIRECTIONAL)

  /**
    * A particle effect which is displayed by fire, minecarts with furnace and blazes:
    *  - It looks like a large gray cloud
    *  - The speed value influences the velocity at which the particle flies off
    */
  final val SMOKE_LARGE = PEValue("smokelarge", 12, -1, ParticleProperty.DIRECTIONAL)

  /**
    * A particle effect which is displayed when splash potions or bottles o' enchanting hit something:
    *  - It looks like a white swirl
    *  - The speed value causes the particle to only move upwards when set to 0
    *  - Only the motion on the y-axis can be controlled, the motion on the x- and z-axis are multiplied by 0.1 when setting the values to 0
    */
  final val SPELL = PEValue("spell", 13, -1)

  /**
    * A particle effect which is displayed when instant splash potions hit something:
    *  - It looks like a white cross
    *  - The speed value causes the particle to only move upwards when set to 0
    *  - Only the motion on the y-axis can be controlled, the motion on the x- and z-axis are multiplied by 0.1 when setting the values to 0
    */
  final val SPELL_INSTANT = PEValue("instantSpell", 14, -1)

  /**
    * A particle effect which is displayed by entities with active potion effects:
    *  - It looks like a colored swirl
    *  - The speed value causes the particle to be colored black when set to 0
    *  - The particle color gets lighter when increasing the speed and darker when decreasing the speed
    */
  final val SPELL_MOB = PEValue("mobSpell", 15, -1, ParticleProperty.COLORABLE)

  /**
    * A particle effect which is displayed by entities with active potion effects applied through a beacon:
    *  - It looks like a transparent colored swirl
    *  - The speed value causes the particle to be always colored black when set to 0
    *  - The particle color gets lighter when increasing the speed and darker when decreasing the speed
    */
  final val SPELL_MOB_AMBIENT = PEValue("mobSpellAmbient", 16, -1, ParticleProperty.COLORABLE)

  /**
    * A particle effect which is displayed by witches:
    *  - It looks like a purple cross
    *  - The speed value causes the particle to only move upwards when set to 0
    *  - Only the motion on the y-axis can be controlled, the motion on the x- and z-axis are multiplied by 0.1 when setting the values to 0
    */
  final val SPELL_WITCH = PEValue("witchMagic", 17, -1)

  /**
    * A particle effect which is displayed by blocks beneath a water source:
    *  - It looks like a blue drip
    *  - The speed value has no influence on this particle effect
    */
  final val DRIP_WATER = PEValue("dripWater", 18, -1)

  /**
    * A particle effect which is displayed by blocks beneath a lava source:
    *  - It looks like an orange drip
    *  - The speed value has no influence on this particle effect
    */
  final val DRIP_LAVA = PEValue("dripLava", 19, -1)

  /**
    * A particle effect which is displayed when attacking a villager in a village:
    *  - It looks like a cracked gray heart
    *  - The speed value has no influence on this particle effect
    */
  final val VILLAGER_ANGRY = PEValue("angryVillager", 20, -1)

  /**
    * A particle effect which is displayed when using bone meal and trading with a villager in a village:
    *  - It looks like a green star
    *  - The speed value has no influence on this particle effect
    */
  final val VILLAGER_HAPPY = PEValue("happyVillager", 21, -1, ParticleProperty.DIRECTIONAL)

  /**
    * A particle effect which is displayed by mycelium:
    *  - It looks like a tiny gray square
    *  - The speed value has no influence on this particle effect
    */
  final val TOWN_AURA = PEValue("townaura", 22, -1, ParticleProperty.DIRECTIONAL)

  /**
    * A particle effect which is displayed by note blocks:
    *  - It looks like a colored note
    *  - The speed value causes the particle to be colored green when set to 0
    */
  final val NOTE = PEValue("note", 23, -1, ParticleProperty.COLORABLE)

  /**
    * A particle effect which is displayed by nether portals, endermen, ender pearls, eyes of ender, ender chests and dragon eggs:
    *  - It looks like a purple cloud
    *  - The speed value influences the spread of this particle effect
    */
  final val PORTAL = PEValue("portal", 24, -1, ParticleProperty.DIRECTIONAL)

  /**
    * A particle effect which is displayed by enchantment tables which are nearby bookshelves:
    *  - It looks like a cryptic white letter
    *  - The speed value influences the spread of this particle effect
    */
  final val ENCHANTMENT_TABLE = PEValue("enchantmenttable", 25, -1, ParticleProperty.DIRECTIONAL)

  /**
    * A particle effect which is displayed by torches, active furnaces, magma cubes and monster spawners:
    *  - It looks like a tiny flame
    *  - The speed value influences the velocity at which the particle flies off
    */
  final val FLAME = PEValue("flame", 26, -1, ParticleProperty.DIRECTIONAL)

  /**
    * A particle effect which is displayed by lava:
    *  - It looks like a spark
    *  - The speed value has no influence on this particle effect
    */
  final val LAVA = PEValue("lava", 27, -1)

  /**
    * A particle effect which is currently unused:
    *  - It looks like a transparent gray square
    *  - The speed value has no influence on this particle effect
    */
  final val FOOTSTEP = PEValue("footstep", 28, -1)

  /**
    * A particle effect which is displayed when a mob dies:
    *  - It looks like a large white cloud
    *  - The speed value influences the velocity at which the particle flies off
    */
  final val CLOUD = PEValue("cloud", 29, -1, ParticleProperty.DIRECTIONAL)

  /**
    * A particle effect which is displayed by redstone ore, powered redstone, redstone torches and redstone repeaters:
    * It looks like a tiny colored cloud
    * The speed value causes the particle to be colored red when set to 0
    */
  final val REDSTONE = PEValue("reddust", 30, -1, ParticleProperty.COLORABLE)

  /**
    * A particle effect which is displayed when snowballs hit a block:
    *  - It looks like a little piece with the snowball texture
    *  - The speed value has no influence on this particle effect
    */
  final val SNOWBALL = PEValue("snowballpoof", 31, -1)

  /**
    * A particle effect which is currently unused:
    *  - It looks like a tiny white cloud
    *  - The speed value influences the velocity at which the particle flies off
    */
  final val SNOW_SHOVEL = PEValue("snowshovel", 32, -1, ParticleProperty.DIRECTIONAL)

  /**
    * A particle effect which is displayed by slimes:
    *  - It looks like a tiny part of the slimeball icon
    *  - The speed value has no influence on this particle effect
    */
  final val SLIME = PEValue("slime", 33, -1)

  /**
    * A particle effect which is displayed when breeding and taming animals:
    *  - It looks like a red heart
    *  - The speed value has no influence on this particle effect
    */
  final val HEART = PEValue("heart", 34, -1)

  /**
    * A particle effect which is displayed by barriers:
    *  - It looks like a red box with a slash through it
    *  - The speed value has no influence on this particle effect
    */
  final val BARRIER = PEValue("barrier", 35, 8)

  /**
    * A particle effect which is displayed when breaking a tool or eggs hit a block:
    *  - It looks like a little piece with an item texture
    */
  final val ITEM_CRACK = PEValue("iconcrack", 36, -1, ParticleProperty.DIRECTIONAL, ParticleProperty.REQUIRES_DATA)

  /**
    * A particle effect which is displayed when breaking blocks or sprinting:
    *  - It looks like a little piece with a block texture
    *  - The speed value has no influence on this particle effect
    */
  final val BLOCK_CRACK = PEValue("blockcrack", 37, -1, ParticleProperty.REQUIRES_DATA)

  /**
    * A particle effect which is displayed when falling:
    *  - It looks like a little piece with a block texture
    */
  final val BLOCK_DUST = PEValue("blockdust", 38, 7, ParticleProperty.DIRECTIONAL, ParticleProperty.REQUIRES_DATA)

  /**
    * A particle effect which is displayed when rain hits the ground:
    *  - It looks like a blue droplet
    *  - The speed value has no influence on this particle effect
    */
  final val WATER_DROP = PEValue("droplet", 39, 8)

  /**
    * A particle effect which is currently unused:
    *  - It has no visual effect
    */
  final val ITEM_TAKE = PEValue("take", 40, 8)

  /**
    * A particle effect which is displayed by elder guardians:
    *  - It looks like the shape of the elder guardian
    *  - The speed value has no influence on this particle effect
    *  - The offset values have no influence on this particle effect
    */
  final val MOB_APPEARANCE = PEValue("mobappearance", 41, 8)

  case class PEVal(name: String, particleID: Int, requiredVersion: Int, properties: ParticleProperty.Value*) extends Val(name) {
    /**
      * Determine if this particle effect has a specific property
      *
      * @return Whether it has the property or not
      */
    def hasProperty(property: ParticleProperty.Value): Boolean = properties.contains(property)

    /**
      * Determine if this particle effect is supported by your current server version
      *
      * @return Whether the particle effect is supported or not
      */
    def isSupported: Boolean = if (requiredVersion == -1) true else ParticlePacket.version >= requiredVersion

    /**
      * Displays a particle effect which is only visible for all players within a certain range in the world of @param center
      *
      * @param offsetX Maximum distance particles can fly away from the center on the x-axis
      * @param offsetY Maximum distance particles can fly away from the center on the y-axis
      * @param offsetZ Maximum distance particles can fly away from the center on the z-axis
      * @param speed Display speed of the particles
      * @param amount Amount of particles
      * @param center Center location of the effect
      * @param range Range of the visibility
      * @throws ParticleVersionException If the particle effect is not supported by the server version
      * @throws ParticleDataException If the particle effect requires additional data
      * @throws IllegalArgumentException If the particle effect requires water and none is at the center location
      * @see `ParticlePacket`
      * @see `ParticlePacket.sendTo(Location, Double)`
      */
    @throws(classOf[ParticleVersionException])
    @throws(classOf[ParticleDataException])
    @throws(classOf[IllegalArgumentException])
    def display(offsetX: Float, offsetY: Float, offsetZ: Float, speed: Float, amount: Int, center: Location, range: Double): Unit = {
      if (!isSupported) throw new ParticleVersionException("This particle effect is not supported by your server version")
      if (hasProperty(ParticleProperty.REQUIRES_DATA)) throw new ParticleDataException("This particle effect requires additional data")
      if (hasProperty(ParticleProperty.REQUIRES_WATER) && !isWater(center)) throw new IllegalArgumentException("There is no water at the target location")
      new ParticlePacket(this, offsetX, offsetY, offsetZ, speed, amount, range > 256, None).sendTo(center, range)
    }

    /**
      * Displays a particle effect which is only visible for the specified players
      *
      * @param offsetX Maximum distance particles can fly away from the center on the x-axis
      * @param offsetY Maximum distance particles can fly away from the center on the y-axis
      * @param offsetZ Maximum distance particles can fly away from the center on the z-axis
      * @param speed Display speed of the particles
      * @param amount Amount of particles
      * @param center Center location of the effect
      * @param players Receivers of the effect
      * @throws ParticleVersionException If the particle effect is not supported by the server version
      * @throws ParticleDataException If the particle effect requires additional data
      * @throws IllegalArgumentException If the particle effect requires water and none is at the center location
      * @see `ParticlePacket`
      * @see `ParticlePacket.sendTo(Location, List[Player])`
      */
    @throws(classOf[ParticleVersionException])
    @throws(classOf[ParticleDataException])
    @throws(classOf[IllegalArgumentException])
    def display(offsetX: Float, offsetY: Float, offsetZ: Float, speed: Float, amount: Int, center: Location, players: List[Player]): Unit = {
      if (!isSupported) throw new ParticleVersionException("This particle effect is not supported by your server version")
      if (hasProperty(ParticleProperty.REQUIRES_DATA)) throw new ParticleDataException("This particle effect requires additional data")
      if (hasProperty(ParticleProperty.REQUIRES_WATER) && !isWater(center)) throw new IllegalArgumentException("There is no water at the target location")
      new ParticlePacket(this, offsetX, offsetY, offsetZ, speed, amount, isLongDistance(center, players), None).sendTo(center, players)
    }

    /**
      * Displays a particle effect which is only visible for the specified players
      *
      * @param offsetX Maximum distance particles can fly away from the center on the x-axis
      * @param offsetY Maximum distance particles can fly away from the center on the y-axis
      * @param offsetZ Maximum distance particles can fly away from the center on the z-axis
      * @param speed Display speed of the particles
      * @param amount Amount of particles
      * @param center Center location of the effect
      * @param players Receivers of the effect
      * @throws ParticleVersionException If the particle effect is not supported by the server version
      * @throws ParticleDataException If the particle effect requires additional data
      * @throws IllegalArgumentException If the particle effect requires water and none is at the center location
      * @see `display(Float, Float, Float, Float, Int, Location, List[Player])`
      */
    @throws(classOf[ParticleVersionException])
    @throws(classOf[ParticleDataException])
    @throws(classOf[IllegalArgumentException])
    def display(offsetX: Float, offsetY: Float, offsetZ: Float, speed: Float, amount: Int, center: Location, players: Player*): Unit = display(offsetX, offsetY, offsetZ, speed, amount, center, players.toList)

    /**
      * Displays a single particle which flies into a determined direction and is only visible for all players within a certain range in the world of @param center
      *
      * @param direction Direction of the particle
      * @param speed Display speed of the particle
      * @param center Center location of the effect
      * @param range Range of the visibility
      * @throws ParticleVersionException If the particle effect is not supported by the server version
      * @throws ParticleDataException If the particle effect requires additional data
      * @throws IllegalArgumentException If the particle effect is not directional or if it requires water and none is at the center location
      * @see `ParticlePacket#ParticlePacket(ParticleEffect, Vector, float, boolean, ParticleData)`
      * @see `ParticlePacket#sendTo(Location, double)`
      */
    @throws(classOf[ParticleVersionException])
    @throws(classOf[ParticleDataException])
    @throws(classOf[IllegalArgumentException])
    def display(direction: Vector, speed: Float, center: Location, range: Double): Unit = {
      if (!isSupported) throw new ParticleVersionException("This particle effect is not supported by your server version")
      if (hasProperty(ParticleProperty.REQUIRES_DATA)) throw new ParticleDataException("This particle effect requires additional data")
      if (!hasProperty(ParticleProperty.DIRECTIONAL)) throw new ParticleDataException("This particle is not directional")
      if (hasProperty(ParticleProperty.REQUIRES_WATER) && !isWater(center)) throw new IllegalArgumentException("There is no water at the target location")
      new ParticlePacket(this, direction, speed, range > 256, None).sendTo(center, range)
    }

    /**
      * Displays a single particle which flies into a determined direction and is only visible for the specified players
      *
      * @param direction Direction of the particle
      * @param speed Display speed of the particle
      * @param center Center location of the effect
      * @param players Receivers of the effect
      * @throws ParticleVersionException If the particle effect is not supported by the server version
      * @throws ParticleDataException If the particle effect requires additional data
      * @throws IllegalArgumentException If the particle effect is not directional or if it requires water and none is at the center location
      * @see `ParticlePacket#ParticlePacket(ParticleEffect, Vector, float, boolean, ParticleData)`
      * @see `ParticlePacket#sendTo(Location, List)`
      */
    @throws(classOf[ParticleVersionException])
    @throws(classOf[ParticleDataException])
    @throws(classOf[IllegalArgumentException])
    def display(direction: Vector, speed: Float, center: Location, players: List[Player]): Unit = {
      if (!isSupported) throw new ParticleVersionException("This particle effect is not supported by your server version")
      if (hasProperty(ParticleProperty.REQUIRES_DATA)) throw new ParticleDataException("This particle effect requires additional data")
      if (!hasProperty(ParticleProperty.DIRECTIONAL)) throw new ParticleDataException("This particle is not directional")
      if (hasProperty(ParticleProperty.REQUIRES_WATER) && !isWater(center)) throw new IllegalArgumentException("There is no water at the target location")
      new ParticlePacket(this, direction, speed, isLongDistance(center, players), None).sendTo(center, players)
    }

    /**
      * Displays a single particle which flies into a determined direction and is only visible for the specified players
      *
      * @param direction Direction of the particle
      * @param speed Display speed of the particle
      * @param center Center location of the effect
      * @param players Receivers of the effect
      * @throws ParticleVersionException If the particle effect is not supported by the server version
      * @throws ParticleDataException If the particle effect requires additional data
      * @throws IllegalArgumentException If the particle effect is not directional or if it requires water and none is at the center location
      * @see `#display(Vector, float, Location, List)`
      */
    @throws(classOf[ParticleVersionException])
    @throws(classOf[ParticleDataException])
    @throws(classOf[IllegalArgumentException])
    def display(direction: Vector, speed: Float, center: Location, players: Player*): Unit = display(direction, speed, center, players.toList)

    /**
      * Displays a single particle which is colored and only visible for all players within a certain range in the world of @param center
      *
      * @param color Color of the particle
      * @param center Center location of the effect
      * @param range Range of the visibility
      * @throws ParticleVersionException If the particle effect is not supported by the server version
      * @throws ParticleColorException If the particle effect is not colorable or the color type is incorrect
      * @see `ParticlePacket#ParticlePacket(ParticleEffect, ParticleColor, boolean)`
      * @see `ParticlePacket#sendTo(Location, double)`
      */
    def display(color: ParticleColor, center: Location, range: Double): Unit = {
      if (!isSupported) throw new ParticleVersionException("This particle effect is not supported by your server version")
      if (!hasProperty(ParticleProperty.COLORABLE)) throw new ParticleColorException("This particle is not colorable")
      if (!isColorCorrect(this, color)) throw new ParticleColorException("The particle color type is incorrect")
      new ParticlePacket(this, color, range > 256).sendTo(center, range)
    }

    /**
      * Displays a single particle which is colored and only visible for the specified players
      *
      * @param color Color of the particle
      * @param center Center location of the effect
      * @param players Receivers of the effect
      * @throws ParticleVersionException If the particle effect is not supported by the server version
      * @throws ParticleColorException If the particle effect is not colorable or the color type is incorrect
      * @see `ParticlePacket#ParticlePacket(ParticleEffect, ParticleColor, boolean)`
      * @see `ParticlePacket#sendTo(Location, List)`
      */
    @throws(classOf[ParticleVersionException])
    @throws(classOf[ParticleDataException])
    def display(color: ParticleColor, center: Location, players: List[Player]): Unit = {
      if (!isSupported) throw new ParticleVersionException("This particle effect is not supported by your server version")
      if (!hasProperty(ParticleProperty.COLORABLE)) throw new ParticleColorException("This particle is not colorable")
      if (!isColorCorrect(this, color)) throw new ParticleColorException("The particle color type is incorrect")
      new ParticlePacket(this, color, isLongDistance(center, players)).sendTo(center, players)
    }

    /**
      * Displays a single particle which is colored and only visible for the specified players
      *
      * @param color Color of the particle
      * @param center Center location of the effect
      * @param players Receivers of the effect
      * @throws ParticleVersionException If the particle effect is not supported by the server version
      * @throws ParticleColorException If the particle effect is not colorable or the color type is incorrect
      * @see `#display(ParticleColor, Location, List)`
      */
    @throws(classOf[ParticleVersionException])
    @throws(classOf[ParticleDataException])
    def display(color: ParticleColor, center: Location, players: Player*): Unit = display(color, center, players.toList)

    /**
      * Displays a particle effect which requires additional data and is only visible for all players within a certain range in the world of @param center
      *
      * @param data Data of the effect
      * @param offsetX Maximum distance particles can fly away from the center on the x-axis
      * @param offsetY Maximum distance particles can fly away from the center on the y-axis
      * @param offsetZ Maximum distance particles can fly away from the center on the z-axis
      * @param speed Display speed of the particles
      * @param amount Amount of particles
      * @param center Center location of the effect
      * @param range Range of the visibility
      * @throws ParticleVersionException If the particle effect is not supported by the server version
      * @throws ParticleDataException If the particle effect does not require additional data or if the data type is incorrect
      * @see `ParticlePacket`
      * @see `ParticlePacket#sendTo(Location, double)`
      */
    @throws(classOf[ParticleVersionException])
    @throws(classOf[ParticleDataException])
    def display(data: ParticleData, offsetX: Float, offsetY: Float, offsetZ: Float, speed: Float, amount: Int, center: Location, range: Double): Unit = {
      if (!isSupported) throw new ParticleVersionException("This particle effect is not supported by your server version")
      if (!hasProperty(ParticleProperty.REQUIRES_DATA)) throw new ParticleDataException("This particle effect does not require additional data")
      if (!isDataCorrect(this, data)) throw new ParticleDataException("The particle data type is incorrect")
      new ParticlePacket(this, offsetX, offsetY, offsetZ, speed, amount, range > 256, Some(data)).sendTo(center, range)
    }

    /**
      * Displays a particle effect which requires additional data and is only visible for the specified players
      *
      * @param data Data of the effect
      * @param offsetX Maximum distance particles can fly away from the center on the x-axis
      * @param offsetY Maximum distance particles can fly away from the center on the y-axis
      * @param offsetZ Maximum distance particles can fly away from the center on the z-axis
      * @param speed Display speed of the particles
      * @param amount Amount of particles
      * @param center Center location of the effect
      * @param players Receivers of the effect
      * @throws ParticleVersionException If the particle effect is not supported by the server version
      * @throws ParticleDataException If the particle effect does not require additional data or if the data type is incorrect
      * @see `ParticlePacket`
      * @see `ParticlePacket#sendTo(Location, List)`
      */
    @throws(classOf[ParticleVersionException])
    @throws(classOf[ParticleDataException])
    def display(data: ParticleData, offsetX: Float, offsetY: Float, offsetZ: Float, speed: Float, amount: Int, center: Location, players: List[Player]): Unit = {
      if (!isSupported) throw new ParticleVersionException("This particle effect is not supported by your server version")
      if (!hasProperty(ParticleProperty.REQUIRES_DATA)) throw new ParticleDataException("This particle effect does not require additional data")
      if (!isDataCorrect(this, data)) throw new ParticleDataException("The particle data type is incorrect")
      new ParticlePacket(this, offsetX, offsetY, offsetZ, speed, amount, isLongDistance(center, players), Some(data)).sendTo(center, players)
    }

    /**
      * Displays a particle effect which requires additional data and is only visible for the specified players
      *
      * @param data Data of the effect
      * @param offsetX Maximum distance particles can fly away from the center on the x-axis
      * @param offsetY Maximum distance particles can fly away from the center on the y-axis
      * @param offsetZ Maximum distance particles can fly away from the center on the z-axis
      * @param speed Display speed of the particles
      * @param amount Amount of particles
      * @param center Center location of the effect
      * @param players Receivers of the effect
      * @throws ParticleVersionException If the particle effect is not supported by the server version
      * @throws ParticleDataException If the particle effect does not require additional data or if the data type is incorrect
      * @see `#display(ParticleData, float, float, float, float, int, Location, List)`
      */
    @throws(classOf[ParticleVersionException])
    @throws(classOf[ParticleDataException])
    def display(data: ParticleData, offsetX: Float, offsetY: Float, offsetZ: Float, speed: Float, amount: Int, center: Location, players: Player*): Unit = display(data, offsetX, offsetY, offsetZ, speed, amount, center, players.toList)

    /**
      * Displays a single particle which requires additional data that flies into a determined direction and is only visible for all players within a certain range in the world of @param center
      *
      * @param data Data of the effect
      * @param direction Direction of the particle
      * @param speed Display speed of the particles
      * @param center Center location of the effect
      * @param range Range of the visibility
      * @throws ParticleVersionException If the particle effect is not supported by the server version
      * @throws ParticleDataException If the particle effect does not require additional data or if the data type is incorrect
      * @see `ParticlePacket`
      * @see `ParticlePacket#sendTo(Location, double)`
      */
    @throws(classOf[ParticleVersionException])
    @throws(classOf[ParticleDataException])
    def display(data: ParticleData, direction: Vector, speed: Float, center: Location, range: Double): Unit = {
      if (!isSupported) throw new ParticleVersionException("This particle effect is not supported by your server version")
      if (!hasProperty(ParticleProperty.REQUIRES_DATA)) throw new ParticleDataException("This particle effect does not require additional data")
      if (!isDataCorrect(this, data)) throw new ParticleDataException("The particle data type is incorrect")
      new ParticlePacket(this, direction, speed, range > 256, Some(data)).sendTo(center, range)
    }

    /**
      * Displays a single particle which requires additional data that flies into a determined direction and is only visible for the specified players
      *
      * @param data Data of the effect
      * @param direction Direction of the particle
      * @param speed Display speed of the particles
      * @param center Center location of the effect
      * @param players Receivers of the effect
      * @throws ParticleVersionException If the particle effect is not supported by the server version
      * @throws ParticleDataException If the particle effect does not require additional data or if the data type is incorrect
      * @see `ParticlePacket`
      * @see `ParticlePacket#sendTo(Location, List)`
      */
    def display(data: ParticleData, direction: Vector, speed: Float, center: Location, players: List[Player]): Unit = {
      if (!isSupported) throw new ParticleVersionException("This particle effect is not supported by your server version")
      if (!hasProperty(ParticleProperty.REQUIRES_DATA)) throw new ParticleDataException("This particle effect does not require additional data")
      if (!isDataCorrect(this, data)) throw new ParticleDataException("The particle data type is incorrect")
      new ParticlePacket(this, direction, speed, isLongDistance(center, players), Some(data)).sendTo(center, players)
    }

    /**
      * Displays a single particle which requires additional data that flies into a determined direction and is only visible for the specified players
      *
      * @param data Data of the effect
      * @param direction Direction of the particle
      * @param speed Display speed of the particles
      * @param center Center location of the effect
      * @param players Receivers of the effect
      * @throws ParticleVersionException If the particle effect is not supported by the server version
      * @throws ParticleDataException If the particle effect does not require additional data or if the data type is incorrect
      * @see `#display(ParticleData, Vector, float, Location, List)`
      */
    def display(data: ParticleData, direction: Vector, speed: Float, center: Location, players: Player*): Unit = display(data, direction, speed, center, players.toList)
  }
  protected final def PEValue(name: String, particleID: Int, requiredVersion: Int, properties: ParticleProperty.Value*): Value = PEVal(name, particleID, requiredVersion, properties:_*)
  implicit def valueToPEValue(v: Value): PEVal = v.asInstanceOf[PEVal]

  private final val NAME_MAP: mutable.HashMap[String, ParticleEffect.PEVal] = new mutable.HashMap[String, ParticleEffect.PEVal]()
  private final val ID_MAP: mutable.HashMap[Int, ParticleEffect.PEVal] = new mutable.HashMap[Int, ParticleEffect.PEVal]()

  for (effect <- values) {
    NAME_MAP += ((effect.name, effect))
    ID_MAP += ((effect.particleID, effect))
  }

  /**
    * Returns the particle effect with the given name
    *
    * @param name Name of the particle effect
    * @return The particle effect
    */
  def fromName(name: String): Option[PEVal] = NAME_MAP collectFirst { case (k, v) if k.equalsIgnoreCase(name) => v }

  /**
    * Returns the particle effect with the given id
    *
    * @param id Id of the particle effect
    * @return The particle effect
    */
  def fromID(id: Int): Option[PEVal] = ID_MAP collectFirst { case (k, v) if k == id => v }

  /**
    * Determine if water is at a certain location
    *
    * @param loc Location to check
    * @return Whether water is at this location or not
    */
  private def isWater(loc: Location): Boolean = loc.getBlock.getType == Material.WATER || loc.getBlock.getType == Material.STATIONARY_WATER

  /**
    * Determine if the distance between @param location and one of the players exceeds 256
    *
    * @param loc Location to check
    * @param players Players to check
    * @return Whether the distance exceeds 256 or not
    */
  private def isLongDistance(loc: Location, players: List[Player]): Boolean = players exists(p => loc.getWorld.getName == p.getLocation.getWorld.getName || p.getLocation.distanceSquared(loc) > 65536)

  /**
    * Determine if the data type for a particle effect is correct
    *
    * @param effect Particle effect
    * @param data Particle data
    * @return Whether the data type is correct or not
    */
  private def isDataCorrect(effect: PEVal, data: ParticleData): Boolean = ((effect == BLOCK_CRACK || effect == BLOCK_DUST) && data.isInstanceOf[BlockData]) || (effect == ITEM_CRACK && data.isInstanceOf[ItemData])

  /**
    * Determine if the color type for a particle effect is correct
    *
    * @param effect Particle effect
    * @param color Particle color
    * @return Whether the color type is correct or not
    */
  private def isColorCorrect(effect: PEVal, color: ParticleColor): Boolean = ((effect == SPELL_MOB || effect == SPELL_MOB_AMBIENT || effect == REDSTONE) && color.isInstanceOf[OrdinaryColor]) || (effect == NOTE && color.isInstanceOf[NoteColor])

  /**
    * Represents the property of a particle effect
    *
    * This class is part of the '''ParticleEffect Library''' and follows the same usage conditions
    *
    * Rewritten in Scala by TheReturningVoid
    *
    * @author DarkBlade12, TheReturningVoid
    * @since 1.7
    */
  object ParticleProperty extends Enumeration {
    final val REQUIRES_WATER, REQUIRES_DATA, DIRECTIONAL, COLORABLE = Value
  }

  /**
    * Represents the particle data for effects like `ParticleEffect.ITEM_CRACK`, `ParticleEffect.BLOCK_CRACK` and `ParticleEffect.BLOCK_DUST`
    *
    * This class is part of the '''ParticleEffect Library''' and follows the same usage conditions
    *
    * Rewritten in Scala by TheReturningVoid
    *
    * @constructor Construct a new particle data
    * @param material Material of the item/block
    * @param data Data value of the item/block
    *
    * @author DarkBlade12, TheReturningVoid
    * @since 1.6
    */
  abstract class ParticleData(val material: Material, val data: Byte) {
    final val packetData: Array[Int] = Array[Int](material.getId, data)

    /**
      * Returns the data as a string for pre 1.8 versions
      *
      * @return The data string for the packet
      */
    def getPacketDataString: String = "_" + packetData(0) + "_" + packetData(1)
  }

  /**
    * Represents the item data for the `ParticleEffect.ITEM_CRACK` effect
    *
    * This class is part of the '''ParticleEffect Library''' and follows the same usage conditions
    *
    * Rewritten in Scala by TheReturningVoid
    *
    * @constructor Construct a new item data
    * @param material Material of the item
    * @param data Data value of the item
    * @see `ParticleData#ParticleData(Material, byte)`
    *
    * @author DarkBlade12, TheReturningVoid
    * @since 1.6
    */
  final class ItemData(material: Material, data: Byte) extends ParticleData(material, data)

  /**
    * Represents the block data for the `ParticleEffect.BLOCK_CRACK` and `ParticleEffect.BLOCK_DUST` effects
    *
    * This class is part of the '''ParticleEffect Library''' and follows the same usage conditions
    *
    * Rewritten in Scala by TheReturningVoid
    *
    * @constructor Construct a new block data
    * @param material Material of the block
    * @param data Data value of the block
    * @throws IllegalArgumentException If the material is not a block
    * @see `ParticleData#ParticleData(Material, byte)`
    *
    * @author DarkBlade12, TheReturningVoid
    * @since 1.6
    */
  @throws(classOf[IllegalArgumentException])
  final class BlockData(material: Material, data: Byte) extends ParticleData(material, data) {
    if (!material.isBlock) throw new IllegalArgumentException("The material is not a block!")
  }

  /**
    * Represents the color for effects like `ParticleEffect.SPELL_MOB`, `ParticleEffect.SPELL_MOB_AMBIENT`, `ParticleEffect.REDSTONE` and `ParticleEffect.NOTE`
    *
    * This class is part of the '''ParticleEffect Library''' and follows the same usage conditions
    *
    * Rewritten in Scala by TheReturningVoid
    *
    * @author DarkBlade12, TheReturningVoid
    * @since 1.7
    */
  abstract class ParticleColor {
    /**
      * Returns the value for the offsetX field
      *
      * @return The offsetX value
      */
    def getValueX: Float

    /**
      * Returns the value for the offsetY field
      *
      * @return The offsetY value
      */
    def getValueY: Float

    /**
      * Returns the value for the offsetZ field
      *
      * @return The offsetZ value
      */
    def getValueZ: Float
  }

  /**
    * Represents the color for effects like `ParticleEffect.SPELL_MOB`, `ParticleEffect.SPELL_MOB_AMBIENT` and `ParticleEffect.REDSTONE`
    *
    * This class is part of the '''ParticleEffect Library''' and follows the same usage conditions
    *
    * Rewritten in Scala by TheReturningVoid
    *
    * @constructor Construct a new ordinary color
    * @param red Red value of the RGB format
    * @param green Green value of the RGB format
    * @param blue Blue value of the RGB format
    * @throws IllegalArgumentException If one of the values is lower than 0 or higher than 255
    *
    * @author DarkBlade12, TheReturningVoid
    * @since 1.7
    */
  @throws(classOf[IllegalArgumentException])
  final class OrdinaryColor(val red: Int, val green: Int, val blue: Int) extends ParticleColor {
    if (red < 0) {
      throw new IllegalArgumentException("The red value is lower than 0")
    }
    if (red > 255) {
      throw new IllegalArgumentException("The red value is higher than 255")
    }
    if (green < 0) {
      throw new IllegalArgumentException("The green value is lower than 0")
    }
    if (green > 255) {
      throw new IllegalArgumentException("The green value is higher than 255")
    }
    if (blue < 0) {
      throw new IllegalArgumentException("The blue value is lower than 0")
    }
    if (blue > 255) {
      throw new IllegalArgumentException("The blue value is higher than 255")
    }

    /**
      * Construct a new ordinary color
      *
      * @param color Bukkit color
      */
    def this(color: Color) = this(color.getRed, color.getGreen, color.getBlue)

    /**
      * Returns the red value divided by 255
      *
      * @return The offsetX value
      */
    override def getValueX: Float = red / 255F

    /**
      * Returns the green value divided by 255
      *
      * @return The offsetY value
      */
    override def getValueY: Float = green / 255F

    /**
      * Returns the blue value divided by 255
      *
      * @return The offsetZ value
      */
    override def getValueZ: Float = blue / 255F
  }

  /**
    * Represents the color for the `ParticleEffect.NOTE` effect
    *
    * This class is part of the '''ParticleEffect Library''' and follows the same usage conditions
    *
    * Rewritten in Scala by TheReturningVoid
    *
    * @constructor Construct a new note color
    * @param note Note id which determines color
    * @throws IllegalArgumentException If the note value is lower than 0 or higher than 24
    *
    * @author DarkBlade12, TheReturningVoid
    * @since 1.7
    */
  @throws(classOf[IllegalArgumentException])
  final class NoteColor(note: Int) extends ParticleColor {
    if (note < 0) {
      throw new IllegalArgumentException("The note value is lower than 0")
    }
    if (note > 24) {
      throw new IllegalArgumentException("The note value is higher than 24")
    }

    /**
      * Returns the note value divided by 24
      *
      * @return The offsetX value
      */
    override def getValueX: Float = note / 24F

    /**
      * Returns zero because the offsetY value is unused
      *
      * @return zero
      */
    override def getValueY: Float = 0

    /**
      * Returns zero because the offsetZ value is unused
      *
      * @return zero
      */
    override def getValueZ: Float = 0
  }

  /**
    * Represents a runtime exception that is thrown either if the displayed particle effect requires data and has none or vice-versa or if the data type is incorrect
    *
    * This class is part of the '''ParticleEffect Library''' and follows the same usage conditions
    *
    * Rewritten in Scala by TheReturningVoid
    *
    * @constructor Construct a new particle data exception
    * @param message Message that will be logged
    *
    * @author DarkBlade12, TheReturningVoid
    * @since 1.6
    */
  @SerialVersionUID(3203085387160737484L)
  final class ParticleDataException(message: String) extends RuntimeException(message)

  /**
    * Represents a runtime exception that is thrown either if the displayed particle effect is not colorable or if the
    * particle color type is incorrect
    *
    * This class is part of the '''ParticleEffect Library''' and follows the same usage conditions
    *
    * Rewritten in Scala by TheReturningVoid
    *
    * @constructor Construct a new particle color exception
    * @param message Message that will be logged
    *
    * @author DarkBlade12, TheReturningVoid
    * @since 1.7
    */
  @SerialVersionUID(3203085387160737484L)
  final class ParticleColorException(message: String) extends RuntimeException(message)

  /**
    * Represents a runtime exception that is thrown if the displayed particle effect requires a newer version
    *
    * This class is part of the '''ParticleEffect Library''' and follows the same usage conditions
    *
    * Rewritten in Scala by TheReturningVoid
    *
    * @constructor Construct a new particle version exception
    * @param message Message that will be logged
    *
    * @author DarkBlade12, TheReturningVoid
    * @since 1.6
    */
  @SerialVersionUID(3203085387160737484L)
  final class ParticleVersionException(message: String) extends RuntimeException(message)

  /**
    * Represents a particle effect packet with all attributes which is used for sending packets to the players
    *
    * This class is part of the '''ParticleEffect Library''' and follows the same usage conditions
    *
    * Rewritten in Scala by TheReturningVoid
    *
    * @constructor Construct a new particle packet
    * @param effect Particle effect
    * @param offsetX Maximum distance particles can fly away from the center on the x-axis
    * @param offsetY Maximum distance particles can fly away from the center on the y-axis
    * @param offsetZ Maximum distance particles can fly away from the center on the z-axis
    * @param speed Display speed of the particles
    * @param amount Amount of particles
    * @param longDistance Indicates whether the maximum distance is increased from 256 to 65536
    * @param data Data of the effect
    * @throws IllegalArgumentException If the speed or amount is lower than 0
    *
    * @author DarkBlade12, TheReturningVoid
    * @since 1.5
    */
  @throws(classOf[IllegalArgumentException])
  final class ParticlePacket(effect: ParticleEffect.PEVal, var offsetX: Float, offsetY: Float, offsetZ: Float, speed: Float, amount: Int, longDistance: Boolean, data: Option[ParticleData]) {
    import ParticlePacket._
    if (speed < 0) throw new IllegalArgumentException("The speed cannot be lower than 0")
    if (amount < 0) throw new IllegalArgumentException("The amount cannot be lower than 0")

    /**
      * Construct a new particle packet of a single particle flying into a determined direction
      *
      * @param effect Particle effect
      * @param direction Direction of the particle
      * @param speed Display speed of the particle
      * @param longDistance Indicates whether the maximum distance is increased from 256 to 65536
      * @param data Data of the effect
      * @throws IllegalArgumentException If the speed is lower than 0
      * @see `#ParticleEffect(ParticleEffect, float, float, float, float, int, boolean, ParticleData)`
      */
    @throws(classOf[IllegalArgumentException])
    def this(effect: ParticleEffect.PEVal, direction: Vector, speed: Float, longDistance: Boolean, data: Option[ParticleData]) =
      this(effect, direction.getX.toFloat, direction.getY.toFloat, direction.getZ.toFloat, speed, 0, longDistance, data)

    /**
      * Construct a new particle packet of a single colored particle
      *
      * @param effect Particle effect
      * @param color Color of the particle
      * @param longDistance Indicates whether the maximum distance is increased from 256 to 65536
      * @see `#ParticleEffect(ParticleEffect, float, float, float, float, int, boolean, ParticleData)`
      */
    def this(effect: ParticleEffect.PEVal, color: ParticleColor, longDistance: Boolean) = {
      this(effect, color.getValueX, color.getValueY, color.getValueZ, 1, 0, longDistance, None)
      if (effect == REDSTONE && color.isInstanceOf[OrdinaryColor] && color.asInstanceOf[OrdinaryColor].red == 0) offsetX = java.lang.Float.MIN_NORMAL
    }

    /**
      * Initializes `ParticlePacket.packet` with all set values
      *
      * @param center Center location of the effect
      * @throws PacketInstantiationException If instantion fails due to an unknown error
      */
    @throws(classOf[PacketInstantiationException])
    def initializePacket(center: Location): Unit = {
      if (packet.isDefined) return
      try {
        packet = Some(packetConstructor.newInstance())
        if (version < 8) {
          val name: String = if (data.isDefined) effect.name + data.get.getPacketDataString else effect.name
          ReflectionUtils.setValue(packet.get, true, "a", name)
        } else {
          ReflectionUtils.setValue(packet.get, true, "a", particleEnum.get.getEnumConstants()(effect.particleID))
          ReflectionUtils.setValue(packet.get, true, "j", longDistance)
          if (data.isDefined) ReflectionUtils.setValue(packet.get, true, "k", if (effect == ITEM_CRACK) data.get.packetData else Array(data.get.packetData(0) | (data.get.packetData(1) << 12)))
        }
        ReflectionUtils.setValue(packet.get, true, "b", center.getX.toFloat)
        ReflectionUtils.setValue(packet.get, true, "c", center.getY.toFloat)
        ReflectionUtils.setValue(packet.get, true, "d", center.getZ.toFloat)
        ReflectionUtils.setValue(packet.get, true, "e", offsetX)
        ReflectionUtils.setValue(packet.get, true, "f", offsetY)
        ReflectionUtils.setValue(packet.get, true, "g", offsetZ)
        ReflectionUtils.setValue(packet.get, true, "h", speed)
        ReflectionUtils.setValue(packet.get, true, "i", amount)
      } catch {
        case e: Exception => throw new PacketInstantiationException("Packet instantiation failed", e)
      }
    }

    /**
      * Sends the packet to a single player and caches it
      *
      * @param center Center location of the effect
      * @param player Receiver of the packet
      * @throws PacketInstantiationException If instantion fails due to an unknown error
      * @throws PacketSendingException If sending fails due to an unknown error
      * @see `#initializePacket(Location)`
      */
    @throws(classOf[PacketInstantiationException])
    @throws(classOf[PacketSendingException])
    def sendTo(center: Location, player: Player): Unit = {
      initializePacket(center)
      try {
        sendPacket.invoke(playerConnection.get(getHandle.invoke(player)), packet.get.asInstanceOf[AnyRef])
      } catch {
        case e: Exception => throw new PacketSendingException("Failed to send packet to player '" + player.getName + "'", e)
      }
    }

    /**
      * Sends the packet to all players in the list
      *
      * @param center Center location of the effect
      * @param players Receivers of the packet
      * @throws IllegalArgumentException If the player list is empty
      * @see `#sendTo(Location center, Player player)`
      */
    @throws(classOf[IllegalArgumentException])
    def sendTo(center: Location, players: List[Player]): Unit = if (players.nonEmpty) players foreach(p => sendTo(center, p)) else throw new IllegalArgumentException("The player list is empty!")

    /**
      * Sends the packet to all players in a certain range
      *
      * @param center Center location of the effect
      * @param range Range in which players will receive the packet (Maximum range for particles is usually 16, but it can differ for some types)
      * @throws IllegalArgumentException If the range is lower than 1
      * @see `#sendTo(Location center, Player player)`
      */
    @throws(classOf[IllegalArgumentException])
    def sendTo(center: Location, range: Double): Unit = if (range > 1) getOnlinePlayers foreach { case p if p.getWorld.getName == center.getWorld.getName || p.getLocation.distanceSquared(center) > range * range => sendTo(center, p) } else throw new IllegalArgumentException("The range cannot be lower than 1")

    /**
      * Gets a list of all online players.
      *
      * This alternate method is used as there are two `Bukkit.getOnlinePlayers` methods; one that returns an `Array`,
      * and another returning a `java.util.Collection`. The Scala compiler cannot figure out which method to use, and
      * fails to compile as a result.
      *
      * @return List of online players
      */
    private def getOnlinePlayers: List[Player] = Bukkit.getWorlds.toList.flatMap(w => w.getPlayers)
  }

  /**
    * Companion object for the ParticlePacket class.
    *
    * @author TheReturningVoid
    * @since 1.7
    */
  object ParticlePacket {
    private lazy val packetClass: Class[_] = tryInit(PackageType.MINECRAFT_SERVER.getClassByName(if (version < 7) "Packet63WorldParticles" else "PacketPlayOutWorldParticles"))
    lazy val version: Int = tryInit(PackageType.getServerVersion.charAt(3).toInt)
    lazy val particleEnum: Option[Class[_]] = tryInit(if (version > 7) Some(PackageType.MINECRAFT_SERVER.getClassByName("EnumParticle")) else None)
    lazy val packetConstructor: Constructor[_] = tryInit(ReflectionUtils.getConstructor(packetClass))
    lazy val getHandle: Method = tryInit(ReflectionUtils.getMethod("CraftPlayer", PackageType.CRAFTBUKKIT_ENTIITY, "getHandle"))
    lazy val playerConnection: Field = tryInit(ReflectionUtils.getField("EntityPlayer", PackageType.MINECRAFT_SERVER, false, "playerConnection"))
    lazy val sendPacket: Method = tryInit(ReflectionUtils.getMethod(playerConnection.getType, "sendPacket", PackageType.MINECRAFT_SERVER.getClassByName("Packet")))
    var packet: Option[Any] = None

    /**
      * Attempt to initialize a variable that could throw an exception. If they throw an exception, then it is caught
      * and a `VersionIncompatibleException is thrown.
      *
      * @param init The variable to initialize
      * @tparam T The type of the desired value
      * @return The value if an exception was not thrown, otherwise it throws a `VersionIncompatibleException`.
      */
    @throws(classOf[VersionIncompatibleException])
    private def tryInit[T](init: => T): T = Try(init) match {
      case Failure(e) => throw new VersionIncompatibleException("Your version of Bukkit seems to be incompatible with this version of ParticleEffect.", e)
      case Success(v) => v
    }
  }

  /**
    * Represents a runtime exception that is thrown if a bukkit version is not compatible with this library
    *
    * This class is part of the '''ParticleEffect Library''' and follows the same usage conditions
    *
    * Rewritten in Scala by TheReturningVoid
    *
    * @constructor Construct a new version incompatible exception
    * @param message Message that will be logged
    * @param cause Cause of the exception
    *
    * @author DarkBlade12, TheReturningVoid
    * @since 1.5
    */
  @SerialVersionUID(3203085387160737484L)
  final class VersionIncompatibleException(message: String, cause: Throwable) extends RuntimeException(message, cause)

  /**
    * Represents a runtime exception that is thrown if packet instantiation fails
    *
    * This class is part of the '''ParticleEffect Library''' and follows the same usage conditions
    *
    * Rewritten in Scala by TheReturningVoid
    *
    * @constructor Construct a new packet instantiation exception
    * @param message Message that will be logged
    * @param cause Cause of the exception
    *
    * @author DarkBlade12, TheReturningVoid
    * @since 1.4
    */
  @SerialVersionUID(3203085387160737484L)
  final class PacketInstantiationException(message: String, cause: Throwable) extends RuntimeException(message, cause)

  /**
    * Represents a runtime exception that is thrown if packet sending fails
    *
    * This class is part of the '''ParticleEffect Library''' and follows the same usage conditions
    *
    * Rewritten in Scala by TheReturningVoid
    *
    * @constructor Construct a new packet sending exception
    * @param message Message that will be logged
    * @param cause Cause of the exception
    *
    * @author DarkBlade12, TheReturningVoid
    * @since 1.4
    */
  @SerialVersionUID(3203085387160737484L)
  final class PacketSendingException(message: String, cause: Throwable) extends RuntimeException(message, cause)

}
