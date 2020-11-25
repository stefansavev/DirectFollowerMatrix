all: build

format:
	sbt scalafmt
	sbt test:scalafmt
	sbt scalastyle
		
test: format
	sbt test
build: test
	sbt package
