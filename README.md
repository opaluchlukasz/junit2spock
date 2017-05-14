# junit2spock

[![Build Status](https://travis-ci.org/opaluchlukasz/junit2spock.svg?branch=master)](https://travis-ci.org/opaluchlukasz/junit2spock)
[![GitHub release](https://img.shields.io/github/release/opaluchlukasz/junit2spock.svg)](https://github.com/opaluchlukasz/junit2spock/releases/latest)

### Overview
This project aims to ease transition from Junit to Spock by converting Junit based test suites to Spock specifications.
Please note that conversion is done on best effort basis and it may happen that it will produce invalid Spock spec or even invalid Groovy code.
Even if tool converts test without syntax errors it is advisable to go through generated test classes and compare them with the orginal Junit tests.

### Supported Features
* JUnit 4
  * converting Junit @Test to specification method
  * converting methods marked with @Before, @After, @BeforeClass, @AfterClass annotations to fixture methods
  * replacing assertEquals, assertFalse, assertNotNull, assertNull, assertTrue with simple comparisons in then/expect block
* Mockito
  * replacing Mockito mocks with Spock mocks
  * replacing given/willReturn, when/thenReturn for defining returned value with stubbed interaction
  * replacing when/thenThrow for defining mocked method throwing an exception with stubbed interaction
  * replacing verify with Spock interaction verification
    * supported VerificationModes: never, atLeastOnce, times, atMost, atLeast
    * supported Matchers: any, anyByte, anyChar, anyCollection, anyCollectionOf, anyDouble, anyFloat, anyInt, anyIterable, anyIterableOf, anyList, anyListOf, anyLong, anyMap, anyMapOf, anyObject, anySet, anySetOf, anyShort, anyString, eq, isA, isNotNull, isNull
  * replacing verifyNoMoreInteractions with Spock equivalent
* given/when/then blocks autodiscovery
* Groovisms - Groovy's syntactic sugar
  * removing redundant semicolon at the end of the line
  * removing redundant public keyword
  * changing quotation in String literals to single quotation mark
  * removing .class in class literals

### Run
Tool requires Java 8.

Download `junit2spock-jar-with-dependencies.jar` from [latest release](https://github.com/opaluchlukasz/junit2spock/releases/latest) and run the following command to convert Junit test classes into Spock's specs:
```
java -jar junit2spock-jar-with-dependencies.jar path_to_junit_tests output_path
```

### Build prerequisites
* Java 8
* Maven 3

### Contributions and bug reports
Contributions as well as the bug reports are very welcome.
