package tgd.mindless.drone.weatherwidgetnumberone.redux;

enum ThemeType {
    HOURLY, DAILY
}

enum CloudCoverageLocation {
    GRAPH, TIME_BAR
}

class CloudCoverage {
    String day
    String night
    CloudCoverageLocation location
}

enum DotType {
    SOLID, RING
}

class Dot {
    String color
    BigDecimal size
    DotType type
    BigDecimal ringSize
}

class Segment {
    String color
    BigDecimal width
    BigDecimal padding
}

class Foo {
    Dot dot
    Segment segment
}

class Bar {
    String name
    Dot dot
    Segment segment
}

class Theme {
    String name
    ThemeType type
    BigDecimal fontSize
    CloudCoverage cloudCoverage
    Foo temperatureMax
    Foo temperatureMin
    Foo windSpeed
    Bar[] properties

    int getFoo() {
        return this.properties.length
    }

    def propNames = ['temperatureMax', 'temperatureMin', 'windSpeed']
    def i =0

//    Iterator myProps() {
//        def baz = propNames.findAll { this[it] }.collect { this[it] }
//
//        return [ hasNext: { i < max }, next: { i += 2 } ] as Iterator
//    }
}

