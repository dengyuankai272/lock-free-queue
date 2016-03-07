import java.util.concurrent.atomic.AtomicReferenceArray;
import java.util.concurrent.atomic.AtomicStampedReference;

/**
 * Created by shunlv on 16-3-7.
 */
public class ArrayLockFreeQueue implements LockFreeQueue {
  private AtomicReferenceArray<AtomicStampedReference<Node>> referenceArray;
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

    Node node = new Node(value);
    AtomicStampedReference<Node> reference;

    int nextTailNodeStamp = 0;
    Node nextTailNodeValue = null;
    do {
      originStamp = tail.getStamp();
      originTail = tail.getReference();

      nextStamp = originStamp + 1;
      nextTail = (originTail + 1) % len;

      reference = referenceArray.get(nextTail);
      if (reference != null) {
        nextTailNodeStamp = reference.getStamp();
        nextTailNodeValue = reference.getReference();
      }
    } while (!(isFull = isFull())
        && !tail.compareAndSet(originTail, nextTail, originStamp, nextStamp));

    if (isFull) {
      return null;
    }

    if (reference == null) {
      reference = new AtomicStampedReference<>(node, 0);
      if (!referenceArray.compareAndSet(nextTail, null, reference)) {
        return null;
      } else {
        return node;
      }
    }

    if (!reference.compareAndSet(nextTailNodeValue, node, nextTailNodeStamp, nextTailNodeStamp + 1)) {
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

    Node nextHeadNode = null;
    do {
      originHeadStamp = head.getStamp();
      originHead = head.getReference();

      nextHeadStamp = originHeadStamp + 1;
      nextHead = (originHead + 1) % len;

      AtomicStampedReference<Node> reference = referenceArray.get(nextHead);
      if (reference != null) {
        nextHeadNode = reference.getReference();
      }
    } while (!(isEmpty = isEmpty())
        && !head.compareAndSet(originHead, nextHead, originHeadStamp, nextHeadStamp));

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
