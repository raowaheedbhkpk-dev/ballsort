package com.ballsort.game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

/** Creates levels from the level number. The same level number always gives the same puzzle. */
public final class LevelGenerator {

    public static final int CAPACITY = 4;
    public static final int EMPTY_TUBES = 2;
    public static final int MAX_COLORS = 12;

    private LevelGenerator() {}

    public static int colorsForLevel(int level) {
        if (level <= 2) return 3;
        if (level <= 5) return 4;
        if (level <= 10) return 5;
        if (level <= 20) return 6;
        if (level <= 35) return 7;
        if (level <= 50) return 8;
        if (level <= 80) return 9;
        if (level <= 120) return 10;
        if (level <= 180) return 11;
        return MAX_COLORS;
    }

    public static GameState generate(int level) {
        int colors = colorsForLevel(level);
        GameState last = null;
        for (int attempt = 0; attempt < 40; attempt++) {
            Random rnd = new Random(level * 7919L + attempt * 104729L + 17);
            GameState st = shuffled(colors, rnd);
            if (st == null) continue;
            last = st;
            if (Solver.solve(st, 60000) != null) return st;
        }
        return last != null ? last : shuffled(colors, new Random(level));
    }

    private static GameState shuffled(int colors, Random rnd) {
        ArrayList<Integer> pool = new ArrayList<>();
        for (int c = 0; c < colors; c++) {
            for (int k = 0; k < CAPACITY; k++) pool.add(c);
        }
        Collections.shuffle(pool, rnd);
        GameState st = new GameState(colors + EMPTY_TUBES, CAPACITY);
        int i = 0;
        for (int t = 0; t < colors; t++) {
            for (int k = 0; k < CAPACITY; k++) st.push(t, pool.get(i++));
        }
        // Reject puzzles that start with a finished tube or too many same-color stacks.
        for (int t = 0; t < colors; t++) {
            if (st.topRun(t) >= 3) return null;
        }
        return st;
    }
}
