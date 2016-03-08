import sun.misc.Unsafe;

import java.lang.reflect.Field;

/**
 * Created by shunlv on 16-3-7.
 */
public class UnsafeUtils {
  private static Unsafe unsafe;

  public static Unsafe getUnsafe() throws Exception {
    if (unsafe == null) {
      synchronized (UnsafeUtils.class) {
        if (unsafe != null) {
          return unsafe;
        }

        Field f = Unsafe.class.getDeclaredField("theUnsafe");
        f.setAccessible(true);
        unsafe = (Unsafe) f.get(null);
      }
    }

    return unsafe;
  }
}
