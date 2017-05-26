import java.util.ArrayList;

public class QuadTree {
    private  QuadTreeNode qTree;
    private double ullat;
    private double ullon;
    private double lrlat;
    private double lrlon;
    private double zoom;

    QuadTree() {
        qTree = new QuadTreeNode("root", MapServer.ROOT_ULLAT, MapServer.ROOT_ULLON,
                MapServer.ROOT_LRLAT, MapServer.ROOT_LRLON, 0);
    }

    public ArrayList<QuadTreeNode> getTiles(double ulLat, double ulLon, double lrLat,
                                            double lrLon, double dpp) {
        this.ullat = ulLat;
        this.ullon = ulLon;
        this.lrlat = lrLat;
        this.lrlon = lrLon;
        this.zoom = dpp;
        ArrayList<QuadTreeNode> tiles = new ArrayList<>();
        traverse(qTree, tiles);
        return tiles;
    }

    public void traverse(QuadTreeNode tree, ArrayList<QuadTreeNode> lst) {
        if (tree != null) {
            if (tree.intersects(ullat, ullon, lrlat, lrlon)) {
                if (tree.nw == null || tree.getDepth() <= zoom) {
                    lst.add(tree);
                    return;
                }
                traverse(tree.nw, lst);
                traverse(tree.ne, lst);
                traverse(tree.sw, lst);
                traverse(tree.se, lst);
            }
        }
    }
}
