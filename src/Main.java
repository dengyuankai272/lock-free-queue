public class Main {
  public static void main(String[] args) throws Exception {
//    LockFreeQueue queue = new LinkedListLockFreeQueue();
    LockFreeQueue queue = new ArrayLockFreeQueue(10);

    Runnable in = new In(queue);
    Thread in1 = new Thread(in);
    Thread in2 = new Thread(in);
    Thread in3 = new Thread(in);

    Runnable out = new Out(queue);
    Thread out1 = new Thread(out);
    Thread out2 = new Thread(out);
    Thread out3 = new Thread(out);

    in1.start();
    in2.start();
    in3.start();

    Thread.sleep(3000);
    System.out.println();

    out1.start();
    out2.start();
    out3.start();
  }

  public static class In implements Runnable {
    private LockFreeQueue queue;

    public In(LockFreeQueue queue) {
      this.queue = queue;
    }

    @Override
    public void run() {
      for (int i = 0; i < 5; i++) {
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
