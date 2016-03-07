import sun.misc.Unsafe;

import java.lang.reflect.Field;

/**
 * Created by shunlv on 16-3-7.
 */
public class LinkedListLockFreeQueue implements LockFreeQueue {
  private static final Unsafe unsafe;
  private LinkedNode head;
  private LinkedNode tail;

  public LinkedListLockFreeQueue() {
    // 初始化一个空节点，以防head和tail之间的竞争
    this.head = new LinkedNode(null, null, null);
    this.tail = head;
  }

  @Override
  public Node enq(Object value) {
    LinkedNode node = new LinkedNode(value, null, null);
    LinkedNode originTail;

    do {
      originTail = tail;
    } while (!compareAndSetTail(originTail, node));

    originTail.setNext(node);
    node.setPrev(originTail);

    return originTail;
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

    return nextHead;
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

  private static final long tailOffset;
  private static final long headOffset;

  static {
    try {
      Field f = Unsafe.class.getDeclaredField("theUnsafe");
      f.setAccessible(true);
      unsafe = (Unsafe) f.get(null);

      tailOffset = unsafe.objectFieldOffset(LinkedListLockFreeQueue.class.getDeclaredField("tail"));
      headOffset = unsafe.objectFieldOffset(LinkedListLockFreeQueue.class.getDeclaredField("head"));
    } catch (Exception ex) {
      throw new Error(ex);
    }
  }
}
