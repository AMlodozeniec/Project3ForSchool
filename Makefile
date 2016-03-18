JFLAGS = -g
JC = javac
.SUFFIXES: .java .class
.java.class:
	$(JC) $(JFLAGS) $*.java

CLASSES = \
    DesEncrypter.java \
	DesCounter.java \
	Cryptage.java \
	
default: classes

classes: $(CLASSES:.java=.class)

clean:
	$(RM) *.class

