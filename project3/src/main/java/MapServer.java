import com.google.gson.Gson;

import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.BasicStroke;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.HashSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.List;
import java.util.Collections;
import java.util.PriorityQueue;
import java.util.Base64;

import static spark.Spark.*;
/* Maven is used to pull in these dependencies. */

/**
 * This MapServer class is the entry point for running the JavaSpark web server for the BearMaps
 * application project, receiving API calls, handling the API call processing, and generating
 * requested images and routes.
 * @author Alan Yao
 */
public class MapServer {
    /**
     * The root upper left/lower right longitudes and latitudes represent the bounding box of
     * the root tile, as the images in the img/ folder are scraped.
     * Longitude == x-axis; latitude == y-axis.
     */
    public static final double ROOT_ULLAT = 37.892195547244356, ROOT_ULLON = -122.2998046875,
            ROOT_LRLAT = 37.82280243352756, ROOT_LRLON = -122.2119140625;
    /** Each tile is 256x256 pixels. */
    public static final int TILE_SIZE = 256;
    /** HTTP failed response. */
    private static final int HALT_RESPONSE = 403;
    /** Route stroke information: typically roads are not more than 5px wide. */
    public static final float ROUTE_STROKE_WIDTH_PX = 5.0f;
    /** Route stroke information: Cyan with half transparency. */
    public static final Color ROUTE_STROKE_COLOR = new Color(108, 181, 230, 200);
    /** The tile images are in the IMG_ROOT folder. */
    private static final String IMG_ROOT = "img/";
    private static QuadTree quadTree = new QuadTree();
//    private static MapDBHandler mapDB = new MapDBHandler(new GraphDB("berkeley.osm"));
    private static LinkedList<Long> shortestPath = new LinkedList<>();
    static HashMap<Node, Double> distance = new HashMap<>();
    /**
     * The OSM XML file path. Downloaded from <a href="http://download.bbbike.org/osm/">here</a>
     * using custom region selection.
     **/
    private static final String OSM_DB_PATH = "berkeley.osm";
    /**
     * Each raster request to the server will have the following parameters
     * as keys in the params map accessible by,
     * i.e., params.get("ullat") inside getMapRaster(). <br>
     * ullat -> upper left corner latitude,<br> ullon -> upper left corner longitude, <br>
     * lrlat -> lower right corner latitude,<br> lrlon -> lower right corner longitude <br>
     * w -> user viewport window width in pixels,<br> h -> user viewport height in pixels.
     **/
    private static final String[] REQUIRED_RASTER_REQUEST_PARAMS = {"ullat", "ullon", "lrlat",
        "lrlon", "w", "h"};
    /**
     * Each route request to the server will have the following parameters
     * as keys in the params map.<br>
     * start_lat -> start point latitude,<br> start_lon -> start point longitude,<br>
     * end_lat -> end point latitude, <br>end_lon -> end point longitude.
     **/
    private static final String[] REQUIRED_ROUTE_REQUEST_PARAMS = {"start_lat", "start_lon",
        "end_lat", "end_lon"};
    /* Define any static variables here. Do not define any instance variables of MapServer. */
    private static GraphDB g;

    /**
     * Place any initialization statements that will be run before the server main loop here.
     * Do not place it in the main function. Do not place initialization code anywhere else.
     * This is for testing purposes, and you may fail tests otherwise.
     **/
    public static void initialize() {
        g = new GraphDB(OSM_DB_PATH);
    }

    public static void main(String[] args) {
        initialize();
        staticFileLocation("/page");
        /* Allow for all origin requests (since this is not an authenticated server, we do not
         * care about CSRF).  */
        before((request, response) -> {
            response.header("Access-Control-Allow-Origin", "*");
            response.header("Access-Control-Request-Method", "*");
            response.header("Access-Control-Allow-Headers", "*");
        });

        /* Define the raster endpoint for HTTP GET requests. I use anonymous functions to define
         * the request handlers. */
        get("/raster", (req, res) -> {
            HashMap<String, Double> params =
                    getRequestParams(req, REQUIRED_RASTER_REQUEST_PARAMS);
            /* The png image is written to the ByteArrayOutputStream */
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            /* getMapRaster() does almost all the work for this API call */
            Map<String, Object> rasteredImgParams = getMapRaster(params, os);
            /* On an image query success, add the image data to the response */
            if (rasteredImgParams.containsKey("query_success")
                    && (Boolean) rasteredImgParams.get("query_success")) {
                String encodedImage = Base64.getEncoder().encodeToString(os.toByteArray());
                rasteredImgParams.put("b64_encoded_image_data", encodedImage);
            }
            /* Encode response to Json */
            Gson gson = new Gson();
            return gson.toJson(rasteredImgParams);
        });

        /* Define the routing endpoint for HTTP GET requests. */
        get("/route", (req, res) -> {
            HashMap<String, Double> params =
                    getRequestParams(req, REQUIRED_ROUTE_REQUEST_PARAMS);
            LinkedList<Long> route = findAndSetRoute(params);
            return !route.isEmpty();
        });

        /* Define the API endpoint for clearing the current route. */
        get("/clear_route", (req, res) -> {
            clearRoute();
            return true;
        });

        /* Define the API endpoint for search */
        get("/search", (req, res) -> {
            Set<String> reqParams = req.queryParams();
            String term = req.queryParams("term");
            Gson gson = new Gson();
            /* Search for actual location data. */
            if (reqParams.contains("full")) {
                List<Map<String, Object>> data = getLocations(term);
                return gson.toJson(data);
            } else {
                /* Search for prefix matching strings. */
                List<String> matches = getLocationsByPrefix(term);
                return gson.toJson(matches);
            }
        });

        /* Define map application redirect */
        get("/", (request, response) -> {
            response.redirect("/map.html", 301);
            return true;
        });
    }

    /**
     * Validate & return a parameter map of the required request parameters.
     * Requires that all input parameters are doubles.
     * @param req HTTP Request
     * @param requiredParams TestParams to validate
     * @return A populated map of input parameter to it's numerical value.
     */
    private static HashMap<String, Double> getRequestParams(
            spark.Request req, String[] requiredParams) {
        Set<String> reqParams = req.queryParams();
        HashMap<String, Double> params = new HashMap<>();
        for (String param : requiredParams) {
            if (!reqParams.contains(param)) {
                halt(HALT_RESPONSE, "Request failed - parameters missing.");
            } else {
                try {
                    params.put(param, Double.parseDouble(req.queryParams(param)));
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                    halt(HALT_RESPONSE, "Incorrect parameters - provide numbers.");
                }
            }
        }
        return params;
    }


    /**
     * Handles raster API calls, queries for tiles and rasters the full image. <br>
     * <p>
     *     The rastered photo must have the following properties:
     *     <ul>
     *         <li>Has dimensions of at least w by h, where w and h are the user viewport width
     *         and height.</li>
     *         <li>The tiles collected must cover the most longitudinal distance per pixel
     *         possible, while still covering less than or equal to the amount of
     *         longitudinal distance per pixel in the query box for the user viewport size. </li>
     *         <li>Contains all tiles that intersect the query bounding box that fulfill the
     *         above condition.</li>
     *         <li>The tiles must be arranged in-order to reconstruct the full image.</li>
     *         <li>If a current route exists, lines of width ROUTE_STROKE_WIDTH_PX and of color
     *         ROUTE_STROKE_COLOR are drawn between all nodes on the route in the rastered photo.
     *         </li>
     *     </ul>
     *     Additional image about the raster is returned and is to be included in the Json response.
     * </p>
     * @param params Map of the HTTP GET request's query parameters - the query bounding box and
     *               the user viewport width and height.
     * @param os     An OutputStream that the resulting png image should be written to.
     * @return A map of parameters for the Json response as specified:
     * "raster_ul_lon" -> Double, the bounding upper left longitude of the rastered image <br>
     * "raster_ul_lat" -> Double, the bounding upper left latitude of the rastered image <br>
     * "raster_lr_lon" -> Double, the bounding lower right longitude of the rastered image <br>
     * "raster_lr_lat" -> Double, the bounding lower right latitude of the rastered image <br>
     * "raster_width"  -> Integer, the width of the rastered image <br>
     * "raster_height" -> Integer, the height of the rastered image <br>
     * "depth"         -> Integer, the 1-indexed quadtree depth of the nodes of the rastered image.
     * Can also be interpreted as the length of the numbers in the image string. <br>
     * "query_success" -> Boolean, whether an image was successfully rastered. <br>
     * @see #REQUIRED_RASTER_REQUEST_PARAMS
     */
    public static Map<String, Object> getMapRaster(Map<String, Double> params, OutputStream os) {
        double zoom = (params.get("lrlon") - params.get("ullon")) / params.get("w");
        int x = 0;
        int y = 0;
        HashMap<String, Object> rasteredImageParams = new HashMap<>();
        ArrayList<QuadTreeNode> tiles = quadTree.getTiles(params.get("ullat"), params.get("ullon"),
                params.get("lrlat"), params.get("lrlon"), zoom);
        Collections.sort(tiles);
        int numOfTiles = 0;
        double compare = tiles.get(0).ullat;
        for (int i = 0; i < tiles.size(); i++) {
            if (tiles.get(i).ullat != compare) {
                break;
            }
            numOfTiles += 1;
        }
        int width = numOfTiles * 256;
        int height = tiles.size() / numOfTiles * 256;
        BufferedImage rastered = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics graph = rastered.getGraphics();
        for (QuadTreeNode nodes : tiles) {
            try {
                BufferedImage bi = ImageIO.read(new File(IMG_ROOT + nodes.name));
                graph.drawImage(bi, x, y, null);
                x += 256;
                if (x >= rastered.getWidth()) {
                    x = 0;
                    y += bi.getHeight();
                }
            } catch (IOException ioException) {
                System.out.println("Error");
            }
        }
        Stroke stroke = new BasicStroke(MapServer.ROUTE_STROKE_WIDTH_PX,
                BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
        ((Graphics2D) graph).setStroke(stroke);
        graph.setColor(ROUTE_STROKE_COLOR);
        double lonPerPix = (tiles.get(tiles.size() - 1).lrlon - tiles.get(0).ullon) / width;
        double latPerPix = (tiles.get(0).ullat - tiles.get(tiles.size() - 1).lrlat) / height;
        for (int i = 0; i < shortestPath.size() - 1; i++) {
            Node n1 = g.allNodes.get(Long.toString(shortestPath.get(i)));
            Node n2 = g.allNodes.get(Long.toString(shortestPath.get(i + 1)));
            int startX = (int) ((n1.lon - tiles.get(0).ullon) / lonPerPix);
            int startY = (int) ((tiles.get(0).ullat - n1.lat) / latPerPix);
            int endX = (int) ((n2.lon - tiles.get(0).ullon) / lonPerPix);
            int endY = (int) ((tiles.get(0).ullat - n2.lat) / latPerPix);
            graph.drawLine(startX, startY, endX, endY);
        }
        try {
            ImageIO.write(rastered, "png", os);
        } catch (IOException ioException) {
            System.out.println("Error");
        }

        rasteredImageParams.put("raster_ul_lon", tiles.get(0).ullon);
        rasteredImageParams.put("raster_ul_lat", tiles.get(0).ullat);
        rasteredImageParams.put("raster_lr_lon", tiles.get(tiles.size() - 1).lrlon);
        rasteredImageParams.put("raster_lr_lat", tiles.get(tiles.size() - 1).lrlat);
        rasteredImageParams.put("raster_width", width);
        rasteredImageParams.put("raster_height", height);
        rasteredImageParams.put("depth", tiles.get(0).depth);
        rasteredImageParams.put("query_success", true);
        return rasteredImageParams;
    }

    /**
     * Searches for the shortest route satisfying the input request parameters, sets it to be the
     * current route, and returns a <code>LinkedList</code> of the route's node ids for testing
     * purposes. <br>
     * The route should start from the closest node to the start point and end at the closest node
     * to the endpoint. Distance is defined as the euclidean between two points (lon1, lat1) and
     * (lon2, lat2).
     * @return A LinkedList of node ids from the start of the route to the end.
     */
    public static LinkedList<Long> findAndSetRoute(Map<String, Double> params) {
        PriorityQueue<Node> route = new PriorityQueue<>(new NewComparator());
        HashMap<Node, Node> prevNode = new HashMap<>();
        HashSet<Node> visited = new HashSet<>();
        distance = new HashMap<>();
        clearRoute();
        double startLat = params.get("start_lat");
        double startLon = params.get("start_lon");
        double endLat = params.get("end_lat");
        double endLon = params.get("end_lon");
        Node startNode = null;
        Node endNode = null;
        double fromStart = 100;
        double fromEnd = 100;

        for (String id : g.allNodes.keySet()) {
            Node nd = g.allNodes.get(id);
            double startDistance = distance(startLat, nd.lat, startLon, nd.lon);
            double endDistance = distance(endLat, nd.lat, endLon, nd.lon);
            if (startDistance < fromStart) {
                fromStart = startDistance;
                startNode = nd;
            }
            if (endDistance < fromEnd) {
                fromEnd = endDistance;
                endNode = nd;
            }
        }
        distance.put(startNode, 0.0);
        startNode.euclidian = distance(endLat, startNode.lat, endLon, startNode.lon);
        route.add(startNode);
        prevNode.put(startNode, null);
        Node curr = null;
        while (!route.isEmpty()) {
            curr = route.poll();
            if (curr.equals(endNode)) {
                break;
            }
            visited.add(curr);
            for (Node adj : curr.connectionSet) {
                if (!distance.containsKey(adj)) {
                    distance.put(adj, Double.POSITIVE_INFINITY);
                }
                if (!visited.contains(adj)) {
                    double newStartToNode =  distance.get(curr)
                            + distance(adj.lat, curr.lat, adj.lon, curr.lon);
                    if ((distance.get(adj) > newStartToNode)) {
//                    if (adj.startToNode > newStartToNode) {
                        distance.put(adj, newStartToNode);
                        adj.euclidian = distance(endNode.lat, adj.lat, endNode.lon, adj.lon);
                        route.add(adj);
                        prevNode.put(adj, curr);
                    }

                }
            }
        }

        while (prevNode.get(curr) != null) {
            shortestPath.addFirst(curr.id);
            curr = prevNode.get(curr);
        }
        shortestPath.addFirst(curr.id);
        return shortestPath;
    }

    /**
     * Clear the current found route, if it exists.
     */

    public static double distance(double lat1, double lat2, double lon1, double lon2) {
        return Math.sqrt((lon1 - lon2)
                * (lon1 - lon2) + (lat1 - lat2) * (lat1 - lat2));
    }

    public static void clearRoute() {
        shortestPath.clear();
    }

    /**
     * In linear time, collect all the names of OSM locations that prefix-match the query string.
     * @param prefix Prefix string to be searched for. Could be any case, with our without
     *               punctuation.
     * @return A <code>List</code> of the full names of locations whose cleaned name matches the
     * cleaned <code>prefix</code>.
     */
    public static List<String> getLocationsByPrefix(String prefix) {
        return new LinkedList<>();
    }

    /**
     * Collect all locations that match a cleaned <code>locationName</code>, and return
     * information about each node that matches.
     * @param locationName A full name of a location searched for.
     * @return A list of locations whose cleaned name matches the
     * cleaned <code>locationName</code>, and each location is a map of parameters for the Json
     * response as specified: <br>
     * "lat" -> Number, The latitude of the node. <br>
     * "lon" -> Number, The longitude of the node. <br>
     * "name" -> String, The actual name of the node. <br>
     *
     */
    public static List<Map<String, Object>> getLocations(String locationName) {
        return new LinkedList<>();
    }
}
