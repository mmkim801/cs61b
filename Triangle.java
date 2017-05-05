public class Triangle {
    public static void main(String[] args) {
       int col = 0;
       int row = 0;
       int SIZE = 5;
       while (row < SIZE) {
          while (col < row) {
          System.out.print('*');
          col = col + 1;
          }
          col =0;
          System.out.println('*');
          row = row + 1;
       }
    }
}

public class TriangleDrawer {
   public static void drawTriangle(int N) {
      int col = 0;
      int row = 0;
      while (row < N) {
         while (col < row) {
          System.out.print('*');
          col = col + 1;
         }
         col =0;
         System.out.println('*');
         row = row + 1;
      }
   }

   public static void main (String [] args){
      drawTriangle(10);
   }
}