// A Groovy solution for the Ninth Annual ICFP <http://www.boundvariable.org/task.shtml> by Jim White.

import groovy.transform.CompileStatic

import java.nio.ByteBuffer

UM.load(new File(args[0])).spin(0)

@CompileStatic
final class UM {

    final int[] registers = new int[8]
    final ArrayList<int[]> arrays = new ArrayList<int[]>()

    UM(int[] program) { arrays.add(program) }

    static UM load(File scroll) {
        def buffer = ByteBuffer.allocate(scroll.length() as int)
        def channel = new FileInputStream(scroll).channel
        def bytesRead = channel.read(buffer)
        channel.close()

        System.err.println "$bytesRead bytes read from scroll."

        buffer.flip()
        def ints = buffer.asIntBuffer()
        def platters = new int[ints.limit()]
        ints.get(platters)

        System.err.println "${platters.length} platters in scroll"

        new UM(platters)
    }

    def spin(int finger) {
        int alloc = 1
        while (true) {
//            trace(finger)
            def instruction = arrays.get(0)[finger++]
            switch (instruction >>> 28) {
                case 0 : // Conditional Move
                    if (registers[c(instruction)]) registers[a(instruction)] = registers[b(instruction)]
                    break
                case 1 : // Array Index
                    registers[a(instruction)] = arrays.get(registers[b(instruction)])[registers[c(instruction)]]
                    break
                case 2 : // Array Amend
                    arrays.get(registers[a(instruction)])[registers[b(instruction)]] = registers[c(instruction)]
                    break
                case 3 : // Addition
                    registers[a(instruction)] = registers[b(instruction)] + registers[c(instruction)]
                    break
                case 4 : // Multiplication
                    registers[a(instruction)] = (int) (ulong(registers[b(instruction)]) * ulong(registers[c(instruction)]))
                    break
                case 5 : // Division
                    registers[a(instruction)] = (int) (ulong(registers[b(instruction)]).intdiv(ulong(registers[c(instruction)])))
                    break
                case 6 : // Not-And
                    registers[a(instruction)] = ~(registers[b(instruction)] & registers[c(instruction)])
                    break
                case 7 : // Halt
                    return finger
                case 8 : // Allocation
                    // Must read registers before writing them.
                    // Otherwise if b and c refer to same register then we clobber the size with the array index.
                    def z = new int[registers[c(instruction)]]
                    while (alloc < arrays.size() && arrays.get(alloc) != null) { ++alloc }
                    registers[b(instruction)] = alloc
                    if (alloc < arrays.size()) {
                        arrays.set(alloc, z)
                    } else {
                        arrays.add(z)
                        alloc = 1
                    }
                    break
                case 9 : // Abandonment
                    arrays.set(registers[c(instruction)], null)
                    break
                case 10 : // Output
                    print(registers[c(instruction)] as char)
                    break
                case 11 : // Input
                    registers[c(instruction)] = System.in.read()
                    break
                case 12 : // Load Program
                    def x = registers[b(instruction)]
                    if (x != 0) {
                        def new_platters = arrays.get(x)
                        arrays.set(0, Arrays.copyOf(new_platters, new_platters.length))
                    }
                    finger = registers[c(instruction)]
                    break
                case 13 : // Orthography
                    registers[A(instruction)] = V(instruction)
                    break

            }
        }
    }

    def trace(int ip) {
        def op = arrays.get(0)[ip]
        System.err.println("ip=$ip op=${op >>> 28} A=${A(op)} V=${V(op)} a=${a(op)}[${registers[a(op)]}] b=${b(op)}[${registers[b(op)]}] c=${c(op)}[${registers[c(op)]}]")
    }

    static int c(int ins) { ins & 7 }
    static int b(int ins) { (ins >> 3) & 7 }
    static int a(int ins) { (ins >> 6) & 7 }

    static int A(int ins) { (ins >> 25) & 7 }
    static int V(int ins) { ins & 0x1FFFFFF }

    static long ulong(int u) { ((long) u) & 0xFFFFFFFFL }
}
