public class Palindrome{
    public static Deque<Character> wordToDeque(String word){
        Deque<Character> characterDeque = new LinkedListDequeSolution<Character>(); 
        for (int i = 0; i < word.length(); i++){ 
            char a_char = word.charAt(i);
            characterDeque.addLast(a_char);
        }
        return characterDeque;
    }
    private static boolean oldhelper(Deque deque) {
        for (int i = 0; deque.size() >= 0; i++) {
            if (deque.size() == 0 || deque.size() == 1) {
                return true;
            } else if (deque.get(0) == deque.get(deque.size() - 1)) {
                deque.removeFirst();
                deque.removeLast();
            } else {
                return false;
            }
        }
        return true;
    }

    public static boolean isPalindrome(String word) {
        Deque<Character> linkedDeque = wordToDeque(word);
        return oldhelper(linkedDeque);
    }

    private static boolean newhelper(Deque<Character> deque, CharacterComparator cc){
        for (int i = 0; deque.size() >= 0; i++) {
            if (deque.size() == 0 || deque.size() == 1) {
                return true;
            } else if (cc.equalChars(deque.get(0), deque.get(deque.size() - 1))) {
                deque.removeFirst();
                deque.removeLast();
            } else {
                return false;
            }
        }
        return true;
    }

    public static boolean isPalindrome(String word, CharacterComparator cc) {
        Deque<Character> stringTocharacter = wordToDeque(word);
        return newhelper(stringTocharacter, cc);
    }
}
