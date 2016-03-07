/**
 * Created by shunlv on 16-3-7.
 */
public interface LockFreeQueue {
  Node enq(Object value);

  Node deq();

  int getSize();

  boolean isEmpty();

  boolean isFull();
}
