package com.vnm;

/**
 * Created by vnm on 17-2-17.
 */
public class Edge {
    int From,to;
    double bandwidth;

    public Edge(int from, int to, double bandwidth) {
        From = from;
        this.to = to;
        this.bandwidth = bandwidth;
    }
}
