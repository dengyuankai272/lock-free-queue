import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by shunlv on 16-3-7.
 */
public class ArrayLockFreeQueue implements LockFreeQueue {
  private Node[] nodes;
  private AtomicInteger head;
  private AtomicInteger tail;

  private final int len;
  private int size;

  public ArrayLockFreeQueue(int len) {
    this.nodes = new Node[len];
    this.head = new AtomicInteger(0);
    this.tail = new AtomicInteger(0);
    this.len = len;
    this.size = 0;
  }

  @Override
  public Node enq(Object value) {
    boolean isFull;
    int originTail;
    int nextTail;

    do {
      originTail = tail.get();
      nextTail = (originTail + 1) % len;
    } while (!(isFull = isFull()) && !compareAndSetTail(originTail, nextTail));

    if (isFull) {
      return null;
    }

    nodes[nextTail] = new Node(value);
    size++;

    return nodes[originTail];
  }

  @Override
  public Node deq() {
    boolean isEmpty;
    int originHead;
    int nextHead;

    do {
      originHead = head.get();
      nextHead = (originHead + 1) % len;
    } while (!(isEmpty = isEmpty()) && !compareAndSetHead(originHead, nextHead));

    if (isEmpty) {
      return null;
    }

    size--;

    return nodes[nextHead];
  }

  @Override
  public int getSize() {
    int head = this.head.get();
    int tail = this.tail.get();

    if (tail >= head) {
      return tail - head;
    }

    return len - (head - tail);
  }

  @Override
  public boolean isEmpty() {
    return size == 0;
  }

  @Override
  public boolean isFull() {
    return size >= len;
  }

  private boolean compareAndSetTail(int current, int replace) {
    return tail.compareAndSet(current, replace);
  }

  private boolean compareAndSetHead(int current, int replace) {
    return head.compareAndSet(current, replace);
  }
}
