import java.util.concurrent.atomic.AtomicReferenceArray;
import java.util.concurrent.atomic.AtomicStampedReference;

/**
 * Created by shunlv on 16-3-7.
 */
public class ArrayLockFreeQueue implements LockFreeQueue {
  private AtomicReferenceArray<Node> referenceArray;
  private AtomicStampedReference<Integer> head;
  private AtomicStampedReference<Integer> tail;

  private final int len;

  public ArrayLockFreeQueue(int len) {
    this.len = len;
    this.head = new AtomicStampedReference<>(0, 0);
    this.tail = new AtomicStampedReference<>(0, 0);

    this.referenceArray = new AtomicReferenceArray<>(len);
  }

  @Override
  public Node enq(Object value) {
    boolean isFull;

    int originStamp;
    int originTail;

    int nextStamp;
    int nextTail;

    Node originNode;
    Node node = new Node(value);
    do {
      originStamp = tail.getStamp();
      originTail = tail.getReference();

      nextStamp = originStamp + 1;
      nextTail = (originTail + 1) % len;

      originNode = referenceArray.get(nextTail);
    } while (!(isFull = isFull())
        && !(tail.compareAndSet(originTail, nextTail, originStamp, nextStamp)
        && referenceArray.compareAndSet(nextTail, originNode, node)) // todo fix ABA
        );

    if (isFull) {
      return null;
    }

    return node;
  }

  @Override
  public Node deq() {
    boolean isEmpty;

    int originHeadStamp;
    int originHead;

    int nextHeadStamp;
    int nextHead;

    Node nextHeadNode;
    do {
      originHeadStamp = head.getStamp();
      originHead = head.getReference();

      nextHeadStamp = originHeadStamp + 1;
      nextHead = (originHead + 1) % len;

      nextHeadNode = referenceArray.get(nextHead);
    } while (!(isEmpty = isEmpty()) && !head.compareAndSet(originHead, nextHead, originHeadStamp, nextHeadStamp));

    if (isEmpty) {
      return null;
    }

    return nextHeadNode;
  }

  @Override
  public int getSize() {
    int head = this.head.getReference();
    int tail = this.tail.getReference();

    if (tail >= head) {
      return tail - head;
    }

    return len - (head - tail);
  }

  @Override
  public boolean isEmpty() {
    return getSize() == 0;
  }

  @Override
  public boolean isFull() {
    return getSize() >= len - 1;
  }
}
