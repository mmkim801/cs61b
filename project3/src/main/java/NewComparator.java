import java.util.Comparator;

/**
 * Created by Minjung on 2016-04-18.
 */
public class NewComparator implements Comparator<Node> {

    @Override
    public int compare(Node n1, Node n2) {
        if (MapServer.distance.get(n1) + n1.euclidian < MapServer.distance.get(n2) + n2.euclidian) {
            return -1;
        } else if (MapServer.distance.get(n1) + n1.euclidian
                > MapServer.distance.get(n2) + n2.euclidian) {
            return 1;
        } else {
            return 0;
        }
    }
}
