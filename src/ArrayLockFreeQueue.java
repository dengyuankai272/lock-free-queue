import java.util.concurrent.atomic.AtomicStampedReference;

/**
 * Created by shunlv on 16-3-7.
 */
public class ArrayLockFreeQueue implements LockFreeQueue {
  private Node[] nodes;
  private AtomicStampedReference<Integer> head;
  private AtomicStampedReference<Integer> tail;

  private final int len;

  public ArrayLockFreeQueue(int len) {
    this.len = len;
    this.head = new AtomicStampedReference<Integer>(0, 0);
    this.tail = new AtomicStampedReference<Integer>(0, 0);

    initNodes(len);
  }

  private void initNodes(int len) {
    nodes = new Node[len];
    for (int i = 0; i < len; i++) {
      nodes[i] = new Node(new AtomicStampedReference<Object>(null, 0));
    }
  }

  @Override
  public Node enq(Object value) {
    boolean isFull;

    int originStamp;
    int originTail;

    int nextStamp;
    int nextTail;

    Node nextTailNode;
    AtomicStampedReference nextTailValue;
    int nextTailNodeStamp;
    Object nextTailNodeValue;

    do {
      originStamp = tail.getStamp();
      originTail = tail.getReference();

      nextStamp = originStamp + 1;
      nextTail = (originTail + 1) % len;

      nextTailNode = nodes[nextTail];
      nextTailValue = (AtomicStampedReference) nextTailNode.getValue();
      nextTailNodeStamp = nextTailValue.getStamp();
      nextTailNodeValue = nextTailValue.getReference();
    } while (!(isFull = isFull()) && !tail.compareAndSet(originTail, nextTail, originStamp, nextStamp));

    if (isFull) {
      return null;
    }

    nextTailValue.compareAndSet(nextTailNodeValue, value, nextTailNodeStamp, nextTailNodeStamp + 1);

    return nextTailNode;
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

      nextHeadNode = nodes[nextHead];
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
