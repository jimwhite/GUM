// A Groovy solution for the Ninth Annual ICFP <http://www.boundvariable.org/task.shtml> by Jim White.

import java.nio.ByteBuffer

import org.ifcx.ifcp.UM

load(new File(args[0])).spin()

UM load(File scroll) {
    def buffer = ByteBuffer.allocate(scroll.length() as int)
    def channel = new FileInputStream(scroll).channel
    def bytesRead = channel.read(buffer)

    System.err.println "$bytesRead bytes read from scroll."

    buffer.flip()
    def ints = buffer.asIntBuffer()
    def platters = new int[ints.limit()]
    ints.get(platters)

    channel.close()

    System.err.println "${platters.length} platters in scroll"

    new UM(platters)
}
