/**
 * Created by shunlv on 16-3-7.
 */
public class Node {
  public Node(Object value) {
    this.value = value;
  }

  private Object value;

  public Object getValue() {
    return value;
  }

  public void setValue(Object value) {
    this.value = value;
  }

  @Override
  public String toString() {
    return "Node{" +
        "value=" + value +
        '}';
  }
}
