package com.ballsort.game;

import java.util.Arrays;

/** Pure game model: tubes holding colored balls. No Android dependencies. */
public final class GameState {

    public final int capacity;
    private int[][] balls;   // balls[tube][index], index 0 = bottom
    private int[] sizes;

    public GameState(int tubeCount, int capacity) {
        this.capacity = capacity;
        this.balls = new int[tubeCount][capacity];
        this.sizes = new int[tubeCount];
    }

    private GameState(GameState o) {
        this.capacity = o.capacity;
        this.balls = new int[o.balls.length][];
        for (int i = 0; i < o.balls.length; i++) this.balls[i] = o.balls[i].clone();
        this.sizes = o.sizes.clone();
    }

    public GameState copy() {
        return new GameState(this);
    }

    public int tubeCount() {
        return sizes.length;
    }

    public int size(int tube) {
        return sizes[tube];
    }

    public int get(int tube, int index) {
        return balls[tube][index];
    }

    public void push(int tube, int color) {
        balls[tube][sizes[tube]++] = color;
    }

    public int top(int tube) {
        return sizes[tube] == 0 ? -1 : balls[tube][sizes[tube] - 1];
    }

    public boolean isEmpty(int tube) {
        return sizes[tube] == 0;
    }

    /** Number of same-colored balls on top of the tube. */
    public int topRun(int tube) {
        int n = sizes[tube];
        if (n == 0) return 0;
        int c = balls[tube][n - 1];
        int run = 1;
        for (int i = n - 2; i >= 0 && balls[tube][i] == c; i--) run++;
        return run;
    }

    public boolean isTubeComplete(int tube) {
        return sizes[tube] == capacity && topRun(tube) == capacity;
    }

    public boolean canMove(int from, int to) {
        if (from == to) return false;
        if (sizes[from] == 0 || sizes[to] >= capacity) return false;
        return sizes[to] == 0 || top(to) == top(from);
    }

    /** Moves as many matching top balls as fit. Returns the number moved (0 if illegal). */
    public int move(int from, int to) {
        if (!canMove(from, to)) return 0;
        int n = Math.min(topRun(from), capacity - sizes[to]);
        int color = top(from);
        for (int i = 0; i < n; i++) {
            sizes[from]--;
            balls[to][sizes[to]++] = color;
        }
        return n;
    }

    public boolean isSolved() {
        for (int i = 0; i < sizes.length; i++) {
            if (sizes[i] != 0 && !isTubeComplete(i)) return false;
        }
        return true;
    }

    public void addEmptyTube() {
        int n = sizes.length;
        balls = Arrays.copyOf(balls, n + 1);
        balls[n] = new int[capacity];
        sizes = Arrays.copyOf(sizes, n + 1);
    }

    /** Order-independent key so equivalent positions are only explored once by the solver. */
    public String canonicalKey() {
        String[] parts = new String[sizes.length];
        for (int i = 0; i < sizes.length; i++) {
            char[] cs = new char[sizes[i]];
            for (int j = 0; j < sizes[i]; j++) cs[j] = (char) ('a' + balls[i][j]);
            parts[i] = new String(cs);
        }
        Arrays.sort(parts);
        StringBuilder sb = new StringBuilder();
        for (String p : parts) sb.append(p).append('|');
        return sb.toString();
    }
}
