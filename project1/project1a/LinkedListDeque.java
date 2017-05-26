public class LinkedListDeque<Item> {
	private class Node {
		public Node prev; 
		public Item item;
		public Node next;

		public Node (Node p, Item i, Node n) {
			prev = p;
			item = i;
			next = n;
		}
	}

	private Node sentinel;
	private int size;

	// Creates an empty list
	public LinkedListDeque() {
		size = 0;
		sentinel = new Node (null, null, null);
		sentinel.next = sentinel;
	}

	// Adds an item to the front of the Deque
	public void addFirst(Item first) {
		sentinel.next = new Node (sentinel, first, sentinel.next);
		sentinel.next.next.prev = sentinel.next;
		size += 1;
	}

	// Adds an item to the back of the Deque
	public void addLast(Item last) {
		if (sentinel.next == sentinel) {
			sentinel.next = new Node (sentinel, last, sentinel);
			sentinel.prev = sentinel.next;
			size += 1;
		} else {
		sentinel.prev.next = new Node(sentinel.prev, last, sentinel);
		sentinel.prev = sentinel.prev.next;
		size += 1;
		}
	}

	// Returns true if deque is empty, false otherwise
	public boolean isEmpty() {
		if (sentinel.next == sentinel || sentinel.next == null){
			return true;
		} else{
			return false;
		}
	}

	// Returns the number of items in the Deque
	public int size() {
		return size;
	}

	// Prints the items in the Deque from first to last, separated by a space
	public void printDeque() {
		Node pointer = sentinel;
		while(pointer.next != sentinel){
			System.out.print(pointer.next.item + " ");
			pointer = pointer.next;
		}
	}

	// Removes and returns the item at the front of the Deque. If no such item exist, returns null
	public Item removeFirst() {
		if(isEmpty() == true) {
			return null;
		} else {
			Node removed = sentinel.next;
			sentinel.next = sentinel.next.next;
			sentinel.next.prev = sentinel;
			size -= 1;
			return removed.item;
		}
	}

	// Removes and returns the item at the back of the Deque. If no such item exists, returns null.
	public Item removeLast() {
		if(isEmpty() == true) {
			return null;
		} else {
			Node removed = sentinel.prev;
			sentinel.prev = sentinel.prev.prev;
			sentinel.prev.next = sentinel;
			size -= 1;
			return removed.item;
		}
	}

	// Gets the item at the given index, where 0 is the front. If no such item exists, returns null.
	public Item get(int index) {
		Node pointer = sentinel.next;
		if (index >= size) {
			return null;
		}
		while(index > 0) {
			pointer = pointer.next;
			index -= 1;
		}
		return pointer.item;
	}

	// Same as get, but uses recursion
	public Item getRecursive(int index){
		return helpergetRecursive(index, sentinel.next);
	}

	private  Item helpergetRecursive(int index, Node pointer){
			if (index < 0 || pointer == sentinel) {
				return null;
			} else if (index == 0) {
				return pointer.item;
			} else {
				return helpergetRecursive(index - 1, pointer.next);
			}
	}
}