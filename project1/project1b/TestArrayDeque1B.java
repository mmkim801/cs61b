import static org.junit.Assert.*;
import org.junit.Test;

public class TestArrayDeque1B {

    public StudentArrayDeque<Integer> wrong = new StudentArrayDeque<Integer>();
    public ArrayDequeSolution<Integer> solutions = new ArrayDequeSolution<Integer>();
    FailureSequence fs = new FailureSequence();

    @Test
    public void testArray(){
        int wrongSize = wrong.size();
        int solutionsSize = solutions.size();
        for (int i = 0; i < 1000 ; i++) {
            int random = StdRandom.uniform(15);
            if (random == 0) {
                wrong.addFirst(random);
                solutions.addFirst(random);
                DequeOperation dequeOp1 = new DequeOperation("addFirst", random);
                fs.addOperation(dequeOp1);
                assertEquals(fs.toString(), solutions.get(0), wrong.get(0));

            } else if (random == 1) {
                wrong.addLast(random);
                solutions.addLast(random);
                DequeOperation dequeOp2 = new DequeOperation("addLast", random);
                fs.addOperation(dequeOp2);
                if (solutionsSize > 0) {
                    assertEquals(fs.toString(), solutions.get(solutionsSize - 1), wrong.get(wrongSize - 1));
                }

            } else if (random == 2) {
                DequeOperation dequeOp3 = new DequeOperation("removeFirst");
                fs.addOperation(dequeOp3);
                assertEquals(fs.toString(), solutions.removeFirst(), wrong.removeFirst());

            } else if (random == 3) {
                DequeOperation dequeOp4 = new DequeOperation("removeLast");
                fs.addOperation(dequeOp4);
                assertEquals(fs.toString(), solutions.removeLast(), wrong.removeLast());

            } else if (random == 4) {
                DequeOperation dequeOp5 = new DequeOperation("size");
                fs.addOperation(dequeOp5);
                assertEquals(fs.toString(), solutionsSize, wrongSize);

            } else if (random == 5) {
                DequeOperation dequeOp6 = new DequeOperation("get", 0);
                fs.addOperation(dequeOp6);
                if (solutionsSize > 0) {
                    assertEquals(fs.toString(), solutions.get(0), wrong.get(0));
                }

            }
        }
    }

    public static void main(String[] args) {
        jh61b.junit.TestRunner.runTests(TestArrayDeque1B.class);
        /**StudentArrayDeque<Integer> wrong = new StudentArrayDeque<Integer>();
         wrong.addFirst(2);
         wrong.addLast(3);
         wrong.addLast(4);
         wrong.addFirst(1);
         wrong.addFirst(6);
         assertEquals(4, (int) wrong.removeLast());
         assertEquals(null, wrong.get(4));
         assertEquals(1, (int) wrong.get(1));
         wrong.removeFirst();
         assertEquals(3, wrong.size());
         wrong.removeFirst();
         wrong.removeLast();
         assertEquals(2, (int)wrong.removeFirst());
         assertEquals(null, wrong.removeFirst());
         assertEquals(null, wrong.get(2));
         assertEquals("Wrong answer!\n" + "Number " + -1 + " not equal to " + 0 + "!\n" ,0, (int) wrong.size());*/
    }
}