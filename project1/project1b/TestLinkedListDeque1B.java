import static org.junit.Assert.*;
import org.junit.Test;

public class TestLinkedListDeque1B {
	public StudentLinkedListDeque<Integer> wrong = new StudentLinkedListDeque<Integer>();
	public LinkedListDequeSolution<Integer> solutions = new LinkedListDequeSolution<Integer>();
	FailureSequence fs = new FailureSequence();

	@Test
	public void testLinkedList(){
		for (int i = 0; i < 1000 ; i++) {
			int random = StdRandom.uniform(15);
			if (random == 0) {
				wrong.addFirst(random);
				solutions.addFirst(random);
				DequeOperation dequeOp1 = new DequeOperation("addFirst", random);
				fs.addOperation(dequeOp1);
				if (solutions.size() > 0) {
					//assertEquals(fs.toString(), solutions.get(0), wrong.get(0));
				}
			} else if (random == 1) {
				wrong.addLast(random);
				solutions.addLast(random);
				DequeOperation dequeOp2 = new DequeOperation("addLast", random);
				fs.addOperation(dequeOp2);
				if (solutions.size() > 0) {
					//assertEquals(fs.toString(), solutions.get(solutions.size() - 1), wrong.get(wrong.size() - 1));
				}
			} else if (random == 2) {
				DequeOperation dequeOp3 = new DequeOperation ("removeFirst");
				if (solutions.size() > 0) {
					fs.addOperation(dequeOp3);
					assertEquals(fs.toString(), solutions.removeFirst(), wrong.removeFirst());
				}

			} else if (random == 3) {
				DequeOperation dequeOp4 = new DequeOperation ("removeLast");
				if (solutions.size() > 0) {
					fs.addOperation(dequeOp4);
					assertEquals(fs.toString(), solutions.removeLast(), wrong.removeLast());
				}

			} else if (random == 4) {
				DequeOperation dequeOp6 = new DequeOperation("isEmpty");
				fs.addOperation(dequeOp6);
				assertEquals(solutions.isEmpty(), wrong.isEmpty());

			} else if (random == 5) {
				DequeOperation dequeOp5 = new DequeOperation ("size");
				fs.addOperation(dequeOp5);
				assertEquals(fs.toString(), solutions.size(), wrong.size());

			} else if (random == 6) {
				DequeOperation dequeOp6 = new DequeOperation ("get", 0);
				if (solutions.size() > 0) {
					fs.addOperation(dequeOp6);
					assertEquals(fs.toString(), solutions.get(0), wrong.get(0));
				}
			}
		}
	}
	public static void main(String[] args) {
		jh61b.junit.TestRunner.runTests(TestLinkedListDeque1B.class);
	}
}