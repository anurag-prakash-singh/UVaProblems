import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Scanner;

/**
 * Created by anuransi on 4/16/17.
 */
class Point {
    public int x;
    public int y;

    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public double dist(Point thatPoint) {
        return Math.sqrt((x - thatPoint.x) * (x - thatPoint.x) + (y - thatPoint.y) * (y - thatPoint.y));
    }
}

class TeamFormerDP {
    private int whichBitSet(int num) {
        for (int i = 0; num <= (1 << i); i++) {
            if (num == 1 << i) return i;
        }

        // We shouldn't ever get here if num is a power of 2
        return -1;
    }

    private boolean isBitSet(int num, int bit) {
        return (num & (1 << bit)) != 0;
    }

    private int setBit(int n, int bit) {
        return n | (1 << bit);
    }

    private int unsetBit(int n, int bit) {
        return n & ~(1 << bit);
    }

    private double minCost(Point[] points, int pointsMask, int pointsPicked, double[] distanceCache) {
        // Check cache
        if (distanceCache[pointsMask] >= 0) {
            return distanceCache[pointsMask];
        }

        int numTeams = points.length;
        double minimumDistance = Double.MAX_VALUE;

        // pick points pair-wise
        for (int i = 0; i < numTeams - 1; i++) {
            for (int j = i + 1; j < numTeams; j++) {
                if (isBitSet(pointsMask, i) && isBitSet(pointsMask, j)) {
                    // calculate mask to send down
                    int maskWithoutPair = unsetBit(unsetBit(pointsMask, i), j);
                    int thisPair = setBit(setBit(0, i), j);

                    double checkDist = distanceCache[thisPair] + minCost(points, maskWithoutPair, 0, distanceCache);

                    if (checkDist < minimumDistance) {
                        minimumDistance = checkDist;
                    }
                }
            }
        }

        distanceCache[pointsMask] = minimumDistance;

        return minimumDistance;
    }

    public double minDistance(Point[] points) {
        int numTeams = points.length;
        double[] distanceCache = new double[65536];

        for (int i = 0; i < distanceCache.length; i++)
            distanceCache[i] = -1.0;

        int pointsMask = 0;

        // Set initial mask
        for (int i = 0; i < numTeams; i++) {
            pointsMask = setBit(pointsMask, i);
        }

        // calculate pair-wise distances
        for (int i = 0; i < numTeams - 1; i++) {
            for (int j = i + 1; j < numTeams; j++ ) {
                Point point1 = points[i];
                Point point2 = points[j];
                distanceCache[setBit(setBit(0, i), j)] = point1.dist(point2);
            }
        }

        double minDist = minCost(points, pointsMask, 0, distanceCache);

        return minDist;
    }

}

/**
 * Naive (non-DP) TeamFormer. An example of how not to solve the problem.
 */
class TeamFormer {
    private double currentMinDistance = Double.MAX_VALUE;

    private double calcDistance(Point[] points, int[] matchArr) {
        double distance = 0.0;

        for (int i = 0; i < matchArr.length; i++) {
            Point point1 = points[i];
            Point point2 = points[matchArr[i]];

            distance += point1.dist(point2);
        }

        return distance / 2;
    }

    private void formTeams(Point[] points, int teamNum, int[] matchArr) {
        if (teamNum == points.length - 1) {
            double distance = calcDistance(points, matchArr);

            if (distance < currentMinDistance) {
                currentMinDistance = distance;
            }

            return;
        }

        if (matchArr[teamNum] != -1) {
            formTeams(points, teamNum + 1, matchArr);

            return;
        }

        int totalTeams = points.length;

        for (int matchingTeamNum = teamNum + 1; matchingTeamNum < totalTeams; matchingTeamNum++) {
            if (matchArr[matchingTeamNum] != -1) continue;

            matchArr[matchingTeamNum] = teamNum;
            matchArr[teamNum] = matchingTeamNum;

            formTeams(points, teamNum + 1, matchArr);

            matchArr[matchingTeamNum] = -1;
            matchArr[teamNum] = -1;
        }
    }

    public double minDistance(Point[] points) {
        int numTeams = points.length;
        int[] matchArr = new int[numTeams];

        for (int i = 0; i < matchArr.length; i++) matchArr[i] = -1;

        formTeams(points, 0, matchArr);

        return currentMinDistance;
    }

}

public class Main {
    private static void dumpPoints(Point[] points) {
        System.out.println("Points:");

        for (Point point : points) {
            System.out.println(String.format("(%d, %d)", point.x, point.y));
        }
    }

    public static void main(String[] args) {
        InputStream is = System.in;

        if (args.length == 1) {
            try {
                is = new FileInputStream(args[0]);
            } catch (Exception exception) {
                System.exit(1);
            }
        }

        Scanner scanner = new Scanner(is);
        int inputNumber = 0;

        while(true) {
            int n = scanner.nextInt();

            if (n == 0) break;

            scanner.nextLine();

            inputNumber++;

            Point[] points = new Point[2 * n];

            for (int i = 0; i < 2 * n; i++) {
                String[] coords = scanner.nextLine().split(" ");

                try {
                    points[i] = new Point(Integer.parseInt(coords[1]), Integer.parseInt(coords[2]));
                } catch (NumberFormatException e) {
                    System.exit(1);
                }
            }

            TeamFormerDP teamFormerDP = new TeamFormerDP();

//            long start = System.currentTimeMillis();

//            double minDist = teamFormer.minDistance(points /*testPoints*/);
            double minDistDP = teamFormerDP.minDistance(points);

//            System.out.println("Time taken: " + (System.currentTimeMillis() - start));

//            String output = String.format("Case %d: %.2f", inputNumber, /*teamFormer.minDistance(points)*/ minDist);
//            System.out.println(output);
            String outputDP = String.format("Case %d: %.2f", inputNumber, /*teamFormer.minDistance(points)*/ minDistDP);
            System.out.println(outputDP);
        }
    }
}
