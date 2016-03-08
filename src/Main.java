public class Main {
  public static void main(String[] args) throws Exception {
//    LockFreeQueue queue = new LinkedListLockFreeQueue();
    LockFreeQueue queue = new ArrayLockFreeQueue(10);

    Runnable in1 = new In(queue, 0);
    Runnable in2 = new In(queue, 5);
    Runnable in3 = new In(queue, 10);
    Thread inT1 = new Thread(in1);
    Thread inT2 = new Thread(in2);
    Thread inT3 = new Thread(in3);

    Runnable out = new Out(queue);
    Thread out1 = new Thread(out);
    Thread out2 = new Thread(out);
    Thread out3 = new Thread(out);

    inT1.start();
    inT2.start();
    inT3.start();

    Thread.sleep(3000);
    System.out.println();

    out1.start();
    out2.start();
    out3.start();
  }

  public static class In implements Runnable {
    private LockFreeQueue queue;
    private int start;

    public In(LockFreeQueue queue, int start) {
      this.queue = queue;
      this.start = start;
    }

    @Override
    public void run() {
      for (int i = start; i < start + 5; i++) {
        queue.enq(i);
        System.out.println(Thread.currentThread().getName() + "_" + i);
      }
    }
  }

  public static class Out implements Runnable {
    private LockFreeQueue queue;

    public Out(LockFreeQueue queue) {
      this.queue = queue;
    }

    @Override
    public void run() {
      for (int i = 0; i < 10; i++) {
        System.out.println(Thread.currentThread().getName() + "_" + queue.deq());
      }
    }
  }
}
