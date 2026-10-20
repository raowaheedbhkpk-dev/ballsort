package com.ballsort.game;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * Depth-first solver with duplicate-state pruning. Used to make sure every
 * generated level is solvable and to give hints from the current position.
 * Run it off the UI thread.
 */
public final class Solver {

    private final int nodeLimit;
    private final HashSet<String> visited = new HashSet<>();
    private final ArrayList<int[]> path = new ArrayList<>();
    private int nodes;

    private Solver(int nodeLimit) {
        this.nodeLimit = nodeLimit;
    }

    /** Returns the list of moves {from, to} that solves the state, or null if none found within the limit. */
    public static List<int[]> solve(GameState start, int nodeLimit) {
        Solver s = new Solver(nodeLimit);
        GameState st = start.copy();
        if (st.isSolved()) return new ArrayList<>();
        return s.dfs(st) ? s.path : null;
    }

    private boolean dfs(GameState st) {
        if (st.isSolved()) return true;
        if (++nodes > nodeLimit) return false;
        if (!visited.add(st.canonicalKey())) return false;

        for (int[] m : candidateMoves(st)) {
            GameState next = st.copy();
            next.move(m[0], m[1]);
            path.add(m);
            if (dfs(next)) return true;
            path.remove(path.size() - 1);
            if (nodes > nodeLimit) return false;
        }
        return false;
    }

    private static List<int[]> candidateMoves(GameState st) {
        ArrayList<int[]> good = new ArrayList<>();
        ArrayList<int[]> toEmpty = new ArrayList<>();
        int n = st.tubeCount();
        int firstEmpty = -1;
        for (int i = 0; i < n; i++) {
            if (st.isEmpty(i)) { firstEmpty = i; break; }
        }
        for (int from = 0; from < n; from++) {
            if (st.isEmpty(from) || st.isTubeComplete(from)) continue;
            int run = st.topRun(from);
            boolean uniform = run == st.size(from);
            for (int to = 0; to < n; to++) {
                if (!st.canMove(from, to)) continue;
                if (st.isEmpty(to)) {
                    // All empty tubes are equivalent; moving a single-color tube into an empty one is pointless.
                    if (to != firstEmpty || uniform) continue;
                    toEmpty.add(new int[]{from, to});
                } else {
                    // Prefer moves that keep a full color group together.
                    int space = st.capacity - st.size(to);
                    if (space >= run) good.add(0, new int[]{from, to});
                    else good.add(new int[]{from, to});
                }
            }
        }
        good.addAll(toEmpty);
        return good;
    }
}
