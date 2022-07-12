package ar.edu.unicen.isistan.asistan.utils.geo.simplifiers;

import java.util.ArrayList;
import ar.edu.unicen.isistan.asistan.storage.database.geolocation.Coordinate;

public class DouglasPeucker {

    private double epsilon;

    public DouglasPeucker(double epsilon) {
        this.epsilon = epsilon;
    }

    public ArrayList<Coordinate> simplify(ArrayList<Coordinate> coordinates) {
        ArrayList<Coordinate> out = new ArrayList<>(coordinates);
        int auxStart = 0;
        int auxEnd = out.size()-1;
        while (auxStart != auxEnd) {
            int index = getFurthest(auxStart, auxEnd, out);
            if (index == -1) {
                for (int aux = auxStart+1; aux < auxEnd; aux++)
                    out.remove(auxStart + 1);
                auxStart = auxEnd - (auxEnd - auxStart - 1);
                auxEnd = out.size()-1;
            } else {
                auxEnd = index;
            }
        }

        return out;
    }

    private int getFurthest(int startIndex, int endIndex, ArrayList<Coordinate> coordinates) {
        double maxDist = this.epsilon;
        int outIndex = -1;

        Coordinate c1 = coordinates.get(startIndex);
        Coordinate c2 = coordinates.get(endIndex);
        double deltaLat = c2.getLatitude() - c1.getLatitude();
        double deltaLng = c2.getLongitude() - c1.getLongitude();

        for (int index = startIndex+1; index < endIndex; index++) {
            Coordinate coordinate = coordinates.get(index);
            double distance;
            if (deltaLat == 0 && deltaLng == 0) {
                distance = c1.distance(coordinate);
            } else {
                double u = (((coordinate.getLatitude() - c1.getLatitude()) * deltaLat) + ((coordinate.getLongitude() - c1.getLongitude()) * deltaLng)) / (deltaLat * deltaLat + deltaLng * deltaLng);
                Coordinate nearest;
                if (u < 0)
                    nearest = c1;
                else if (u > 1)
                    nearest = c2;
                else
                    nearest = new Coordinate(c1.getLatitude() + u * deltaLat, c1.getLongitude() + u * deltaLng);

                distance = coordinate.distance(nearest);
            }
            if (maxDist < distance) {
                outIndex = index;
                maxDist = distance;
            }
        }

        return outIndex;
    }

}
