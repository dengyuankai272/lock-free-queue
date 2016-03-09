import sun.misc.Unsafe;

/**
 * Created by shunlv on 16-3-7.
 */
public class LinkedListLockFreeQueue implements LockFreeQueue {
  private LinkedNode head;
  private LinkedNode tail;

  private int size;

  public LinkedListLockFreeQueue() {
    // 初始化一个空节点，以防head和tail之间的竞争
    this.head = new LinkedNode(null, null, null);
    this.tail = head;
    this.size = 0;
  }

  @Override
  public Node enq(Object value) {
    LinkedNode node = new LinkedNode(value, null, null);
    LinkedNode originTail;

    do {
      originTail = tail;
      node.setPrev(originTail);
    } while (!compareAndSetTail(originTail, node));

    originTail.setNext(node);
    size++;

    return node;
  }

  @Override
  public Node deq() {
    LinkedNode originHead;
    LinkedNode nextHead;

    do {
      originHead = head;
      nextHead = (LinkedNode) originHead.getNext();
      if (nextHead == null) {
        return null;
      }
    } while (!compareAndSetHead(originHead, nextHead));

    nextHead.setPrev(null);
    originHead.setNext(null); // help GC
    size--;

    return nextHead;
  }

  @Override
  public int getSize() {
    return size;
  }

  @Override
  public boolean isEmpty() {
    return size == 0;
  }

  @Override
  public boolean isFull() {
    return false;
  }

  private boolean compareAndSetTail(LinkedNode current, LinkedNode replace) {
    return unsafe.compareAndSwapObject(this, tailOffset, current, replace);
  }

  private boolean compareAndSetHead(LinkedNode current, LinkedNode replace) {
    return unsafe.compareAndSwapObject(this, headOffset, current, replace);
  }

  private static class LinkedNode extends Node {
    public LinkedNode(Object value, Node next, Node prev) {
      super(value);
      this.next = next;
      this.prev = prev;
    }

    private Node next;
    private Node prev;

    public Node getNext() {
      return next;
    }

    public void setNext(Node next) {
      this.next = next;
    }

    public Node getPrev() {
      return prev;
    }

    public void setPrev(Node prev) {
      this.prev = prev;
    }
  }

  private static final Unsafe unsafe;

  private static final long tailOffset;
  private static final long headOffset;

  static {
    try {
      unsafe = UnsafeUtils.getUnsafe();

      tailOffset = unsafe.objectFieldOffset(LinkedListLockFreeQueue.class.getDeclaredField("tail"));
      headOffset = unsafe.objectFieldOffset(LinkedListLockFreeQueue.class.getDeclaredField("head"));
    } catch (Exception ex) {
      throw new Error(ex);
    }
  }
}
