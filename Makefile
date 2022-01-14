build:
	@sbt compile

assembly:
	sbt assembly

run:
	sbt run


package:
	@sbt package

docker:

clean:
	@sbt clean

fmt:
	@sbt scalafmt

check:
	@sbt scalafmtCheck

test:
	sbt test

integration-test:
	sbt it:test

test-all: clean fmt unit-test integration-test
	sbt doc

benchmark:

