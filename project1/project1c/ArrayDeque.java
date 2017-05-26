public class ArrayDeque<Item> {
	private Item[] items;
	private int front;
	private int back;
	private int size;
	private static int rFACTOR = 2;

	// Creates an empty 
	public ArrayDeque() {
		size = 0;
		items = (Item []) new Object[8];
		front = 0;
		back = 1;
	}

	private int plusOne(int index) {
		index += 1;
		if (index >= items.length) {
			index = index % items.length;
		}
		return index;
	}

	private int minusOne(int index) {
		index -= 1;
		if (index < 0) {
			index =  items.length - 1;
		}
		return index;
	}

	// Adds an item to the frnot of the Deque
	public void addFirst(Item first) {
		if(size < items.length){
			items[front] = first;
		} else {
			resize(size * rFACTOR);
			items[front] = first;	
		}
		front = minusOne(front);
		size += 1;
	}

	// Adds an item to the back of the Deque
	public void addLast(Item last) {
		if (size < items.length) {	
			items[back] = last;
		} else {
			resize(size * rFACTOR);
			items[back] = last;
		}
		back = plusOne(back);
		size += 1;
	}

	// Returns true if deque is empty, false otherwise
	public boolean isEmpty() {
		if(size == 0) {
			return true;
		} else {
			return false;
		}
	}

	// Returns the number of items in the Deque
	public int size() {
		return size;
	}

	// Prints the items in the Deque from first to last, separated by a space
	public void printDeque() {
		int pointer = front;
		if (front != back) {
			while(pointer != back) {
				System.out.print(items[pointer] + " ");
				pointer = plusOne(pointer);
			}
			System.out.println(items[pointer]);
		} else {
			System.out.print(items[pointer]);
			pointer = plusOne(pointer);
			while (pointer != back) {
				System.out.println(" " + items[pointer]);
				pointer = plusOne(pointer);
			}
		}
	}

	// Removes and returns the item at the front of the Deque. If no such item exist, returns null
	public Item removeFirst() {
		Item fremoved;
		double rsize = size;
		double length = items.length;
		double percent = rsize / length;
		if(size == 0) {
			return null;
		} else {
			front = plusOne(front);
			fremoved = items[front];
			items[front] = null;
			size -= 1;
			if (length >= 16 && percent < 0.25) {
				resize(size * rFACTOR);
			}
		}
		return fremoved;
	}

	// Removes and returns the item at the back of the Deque. If no such item exists, returns null.
	public Item removeLast() {
		Item bremoved;
		double rsize = size;
		double length = items.length;
		double percent = rsize / length;
		if(size == 0) {
			return null;
		} else {
			back = minusOne(back);
			bremoved = items[back];
			items[back] = null;
			size -= 1;
			//shrink
			if (length >= 16 && percent < 0.25) {
				resize(size * rFACTOR);
			}
		}
		return bremoved;
	}
	// Gets the item at the given index, where 0 is the front. If no such item exists, returns null.
	public Item get(int index) {
		if (index > (items.length - 1)) {
			return null;
		} else {
			return items[(plusOne(front) + index) % items.length];
		}
	}

 	private void resize(int capacity) {
 		int pointer = plusOne(front);
 		Item[] resizedlst = (Item []) new Object[capacity];
 		int newsize = 0;
 		for (int i = 1; newsize < size; i++) {
 			resizedlst[i] = items[pointer];
 			pointer = plusOne(pointer);
 			newsize += 1;
 		}
 		front = 0;  
 		back = size + 1;
 		items = resizedlst;
 	}
}