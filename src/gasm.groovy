import org.objectweb.asm.ClassWriter

import static org.objectweb.asm.Opcodes.*

//@Grab(group='org.ow2.asm', module='asm', version='4.1')

def cw = new ClassWriter(0)

byte[] b = cw.with {
    visit(V1_5, ACC_PUBLIC + ACC_SYNTHETIC,
            "org/ifcx/ifcp/class_xxxxx", null, "org/ifcx/ifcp/Code", null)
    visitField(ACC_PUBLIC + ACC_FINAL + ACC_STATIC, "LESS", "I", null, new Integer(-1)).visitEnd()
    visitField(ACC_PUBLIC + ACC_FINAL + ACC_STATIC, "EQUAL", "I", null, new Integer(0)).visitEnd()
    visitField(ACC_PUBLIC + ACC_FINAL + ACC_STATIC, "GREATER", "I", null, new Integer(1)).visitEnd();
    visitMethod(0, "execute", "(Lorg/ifcx/ifcp/UM;)I", null, null).visitEnd()
    visitEnd()
    toByteArray()
}

println b

class MyClassLoader extends ClassLoader {
    MyClassLoader() { super(this.getClass().getClassLoader()) }

    MyClassLoader(ClassLoader parent) { super(parent) }

    public Class defineClass(String name, byte[] b) {
        return defineClass(name, b, 0, b.length);
    }
}

def myClassLoader = new MyClassLoader(this.getClass().getClassLoader())

Class c = myClassLoader.defineClass("ifcp.Comparable", b)

println c

def i = c.newInstance()

println i
