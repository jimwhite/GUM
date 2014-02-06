package org.ifcx.ifcp;

public class CodeFactoryPrototype {
    private static final int ORTHOGRAPHY_VALUE_LIMIT = 300;

    static Code[] conditional_move = {
            new Code() {
                @Override
                void execute(UM um) {
                    //To change body of implemented methods use File | Settings | File Templates.
                }
            }
    };

    static Code[] array_index;
    static Code[] array_amend;
    static Code[] addition;
    static Code[] multiplication;
    static Code[] division;
    static Code[] not_and;

    static Code[] allocation;
    static Code[] abandonment;
    static Code[] output;
    static Code[] input;
    static Code[] load;

    static Code halt = new Code() {
        @Override
        void execute(UM um) { um.halt(); }
    };

    static Code[][] orthography = {{new Code() {
        @Override
        void execute(UM um) {
            //To change body of implemented methods use File | Settings | File Templates.
        }
    }},
            {new Code() {

        @Override
        void execute(UM um) {
            //To change body of implemented methods use File | Settings | File Templates.
        }
    }}
    };

    public static Code forOp(int op) {
        switch (op >>> 28) {
            case 0 : // Conditional Move
                return conditional_move[op & 511];
            case 1 : // Array Index
                return array_index[op & 511];
            case 2 : // Array Amend
                return array_amend[op & 511];
            case 3 : // Addition
                return addition[op & 511];
            case 4 : // Multiplication
                return multiplication[op & 511];
            case 5 : // Division
                return division[op & 511];
            case 6 : // Not-And
                return not_and[op & 511];
            case 7 : // Halt
                return halt;
            case 8 : // Allocation
                return allocation[op & 63];
            case 9 : // Abandonment
                return abandonment[op & 7];
            case 10 : // Output
                return output[op & 7];
            case 11 : // Input
                return input[op & 7];
            case 12 : // Load Program
                return load[op & 63];
            case 13 : // Orthography
                final int v = op & 0x1FFFFFF;
                final int a = (op >> 25) & 7;
                if (v < ORTHOGRAPHY_VALUE_LIMIT) {
                    return orthography[a][v];
                }
                return new Code() {
                    @Override
                    void execute(UM um) { um.setRegister(a, v); }
                };
            default:
                throw new IllegalArgumentException("Bad opcode " + (op >>> 28));
        }
    }
}
