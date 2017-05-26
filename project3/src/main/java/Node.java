import java.util.HashSet;
import java.util.Set;

/**
 * Created by Minjung on 2016-04-15.
 */
public class Node {
    long id;
    double lat, lon, euclidian;
    Set<Node> connectionSet;

    public Node(long id, double lat, double lon, double euclidian) {
        this.id = id;
        this.lat = lat;
        this.lon = lon;
        this.euclidian = euclidian;
        connectionSet = new HashSet<>();
    }

    @Override
    public String toString() {
        return "Node{"
                + "id=" + id
                + ", lat=" + lat
                + ", lon=" + lon
                + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Node node = (Node) o;

        return id == node.id;

    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = (int) (id ^ (id >>> 32));
        temp = Double.doubleToLongBits(lat);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(lon);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }
}
