# Change Log
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](http://keepachangelog.com/)
and this project adheres to [Semantic Versioning](http://semver.org/).

## [Unreleased]
### Added
- `anyVararg`, `contains`, `endsWith`, `startsWith` matchers support
- `argThat`, `booleanThat`, `byteThat`, `charThat`, `doubleThat`, `floatThat`, `intThat`, `longThat`, `shortThat` matchers support (partial)
- underscore test naming convention support
- matcher support for `given/willReturn`, `when/thenReturn` Mockito methods

### Fixed 
- fix for replacing static imports with regular imports bug

## [0.1.0] - 2017-05-14
### Added
- JUnit annotations support (`@Test`, `@Before`, `@After`, `@BeforeClass`, `@AfterClass`)
- JUnit assertions support (`assertEquals`, `assertFalse`, `assertNotNull`, `assertNull`, `assertTrue`)
- Mockito (`@Mock` and `mock()` method support)
- Mockito stubbing methods (`given/willReturn`, `when/thenReturn`, `when/thenThrow`)
- Mockito verifications (verifyNoMoreInteractions and verify with `never`, `atLeastOnce`, `times`, `atMost`, `atLeast` verification modes)
- Mockito matchers support for `verify` invocations (`any`, `anyByte`, `anyChar`, `anyCollection`, `anyCollectionOf`, `anyDouble`, `anyFloat`, `anyInt`, `anyIterable`, `anyIterableOf`, `anyList`, `anyListOf`, `anyLong`, `anyMap`, `anyMapOf`, `anyObject`, `anySet`, `anySetOf`, `anyShort`, `anyString`, `eq`, `isA`, `isNotNull`, `isNull`)
- given/when/then blocks autodiscovery
- Groovisms - Groovy's syntactic sugar (redundant semicolon at the end of the line, redundant public keyword, Groovy's String literals, redundant .class in class literals)