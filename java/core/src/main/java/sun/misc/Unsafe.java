package sun.misc;

/**
 * A stub class for sun.misc.Unsafe to make it possible to build protobuf only using Android SDK,
 * which lacks this class.
 *
 * At runtime, if Unsafe is provided by the OS, the real class is loaded because Java tries to
 * load a class from the parent classloader first. This class that is compiled into the protobuf lib
 * is only loaded when the Unsafe class isn't provided by the OS.
 *
 * However, this does not mean that protobuf will be actually calling empty methods in this class.
 * It relies on the existence of a static field of type Unsafe to reflectively check whether Unsafe
 * is really available or not. Since this stub class does not have the static field, protobuf does
 * not use this and falls back to the slow path.
 */
public class Unsafe {
  public byte getByte(Object o, long offset) { /* null implementation */ return 0; }
  public byte getByte(long address) { /* null implementation */ return 0; }
  public int arrayBaseOffset(Class arrayClass) { /* null implementation */ return 0; }
  public long getLong(Object o, long offset) { /* null implementation */ return 0; }
  public long getLong(long address) { /* null implementation */ return 0; }
  public long objectFieldOffset(java.lang.reflect.Field f) { /* null implementation */ return 0; }
  public void putByte(Object o, long offset, byte x) { /* null implementation */ }
  public void putByte(long address, byte x) { /* null implmentation */ }
}
