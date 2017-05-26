/**
 * Created by Minjung on 2016-04-12.
 */
import java.io.File;


public class QuadTreeNode implements Comparable<QuadTreeNode> {
    String name;
    double ullat, ullon, lrlat, lrlon;
    QuadTreeNode nw, ne, sw, se;
    int depth;

    public QuadTreeNode(String name, double ullat, double ullon, double lrlat,
                        double lrlon, int depth) {
        String c1, c2, c3, c4;
        this.name = name + ".png";
        this.ullat = ullat;
        this.ullon = ullon;
        this.lrlat = lrlat;
        this.lrlon = lrlon;
        this.depth = depth;
        if (name.equals("root")) {
            c1 = "1";
            c2 = "2";
            c3 = "3";
            c4 = "4";
        } else {
            c1 = name + "1";
            c2 = name + "2";
            c3 = name + "3";
            c4 = name + "4";
        }
        this.nw = constructorHelper(c1, ullat, ullon, (ullat + lrlat) / 2,
                (ullon + lrlon) / 2, depth + 1);
        this.ne = constructorHelper(c2, ullat, (ullon + lrlon) / 2, (ullat + lrlat) / 2,
                lrlon, depth + 1);
        this.sw = constructorHelper(c3, (ullat + lrlat) / 2, ullon, lrlat,
                (ullon + lrlon) / 2, depth + 1);
        this.se = constructorHelper(c4, (ullat + lrlat) / 2, (ullon + lrlon) / 2, lrlat,
                lrlon, depth + 1);
    }
    public QuadTreeNode constructorHelper(String childName, double ulLat, double ulLon,
                                          double lrLat, double lrLon, int d) {
        String c1, c2, c3, c4;
        File file = new File("img/" + childName + ".png");
        if (!file.exists()) {
            return null;
        }
        return new QuadTreeNode(childName, ulLat, ulLon, lrLat, lrLon, d);
    }

    public double getDepth() {
        return (lrlon - ullon) / 256;
    }

    public boolean intersects(double qullat, double qullon, double qlrlat, double qlrlon) {
        if (lrlon < qullon || ullon > qlrlon || lrlat > qullat || ullat < qlrlat) {
            return false;
        }
        return true;
    }

    public int compareTo(QuadTreeNode qn) {
        if (ullat == qn.ullat) {
            if (ullon < qn.ullon) {
                return -1;
            } else {
                return 1;
            }
        } else if (ullat > qn.ullat) {
            return -1;
        } else {
            return 1;
        }
    }
}
