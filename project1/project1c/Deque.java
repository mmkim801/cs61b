public interface Deque<Item> {
    public void addFirst(Item first);
    public void addLast(Item last);
    public boolean isEmpty();
    public int size();
    public Item get(int i);
    public Item removeFirst();
    public Item removeLast();
    public void printDeque();
}