build:
	@sbt compile

run:
	sbt run

assembly:
	#sbt assembly

package:
	@sbt package

docker:


clean:
	@sbt clean

fmt:
	@sbt scalafmt

unit-test:
	sbt test

integration-test:
	sbt it:test

test-all: clean fmt unit-test integration-test
	sbt doc

benchmark:

