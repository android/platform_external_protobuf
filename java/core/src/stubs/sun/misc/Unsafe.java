package sun.misc;

/**
 * A stub class for sun.misc.Unsafe to make it possible to build protobuf only using Android SDK,
 * which lacks this class.
 *
 * Note that this class is only to provide symbol definitions at build-time and this class is not
 * embedded in the protobuf library. At runtime, Unsafe class is provided by the libcore-oj which is
 * in the bootclasspath and protobuf is linked to the class loaded there.
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
