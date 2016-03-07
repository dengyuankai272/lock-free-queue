/**
 * Created by shunlv on 16-3-7.
 */
public class ArrayLockFreeQueue implements LockFreeQueue {
  private Node[] nodes;
  private int head;
  private int tail;

  public ArrayLockFreeQueue(int size) {
    this.nodes = new Node[size];
    this.head = 0;
    this.tail = 0;
  }

  @Override
  public Node enq(Object value) {
    return null;
  }

  @Override
  public Node deq() {
    return null;
  }

  public boolean isEmpty() {
    return head == tail;
  }

  public boolean isFull() {
    // todo
    return false;
  }
}
