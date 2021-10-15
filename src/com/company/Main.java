package com.company;

import java.io.*;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Objects;
import java.util.stream.IntStream;

public class Main {

    private static void addCoord(ArrayList<int[]> points, String line, int verNum){
        String[] coordStr = line.split("\t");
        int[] coord = new int[3];
        coord[0] = Integer.parseInt(coordStr[0]);
        coord[1] = Integer.parseInt(coordStr[1]);
        coord[2] = verNum;
        points.add(coord);
    }

    public static void quickSort(ArrayList<int[]>[] array, int low, int high) {
        if (array.length == 0)
            return;//завершить выполнение если длина массива равна 0

        if (low >= high)
            return;//завершить выполнение если уже нечего делить

        // выбрать опорный элемент
        int middle = low + (high - low) / 2;
        int  opora = array[middle].size();

        // разделить на подмассивы, который больше и меньше опорного элемента
        int i = low, j = high;
        while (i <= j) {
            while (array[i].size() < opora) {
                i++;
            }

            while (array[j].size() > opora) {
                j--;
            }

            if (i <= j) {//меняем местами
                ArrayList temp = array[i];
                array[i] = array[j];
                array[j] = temp;
                i++;
                j--;
            }
        }

        // вызов рекурсии для сортировки левой и правой части
        if (low < j)
            quickSort(array, low, j);

        if (high > i)
            quickSort(array, i, high);
    }

    private static int dist(int[] point1, int[] point2){
        return Math.abs(point1[0] - point2[0]) + Math.abs(point1[1] - point2[1]);
    }

    private static boolean inCircle(double diam, double centerX, double centerY, int[] point){
        return (Math.pow((point[0]-centerX),2) + Math.pow((point[1]-centerY),2) < Math.pow((diam/2),2));
    }

    private static boolean inSquare(double x1, double y1, double x2, double y2, int x, int y){
        if (x > x1 && x < x2 &&
                y > y1 && y < y2)
            return true;

        return false;
    }

    private static int[][] adjMatrix(ArrayList<int[]> chosenPoints){
        int[][] mat = new int[chosenPoints.size()][chosenPoints.size()];
        for (int i = 0; i < chosenPoints.size(); i++){
            for (int j = 0; j < chosenPoints.size(); j++){
                mat[i][j] = dist(chosenPoints.get(i), chosenPoints.get(j));
            }
        }
        return mat;
    }

    public static int minKey(int key[], Boolean mstSet[])
    {
        // Initialize min value
        int min = Integer.MAX_VALUE, min_index = -1;

        for (int v = 0; v < key.length; v++)
            if (mstSet[v] == false && key[v] < min) {
                min = key[v];
                min_index = v;
            }

        return min_index;
    }

    public static void primMST(int graph[][], int parent[])
    {

        // Key values used to pick minimum weight edge in cut
        int key[] = new int[graph.length];

        // To represent set of vertices included in MST
        Boolean mstSet[] = new Boolean[graph.length];

        // Initialize all keys as INFINITE
        for (int i = 0; i < graph.length; i++) {
            key[i] = Integer.MAX_VALUE;
            mstSet[i] = false;
        }

        // Always include first 1st vertex in MST.
        key[0] = 0; // Make key 0 so that this vertex is
        // picked as first vertex
        parent[0] = -1; // First node is always root of MST

        // The MST will have V vertices
        for (int count = 0; count < graph.length - 1; count++) {
            // Pick thd minimum key vertex from the set of vertices
            // not yet included in MST
            int u = minKey(key, mstSet);

            // Add the picked vertex to the MST Set
            mstSet[u] = true;

            // Update key value and parent index of the adjacent
            // vertices of the picked vertex. Consider only those
            // vertices which are not yet included in MST
            for (int v = 0; v < graph.length; v++)

                // graph[u][v] is non zero only for adjacent vertices of m
                // mstSet[v] is false for vertices not yet included in MST
                // Update the key only if graph[u][v] is smaller than key[v]
                if (graph[u][v] != 0 && mstSet[v] == false && graph[u][v] < key[v]) {
                    parent[v] = u;
                    key[v] = graph[u][v];
                }
        }
    }

    private static int[][] kMST(ArrayList<int[]> points){
        int k = points.size()/8;
        int[][] MST = new int [k][3];
        int minWeight = Integer.MAX_VALUE;
        for (int i = 0; i < points.size(); i++) {
            for (int j = i + 1; j < points.size(); j++) {
                double diam = Math.sqrt(3)*dist(points.get(i), points.get(j));
                double centerX = (points.get(i)[0]+points.get(j)[0])/2;
                double centerY = (points.get(i)[1]+points.get(j)[1])/2;
                ArrayList<int[]> Sc = new ArrayList<>();
                for (int[] point : points) {
                    if (inCircle(diam, centerX, centerY, point)) {
                        Sc.add(point);
                    }
                }
                if (Sc.size() >= k){
                    double topLeftX = centerX - diam/2;
                    double topLeftY = centerY + diam/2;
                    ArrayList<int[]>[] squares = new ArrayList[(int) Math.pow(Math.floor(Math.sqrt(k)), 2)];
                    double side = diam/Math.sqrt(k);
                    for (int x = 0; x < Math.sqrt(k); x++) {
                        for (int y = 0; y < Math.sqrt(k); y++) {
                            squares[x * (int) Math.sqrt(k) + y] = new ArrayList<>();
                        }
                    }
                    for (int x = 0; x < Math.sqrt(k); x++) {
                        for (int y = 0; y < Math.sqrt(k); y++) {
                            squares[x * (int) Math.sqrt(k) + y] = new ArrayList<>();
                            for (int[] s : Sc) {
                                if (inSquare(topLeftX + x * side, topLeftY - (y + 1) * side, topLeftX + (x + 1) * side, topLeftY - y * side, s[0], s[1])) {
                                    squares[x * (int) Math.sqrt(k) + y].add(s);
                                }
                            }
                        }
                    }
                    quickSort(squares, 0, squares.length - 1);
                    ArrayList<int[]> chosenPoints = new ArrayList<>();
                    int count = 0;
                    for (int n = squares.length - 1; n > 0; n--){
                        if (count < k) {
                            if (count + squares[n].size() < k) {
                                chosenPoints.addAll(squares[n]);
                                count += squares[n].size();
                            }
                            else{
                                chosenPoints.addAll(squares[n].subList(0, k - count));
                                break;
                            }
                        }
                        else
                            break;
                    }
                    if (chosenPoints.size() == k) {
                        int[] parent = new int[k];
                        int[][] matrix = adjMatrix(chosenPoints);
                        primMST(matrix, parent);
                        int[][] ST = new int[k][3];
                        int weight = 0;
                        for (int n = 1; n < k; n++) {
                            ST[n][0] = chosenPoints.get(parent[n])[2];
                            ST[n][1] = chosenPoints.get(n)[2];
                            ST[n][2] = matrix[n][parent[n]];
                            weight += matrix[n][parent[n]];
                        }
                        if (weight < minWeight) {
                            MST = ST;
                            minWeight = weight;
                        }
                    }
                }
            }
        }

        return MST;
    }

    public static void main(String[] args) {
        ArrayList<int[]> points = new ArrayList<>();
        try {
            try (FileWriter writer = new FileWriter("output.txt", false)){
                File file = new File("input.txt");
                FileReader fr = new FileReader(file);
                BufferedReader reader = new BufferedReader(fr);
                String line = reader.readLine();
                int i = 0;
                while (line != null) {
                    addCoord(points, line, i+1);
                    i++;
                    line = reader.readLine();
                }
                int[][] MST = kMST(points);
                for (int j = 1; j < points.size()/8; j++){
                    writer.write(MST[j][0]+"-"+MST[j][1]+ " "+MST[j][2]+"\n");
                }
                writer.flush();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
