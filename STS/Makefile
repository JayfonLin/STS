# STS (for Assignment 2)
STS_SRC = src
STS_BIN = bin
STS_FILES = $(STS_SRC)/Ball.java $(STS_SRC)/Game.java $(STS_SRC)/Input.java $(STS_SRC)/Parser.java \
	    $(STS_SRC)/Pitch.java $(STS_SRC)/Player.java $(STS_SRC)/Point2D.java \
	    $(STS_SRC)/Printer.java $(STS_SRC)/STS.java $(STS_SRC)/Scanner.java \
	    $(STS_SRC)/Team.java $(STS_SRC)/Tournament.java $(STS_SRC)/Quit.java

# Put your path and emma jar file here ...
EMMA = /Users/spiros/bin/emma.jar
# Put your path and junit jar file here ...
JUNIT = /Users/spiros/bin/junit-4.7.jar

STS_CP = $(EMMA):$(JUNIT):$(STS_BIN):.
STS_ARGS = attributes.txt ts


# Compile all of the java code for STS and the GA
compile:
	javac -d $(STS_BIN) $(STS_FILES) 


# Run STS
run:
	java -ea -cp $(STS_CP) STS $(STS_ARGS)


# Instrument STS bytecode offline and run of the instrumented code
instr:
	java -ea emma instr -m overwrite -cp $(STS_BIN) STS


# Generate the html code coverage report 
html:
	java emma report -r html -sp $(STS_SRC) -in coverage.em,coverage.ec


# Remove all class files and coverage files 
clean:
	rm -rf coverage*
	rm $(STS_BIN)/*.class 


