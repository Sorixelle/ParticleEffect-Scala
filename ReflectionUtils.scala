import java.lang.reflect.{Field, Method, InvocationTargetException, Constructor}

import org.bukkit.Bukkit

import scala.collection.mutable

/**
  * '''ReflectionUtils'''
  *
  * This class provides useful methods which makes dealing with reflection much easier, especially when working with Bukkit
  *
  * You are welcome to use it, modify it and redistribute it under the following conditions:
  *  - Don't claim this class as your own
  *  - Don't remove this disclaimer
  *
  * ''It would be nice if you provide credit to me if you use this class in a published project''
  *
  * Rewritten in Scala by TheReturningVoid
  *
  * @author DarkBlade12, TheReturningVoid
  * @version 1.1
  */
object ReflectionUtils {

  /**
    * Returns the constructor of a given class with the given parameter types
    *
    * @param clazz Target class
    * @param parameterTypes Parameter types of the desired constructor
    * @return The constructor of the target class with the specified parameter types
    * @throws NoSuchMethodException If the desired constructor with the specified parameter types cannot be found
    * @see `DataType`
    * @see `DataType#getPrimitive(Array[Class[_]])`
    * @see `DataType#compare(Array[Class[_]], Array[Class[_]])`
    */
  @throws(classOf[NoSuchMethodException])
  def getConstructor(clazz: Class[_], parameterTypes: Class[_]*): Constructor[_] = clazz.getConstructors.collectFirst { case c if DataType.compare(DataType.getPrimitive(c.getParameterTypes), DataType.getPrimitive(parameterTypes.toArray)) => c} match {
    case Some(v) => v
    case None => throw new NoSuchMethodException("There is no such constructor in this class with the specified parameter types")
  }

  /**
    * Returns the constructor of a desired class with the given parameter types
    *
    * @param className Name of the desired target class
    * @param packageType Package where the desired target class is located
    * @param parameterTypes Parameter types of the desired constructor
    * @return The constructor of the desired target class with the specified parameter types
    * @throws NoSuchMethodException If the desired constructor with the specified parameter types cannot be found
    * @throws ClassNotFoundException ClassNotFoundException If the desired target class with the specified name and package cannot be found
    * @see `PackageType.PTVal#getClassByName(String)`
    * @see `#getConstructor(Class[_], Class[_]*)`
    */
  @throws(classOf[NoSuchMethodException])
  @throws(classOf[ClassNotFoundException])
  def getConstructor(className: String, packageType: PackageType.PTVal, parameterTypes: Class[_]*): Constructor[_] = getConstructor(packageType.getClassByName(className), parameterTypes:_*)

  /**
    * Returns an instance of a class with the given arguments
    *
    * @param clazz Target class
    * @param arguments Arguments which are used to construct an object of the target class
    * @return The instance of the target class with the specified arguments
    * @throws InstantiationException If you cannot create an instance of the target class due to certain circumstances
    * @throws IllegalAccessException If the desired constructor cannot be accessed due to certain circumstances
    * @throws IllegalArgumentException If the types of the arguments do not match the parameter types of the constructor (this should not occur since it searches for a constructor with the types of the arguments)
    * @throws InvocationTargetException If the desired constructor cannot be invoked
    * @throws NoSuchMethodException If the desired constructor with the specified arguments cannot be found
    */
  @throws(classOf[InstantiationException])
  @throws(classOf[IllegalAccessException])
  @throws(classOf[IllegalArgumentException])
  @throws(classOf[InvocationTargetException])
  @throws(classOf[NoSuchMethodException])
  def instantiateObject(clazz: Class[_], arguments: AnyRef*): AnyRef = getConstructor(clazz, DataType.getPrimitive(arguments.toArray):_*).newInstance(arguments).asInstanceOf[AnyRef]

  /**
    * Returns an instance of a desired class with the given arguments
    *
    * @param className Name of the desired target class
    * @param packageType Package where the desired target class is located
    * @param arguments Arguments which are used to construct an object of the desired target class
    * @return The instance of the desired target class with the specified arguments
    * @throws InstantiationException If you cannot create an instance of the desired target class due to certain circumstances
    * @throws IllegalAccessException If the desired constructor cannot be accessed due to certain circumstances
    * @throws IllegalArgumentException If the types of the arguments do not match the parameter types of the constructor (this should not occur since it searches for a constructor with the types of the arguments)
    * @throws InvocationTargetException If the desired constructor cannot be invoked
    * @throws NoSuchMethodException If the desired constructor with the specified arguments cannot be found
    * @throws ClassNotFoundException If the desired target class with the specified name and package cannot be found
    * @see `PackageType.PTVal#getClassByName(String)`
    * @see `instantiateObject(Class[_], AnyRef*)`
    */
  @throws(classOf[InstantiationException])
  @throws(classOf[IllegalAccessException])
  @throws(classOf[IllegalArgumentException])
  @throws(classOf[InvocationTargetException])
  @throws(classOf[NoSuchMethodException])
  @throws(classOf[ClassNotFoundException])
  def instantiateObject(className: String, packageType: PackageType.PTVal, arguments: AnyRef*): AnyRef = instantiateObject(packageType.getClassByName(className), arguments:_*)

  /**
    * Returns a method of a class with the given parameter types
    *
    * @param clazz Target class
    * @param methodName Name of the desired method
    * @param parameterTypes Parameter types of the desired method
    * @return The method of the target class with the specified name and parameter types
    * @throws NoSuchMethodException If the desired method of the target class with the specified name and parameter types cannot be found
    * @see `DataType#getPrimitive(Array[Class[_]])`
    * @see `DataType#compare(Array[Class[_]], Array[Class[_]])`
    */
  @throws(classOf[NoSuchMethodException])
  def getMethod(clazz: Class[_], methodName: String, parameterTypes: Class[_]*): Method = clazz.getMethods.collectFirst { case m if m.getName == methodName && DataType.compare(DataType.getPrimitive(m.getParameterTypes), DataType.getPrimitive(parameterTypes.toArray)) => m } match {
    case Some(v) => v
    case None => throw new NoSuchMethodException("There is no such method in this class with the specified name and parameter types")
  }

  /**
    * Returns a method of a desired class with the given parameter types
    *
    * @param className Name of the desired target class
    * @param packageType Package where the desired target class is located
    * @param methodName Name of the desired method
    * @param parameterTypes Parameter types of the desired method
    * @return The method of the desired target class with the specified name and parameter types
    * @throws NoSuchMethodException If the desired method of the desired target class with the specified name and parameter types cannot be found
    * @throws ClassNotFoundException If the desired target class with the specified name and package cannot be found
    * @see `PackageType.PTVal#getClassByName(String)`
    * @see `#getMethod(Class[_], String, Class[_]*)`
    */
  @throws(classOf[NoSuchMethodException])
  @throws(classOf[ClassNotFoundException])
  def getMethod(className: String, packageType: PackageType.PTVal, methodName: String, parameterTypes: Class[_]*): Method = getMethod(packageType.getClassByName(className), methodName, parameterTypes:_*)

  /**
    * Invokes a method on an object with the given arguments
    *
    * @param instance Target object
    * @param methodName Name of the desired method
    * @param arguments Arguments which are used to invoke the desired method
    * @return The result of invoking the desired method on the target object
    * @throws IllegalAccessException If the desired method cannot be accessed due to certain circumstances
    * @throws IllegalArgumentException If the types of the arguments do not match the parameter types of the method (this should not occur since it searches for a method with the types of the arguments)
    * @throws InvocationTargetException If the desired method cannot be invoked on the target object
    * @throws NoSuchMethodException If the desired method of the class of the target object with the specified name and arguments cannot be found
    * @see `#getMethod(Class[_], String, Class[_]*)`
    * @see `DataType#getPrimitive(Array[AnyRef])`
    */
  @throws(classOf[IllegalAccessException])
  @throws(classOf[IllegalArgumentException])
  @throws(classOf[InvocationTargetException])
  @throws(classOf[NoSuchMethodException])
  def invokeMethod(instance: AnyRef, methodName: String, arguments: AnyRef*): AnyRef = getMethod(instance.getClass, methodName, DataType.getPrimitive(arguments.toArray):_*).invoke(instance, arguments)

  /**
    * Invokes a method of the target class on an object with the given arguments
    *
    * @param instance Target object
    * @param clazz Target class
    * @param methodName Name of the desired method
    * @param arguments Arguments which are used to invoke the desired method
    * @return The result of invoking the desired method on the target object
    * @throws IllegalAccessException If the desired method cannot be accessed due to certain circumstances
    * @throws IllegalArgumentException If the types of the arguments do not match the parameter types of the method (this should not occur since it searches for a method with the types of the arguments)
    * @throws InvocationTargetException If the desired method cannot be invoked on the target object
    * @throws NoSuchMethodException If the desired method of the target class with the specified name and arguments cannot be found
    * @see `#getMethod(Class[_], String, Class[_]*)`
    * @see `DataType#getPrimitive(Array[AnyRef])`
    */
  @throws(classOf[IllegalAccessException])
  @throws(classOf[IllegalArgumentException])
  @throws(classOf[InvocationTargetException])
  @throws(classOf[NoSuchMethodException])
  def invokeMethod(instance: AnyRef, clazz: Class[_], methodName: String, arguments: AnyRef*): AnyRef = getMethod(clazz, methodName, DataType.getPrimitive(arguments.toArray):_*).invoke(instance, arguments)

  /**
    * Invokes a method of a desired class on an object with the given arguments
    *
    * @param instance Target object
    * @param className Name of the desired target class
    * @param packageType Package where the desired target class is located
    * @param methodName Name of the desired method
    * @param arguments Arguments which are used to invoke the desired method
    * @return The result of invoking the desired method on the target object
    * @throws IllegalAccessException If the desired method cannot be accessed due to certain circumstances
    * @throws IllegalArgumentException If the types of the arguments do not match the parameter types of the method (this should not occur since it searches for a method with the types of the arguments)
    * @throws InvocationTargetException If the desired method cannot be invoked on the target object
    * @throws NoSuchMethodException If the desired method of the desired target class with the specified name and arguments cannot be found
    * @throws ClassNotFoundException If the desired target class with the specified name and package cannot be found
    * @see `PackageType.PTVal#getClassByName(String)`
    * @see `#invokeMethod(AnyRef, Class[_], String, AnyRef*)`
    */
  @throws(classOf[InstantiationException])
  @throws(classOf[IllegalAccessException])
  @throws(classOf[IllegalArgumentException])
  @throws(classOf[InvocationTargetException])
  @throws(classOf[NoSuchMethodException])
  @throws(classOf[ClassNotFoundException])
  def invokeMethod(instance: AnyRef, className: String, packageType: PackageType.PTVal, methodName: String, arguments: AnyRef*): AnyRef = invokeMethod(instance, packageType.getClassByName(className), methodName, arguments)

  /**
    * Returns a field of the target class with the given name
    *
    * @param clazz Target class
    * @param declared Whether the desired field is declared or not
    * @param fieldName Name of the desired field
    * @return The field of the target class with the specified name
    * @throws NoSuchFieldException If the desired field of the given class cannot be found
    * @throws SecurityException If the desired field cannot be made accessible
    */
  @throws(classOf[NoSuchFieldException])
  @throws(classOf[SecurityException])
  def getField(clazz: Class[_], declared: Boolean, fieldName: String): Field = {
    val field: Field = if (declared) clazz.getDeclaredField(fieldName) else clazz.getField(fieldName)
    field.setAccessible(true)
    field
  }

  /**
    * Returns a field of a desired class with the given name
    *
    * @param className Name of the desired target class
    * @param packageType Package where the desired target class is located
    * @param declared Whether the desired field is declared or not
    * @param fieldName Name of the desired field
    * @return The field of the desired target class with the specified name
    * @throws NoSuchFieldException If the desired field of the desired class cannot be found
    * @throws SecurityException If the desired field cannot be made accessible
    * @throws ClassNotFoundException If the desired target class with the specified name and package cannot be found
    * @see `#getField(Class[_], Boolean, String)`
    */
  @throws(classOf[NoSuchFieldException])
  @throws(classOf[SecurityException])
  @throws(classOf[ClassNotFoundException])
  def getField(className: String, packageType: PackageType.PTVal, declared: Boolean, fieldName: String): Field = getField(packageType.getClassByName(className), declared, fieldName)

  /**
    * Returns the value of a field of the given class of an object
    *
    * @param instance Target object
    * @param clazz Target class
    * @param declared Whether the desired field is declared or not
    * @param fieldName Name of the desired field
    * @return The value of field of the target object
    * @throws IllegalArgumentException If the target object does not feature the desired field
    * @throws IllegalAccessException If the desired field cannot be accessed
    * @throws NoSuchFieldException If the desired field of the target class cannot be found
    * @throws SecurityException If the desired field cannot be made accessible
    * @see `#getField(Class[_], Boolean, String)`
    */
  @throws(classOf[IllegalArgumentException])
  @throws(classOf[IllegalAccessException])
  @throws(classOf[NoSuchFieldException])
  @throws(classOf[SecurityException])
  def getValue(instance: AnyRef, clazz: Class[_], declared: Boolean, fieldName: String): AnyRef = getField(clazz, declared, fieldName).get(instance)

  /**
    * Returns the value of a field of a desired class of an object
    *
    * @param instance Target object
    * @param className Name of the desired target class
    * @param packageType Package where the desired target class is located
    * @param declared Whether the desired field is declared or not
    * @param fieldName Name of the desired field
    * @return The value of field of the target object
    * @throws IllegalArgumentException If the target object does not feature the desired field
    * @throws IllegalAccessException If the desired field cannot be accessed
    * @throws NoSuchFieldException If the desired field of the desired class cannot be found
    * @throws SecurityException If the desired field cannot be made accessible
    * @throws ClassNotFoundException If the desired target class with the specified name and package cannot be found
    * @see `#getValue(AnyRef, Class[_], Boolean, String)`
    */
  @throws(classOf[IllegalArgumentException])
  @throws(classOf[IllegalAccessException])
  @throws(classOf[NoSuchFieldException])
  @throws(classOf[SecurityException])
  @throws(classOf[ClassNotFoundException])
  def getValue(instance: AnyRef, className: String, packageType: PackageType.PTVal, declared: Boolean, fieldName: String): AnyRef = getValue(instance, packageType.getClassByName(className), declared, fieldName)

  /**
    * Returns the value of a field with the given name of an object
    *
    * @param instance Target object
    * @param declared Whether the desired field is declared or not
    * @param fieldName Name of the desired field
    * @return The value of field of the target object
    * @throws IllegalArgumentException If the target object does not feature the desired field (should not occur since it searches for a field with the given name in the class of the object)
    * @throws IllegalAccessException If the desired field cannot be accessed
    * @throws NoSuchFieldException If the desired field of the target object cannot be found
    * @throws SecurityException If the desired field cannot be made accessible
    * @see `#getValue(AnyRef, Class[_], Boolean, String)`
    */
  @throws(classOf[IllegalArgumentException])
  @throws(classOf[IllegalAccessException])
  @throws(classOf[NoSuchFieldException])
  @throws(classOf[SecurityException])
  def getValue(instance: AnyRef, declared: Boolean, fieldName: String): AnyRef = getValue(instance, instance.getClass, declared, fieldName)

  /**
    * Sets the value of a field of the given class of an object
    *
    * @param instance Target object
    * @param clazz Target class
    * @param declared Whether the desired field is declared or not
    * @param fieldName Name of the desired field
    * @param value New value
    * @throws IllegalArgumentException If the type of the value does not match the type of the desired field
    * @throws IllegalAccessException If the desired field cannot be accessed
    * @throws NoSuchFieldException If the desired field of the target class cannot be found
    * @throws SecurityException If the desired field cannot be made accessible
    * @see `#getField(Class[_], Boolean, String)`
    */
  @throws(classOf[IllegalArgumentException])
  @throws(classOf[IllegalAccessException])
  @throws(classOf[NoSuchFieldException])
  @throws(classOf[SecurityException])
  def setValue(instance: Any, clazz: Class[_], declared: Boolean, fieldName: String, value: Any): Unit = getField(clazz, declared, fieldName).set(instance, value)

  /**
    * Sets the value of a field of a desired class of an object
    *
    * @param instance Target object
    * @param className Name of the desired target class
    * @param packageType Package where the desired target class is located
    * @param declared Whether the desired field is declared or not
    * @param fieldName Name of the desired field
    * @param value New value
    * @throws IllegalArgumentException If the type of the value does not match the type of the desired field
    * @throws IllegalAccessException If the desired field cannot be accessed
    * @throws NoSuchFieldException If the desired field of the desired class cannot be found
    * @throws SecurityException If the desired field cannot be made accessible
    * @throws ClassNotFoundException If the desired target class with the specified name and package cannot be found
    * @see `#setValue(Any, Class[_], Boolean, String, Any)`
    */
  @throws(classOf[IllegalArgumentException])
  @throws(classOf[IllegalAccessException])
  @throws(classOf[NoSuchFieldException])
  @throws(classOf[SecurityException])
  @throws(classOf[ClassNotFoundException])
  def setValue(instance: Any, className: String, packageType: PackageType.PTVal, declared: Boolean, fieldName: String, value: Any): Unit = setValue(instance, packageType.getClassByName(className), declared, fieldName, value)

  /**
    * Sets the value of a field with the given name of an object
    *
    * @param instance Target object
    * @param declared Whether the desired field is declared or not
    * @param fieldName Name of the desired field
    * @param value New value
    * @throws IllegalArgumentException If the type of the value does not match the type of the desired field
    * @throws IllegalAccessException If the desired field cannot be accessed
    * @throws NoSuchFieldException If the desired field of the target object cannot be found
    * @throws SecurityException If the desired field cannot be made accessible
    * @see `#setValue(Any, Class[_], Boolean, String, Any)`
    */
  @throws(classOf[IllegalArgumentException])
  @throws(classOf[IllegalAccessException])
  @throws(classOf[NoSuchFieldException])
  @throws(classOf[SecurityException])
  def setValue(instance: Any, declared: Boolean, fieldName: String, value: Any): Unit = setValue(instance, instance.getClass, declared, fieldName, value)

  /**
    * Represents an enumeration of dynamic packages of NMS and CraftBukkit
    *
    * This class is part of the '''ReflectionUtils''' and follows the same usage conditions
    *
    * Rewritten in Scala by TheReturningVoid
    *
    * @author DarkBlade12, TheReturningVoid
    * @since 1.0
    */
  object PackageType extends Enumeration {
    final val MINECRAFT_SERVER = PTValue1("minecraft_server", "net.minecraft.server." + getServerVersion)
    final val CRAFTBUKKIT = PTValue1("craftbukkit", "org.bukkit.craftbukkit." + getServerVersion)
    final val CRAFTBUKKIT_BLOCK = PTValue2("craftbukkit_block", CRAFTBUKKIT, "block")
    final val CRAFTBUKKIT_CHUNKIO = PTValue2("craftbukkit_chunkio", CRAFTBUKKIT, "chunkio")
    final val CRAFTBUKKIT_COMMAND = PTValue2("craftbukkit_command", CRAFTBUKKIT, "command")
    final val CRAFTBUKKIT_CONVERSATIONS = PTValue2("craftbukkit_conversations", CRAFTBUKKIT, "conversations")
    final val CRAFTBUKKIT_ENCHANTMENTS = PTValue2("craftbukkit_enchantments", CRAFTBUKKIT, "enchantments")
    final val CRAFTBUKKIT_ENTIITY = PTValue2("craftbukkit_entity", CRAFTBUKKIT, "entity")
    final val CRAFTBUKKIT_EVENT = PTValue2("craftbukkit_event", CRAFTBUKKIT, "event")
    final val CRAFTBUKKIT_GENERATOR = PTValue2("craftbukkit_generator", CRAFTBUKKIT, "generator")
    final val CRAFTBUKKIT_HELP = PTValue2("craftbukkit_help", CRAFTBUKKIT, "help")
    final val CRAFTBUKKIT_INVENTORY = PTValue2("craftbukkit_inventory", CRAFTBUKKIT, "inventory")
    final val CRAFTBUKKIT_MAP = PTValue2("craftbukkit_map", CRAFTBUKKIT, "map")
    final val CRAFTBUKKIT_METADATA = PTValue2("craftbukkit_metadata", CRAFTBUKKIT, "metadata")
    final val CRAFTBUKKIT_POTION = PTValue2("craftbukkit_potion", CRAFTBUKKIT, "potion")
    final val CRAFTBUKKIT_PROJECTILES = PTValue2("craftbukkit_projectiles", CRAFTBUKKIT, "projectiles")
    final val CRAFTBUKKIT_SCHEDULER = PTValue2("craftbukkit_scheduler", CRAFTBUKKIT, "scheduler")
    final val CRAFTBUKKIT_SCOREBOARD = PTValue2("craftbukkit_scoreboard", CRAFTBUKKIT, "scoreboard")
    final val CRAFTBUKKIT_UTIL = PTValue2("craftbukkit_util", CRAFTBUKKIT, "util")

    case class PTVal(name: String, path: String) extends Val(name) {
      /**
        * Returns the class with the given name
        *
        * @param className Name of the desired class
        * @return The class with the specified name
        * @throws ClassNotFoundException If the desired class with the specified name and package cannot be found
        */
      @throws(classOf[ClassNotFoundException])
      def getClassByName(className: String): Class[_] = Class.forName(this + "." + className)

      // Override for convenience
      override def toString: String = path
    }
    protected final def PTValue1(name: String, path: String): Value = PTVal(name, path)
    protected final def PTValue2(name: String, parent: PTVal, path: String): Value = PTVal(name, parent + "." + path)
    implicit def valueToPTValue1(v: Value): PTVal = v.asInstanceOf[PTVal]

    /**
      * Returns the version of your server
      *
      * @return The server version
      */
    def getServerVersion: String = Bukkit.getServer.getClass.getPackage.getName.substring(23)
  }

  /**
    * Represents an enumeration of Java data types with corresponding classes
    *
    * This class is part of the '''ReflectionUtils''' and follows the same usage conditions
    *
    * Rewritten in Scala by TheReturningVoid
    *
    * @author DarkBlade12, TheReturningVoid
    * @since 1.0
    */
  object DataType extends Enumeration {
    final val BYTE = DTValue("byte", classOf[Byte], classOf[java.lang.Byte])
    final val SHORT = DTValue("short", classOf[Short], classOf[java.lang.Short])
    final val INTEGER = DTValue("integer", classOf[Int], classOf[Integer])
    final val LONG = DTValue("long", classOf[Long], classOf[java.lang.Long])
    final val CHARACTER = DTValue("character", classOf[Char], classOf[Character])
    final val FLOAT = DTValue("float", classOf[Float], classOf[java.lang.Float])
    final val DOUBLE = DTValue("double", classOf[Double], classOf[java.lang.Double])
    final val BOOLEAN = DTValue("boolean", classOf[Long], classOf[java.lang.Boolean])

    case class DTVal(name: String, primitive: Class[_], reference: Class[_]) extends Val(name)
    protected final def DTValue(name: String, primitive: Class[_], reference: Class[_]): Value = DTVal(name, primitive, reference)
    implicit def valueToDTValue(v: Value): DTVal = v.asInstanceOf[DTVal]

    private final val CLASS_MAP: mutable.HashMap[Class[_], DTVal] = new mutable.HashMap[Class[_], DTVal]

    for (tipe <- values) {
      CLASS_MAP += ((tipe.primitive, tipe))
      CLASS_MAP += ((tipe.reference, tipe))
    }

    /**
      * Returns the data type with the given primitive/reference class
      *
      * @param clazz Primitive/Reference class of the data type
      * @return The data type
      */
    def fromClass(clazz: Class[_]): Option[DTVal] = CLASS_MAP.get(clazz)

    /**
      * Returns the primitive class of the data type with the given reference class
      *
      * @param clazz Reference class of the data type
      * @return The primitive class
      */
    def getPrimitive(clazz: Class[_]): Class[_] = if (fromClass(clazz).isDefined) fromClass(clazz).get.primitive else clazz

    /**
      * Returns the reference class of the data type with the given primitive class
      *
      * @param clazz Primitive class of the data type
      * @return The reference class
      */
    def getReference(clazz: Class[_]): Class[_] = if (fromClass(clazz).isDefined) fromClass(clazz).get.reference else clazz

    /**
      * Returns the primitive class array of the given class array
      *
      * @param classes Given class array
      * @return The primitive class array
      */
    def getPrimitive(classes: Array[Class[_]]): Array[Class[_]] = classes.map(getPrimitive)

    /**
      * Returns the reference class array of the given class array
      *
      * @param classes Given class array
      * @return The reference class array
      */
    def getReference(classes: Array[Class[_]]): Array[Class[_]] = classes.map(getReference)

    /**
      * Returns the primitive class array of the given object array
      *
      * @param anyrefs Given object array
      * @return The primitive class array
      */
    def getPrimitive(anyrefs: Array[AnyRef]): Array[Class[_]] = anyrefs.map(c => getPrimitive(c.getClass))

    /**
      * Returns the reference class array of the given object array
      *
      * @param anyrefs Given object array
      * @return The reference class array
      */
    def getReference(anyrefs: Array[AnyRef]): Array[Class[_]] = anyrefs.map(c => getReference(c.getClass))

    /**
      * Compares two class arrays on equivalence
      *
      * @param primary Primary class array
      * @param secondary Class array which is compared to the primary array
      * @return Whether these arrays are equal or not
      */
    def compare(primary: Array[Class[_]], secondary: Array[Class[_]]): Boolean = (primary zip secondary exists(c => c._1 == c._2 || c._1.isAssignableFrom(c._2))) || (primary.isEmpty && secondary.isEmpty)
  }
}
