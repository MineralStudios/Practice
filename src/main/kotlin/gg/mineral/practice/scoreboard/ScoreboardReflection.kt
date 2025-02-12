package gg.mineral.practice.scoreboard

import gg.mineral.practice.scoreboard.ScoreboardReflection.PacketConstructor
import org.bukkit.Bukkit
import java.lang.invoke.MethodHandles
import java.lang.invoke.MethodType
import java.util.*
import java.util.function.Predicate

/**
 * Small reflection utility class to use CraftBukkit and NMS.
 */
object ScoreboardReflection {

    private const val NM_PACKAGE = "net.minecraft"
    private const val OBC_PACKAGE = "org.bukkit.craftbukkit"
    private const val NMS_PACKAGE = "$NM_PACKAGE.server"

    private val VERSION: String = Bukkit.getServer().javaClass.packageName
        .substring(OBC_PACKAGE.length + 1)

    private val VOID_METHOD_TYPE: MethodType = MethodType.methodType(Void.TYPE)
    private val NMS_REPACKAGED: Boolean = optionalClass("$NM_PACKAGE.network.protocol.Packet").isPresent

    @Volatile
    private var theUnsafe: Any? = null

    fun isRepackaged(): Boolean = NMS_REPACKAGED

    private fun nmsClassName(post1_17package: String?, className: String): String {
        return if (NMS_REPACKAGED) {
            val classPackage = post1_17package?.let { "$NM_PACKAGE.$it" } ?: NM_PACKAGE
            "$classPackage.$className"
        } else {
            "$NMS_PACKAGE.$VERSION.$className"
        }
    }

    @Throws(ClassNotFoundException::class)
    fun nmsClass(post1_17package: String?, className: String): Class<*> =
        Class.forName(nmsClassName(post1_17package, className))

    fun nmsOptionalClass(post1_17package: String?, className: String): Optional<Class<*>> =
        optionalClass(nmsClassName(post1_17package, className))

    private fun obcClassName(className: String): String = "$OBC_PACKAGE.$VERSION.$className"

    @Throws(ClassNotFoundException::class)
    fun obcClass(className: String): Class<*> = Class.forName(obcClassName(className))

    private fun optionalClass(className: String): Optional<Class<*>> = try {
        Optional.of(Class.forName(className))
    } catch (e: ClassNotFoundException) {
        Optional.empty()
    }

    private fun enumValueOf(enumClass: Class<*>, enumName: String): Enum<*> =
        java.lang.Enum.valueOf(enumClass.asSubclass(Enum::class.java), enumName)

    fun enumValueOf(enumClass: Class<*>, enumName: String, fallbackOrdinal: Int): Any {
        return try {
            enumValueOf(enumClass, enumName)
        } catch (e: IllegalArgumentException) {
            val constants = enumClass.enumConstants
            if (constants.size > fallbackOrdinal) constants[fallbackOrdinal] else throw e
        }
    }

    @Throws(ClassNotFoundException::class)
    fun innerClass(parentClass: Class<*>, classPredicate: Predicate<Class<*>>): Class<*> {
        return parentClass.declaredClasses.firstOrNull { classPredicate.test(it) }
            ?: throw ClassNotFoundException("No class in ${parentClass.canonicalName} matches the predicate.")
    }

    @Throws(Exception::class)
    fun findPacketConstructor(packetClass: Class<*>, lookup: MethodHandles.Lookup): PacketConstructor {
        return try {
            val constructor = lookup.findConstructor(packetClass, VOID_METHOD_TYPE)
            PacketConstructor { constructor.invoke() }
        } catch (e: NoSuchMethodException) {
            if (theUnsafe == null) {
                synchronized(this) {
                    if (theUnsafe == null) {
                        val unsafeClass = Class.forName("sun.misc.Unsafe")
                        val theUnsafeField = unsafeClass.getDeclaredField("theUnsafe")
                        theUnsafeField.isAccessible = true
                        theUnsafe = theUnsafeField.get(null)
                    }
                }
            }
            val allocateMethodType = MethodType.methodType(Any::class.java, Class::class.java)
            val allocateMethod = lookup.findVirtual(theUnsafe!!::class.java, "allocateInstance", allocateMethodType)
            PacketConstructor { allocateMethod.invoke(theUnsafe, packetClass) }
        }
    }

    fun interface PacketConstructor {
        @Throws(Throwable::class)
        fun invoke(): Any
    }
} 
