package org.ifcx.ifcp;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public final class UM {
    boolean halt;
    int ip;

    int r0;
    int r1;
    int r2;
    int r3;
    int r4;
    int r5;
    int r6;
    int r7;

    final ArrayList<int[]> arrays = new ArrayList<int[]>(40000);

    int alloc;

    Code[] program = new Code[0];

    public UM(int[] platters) {
        arrays.add(null);
        load(platters);
    }

    public void spin() {
        while (!halt) {
            program[ip++].execute(this);
        }
    }

    int getRegister(int r) {
        switch (r) {
            case 0 : return r0;
            case 1 : return r1;
            case 2 : return r2;
            case 3 : return r3;
            case 4 : return r4;
            case 5 : return r5;
            case 6 : return r6;
            case 7 : return r7;
            default:
                throw new IllegalArgumentException("Invalid register # " + r);
        }
    }

    void setRegister(int r, int v) {
        switch (r) {
            case 0 : r0 = v; break;
            case 1 : r1 = v; break;
            case 2 : r2 = v; break;
            case 3 : r3 = v; break;
            case 4 : r4 = v; break;
            case 5 : r5 = v; break;
            case 6 : r6 = v; break;
            case 7 : r7 = v; break;
            default:
                throw new IllegalArgumentException("Invalid register # " + r);
        }
    }

    int allocate(int size) {
        int[] z = new int[size];
        while (alloc < arrays.size() && arrays.get(alloc) != null) ++alloc;
        int x = alloc;
        if (x < arrays.size()) {
            arrays.set(x, z);
        } else {
            arrays.add(z);
            alloc = 1;
        }
        return x;
    }

    void deallocate(int x) {
        arrays.set(x, null);
    }

    void output(int c) {
        System.out.print((char) c);
    }

    int input() {
        try {
            return System.in.read();
        } catch (IOException e) {
            System.err.println("IOException on read: " + e.getMessage());
            return -1;
        }
    }

    void load(int x) {
        if (x != 0) {
            int[] p = arrays.get(x);
            p = Arrays.copyOf(p, p.length);
            load(p);
        }
    }

    void load(int[] p) {
        arrays.set(0, p);
        if (program.length < p.length) program = new Code[p.length];
        compile(program, p, 0, p.length);
    }


    void compile(Code[] program, int[] p, int i, int length) {
        while (i < length) {
            program[i] = CodeFactory.forOp(p[i]);
            ++i;
        }
    }

    public void halt() {
        halt = true;
    }

    void illegal() {
        int op = arrays.get(0)[ip-1];
        throw new IllegalArgumentException("Bad opcode " + (op >>> 28));
    }
}
