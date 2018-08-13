The __@json__ scala macro annotation is the quickest way to add a JSON format to your [Play](http://www.playframework.com/) project's case classes.

Project forked from Martin Raison's [Kifi macros](https://github.com/kifi/json-annotation)

## How it works

Just add ```@json``` in front of your case class definition:

```scala
import com.github.vitalsoftware.macros._

@json case class Person(name: String, age: Int)
```

You can now serialize/deserialize your objects using Play's convenience methods:

```scala
import play.api.libs.json._
val person = Person("Victor Hugo", 46)
val json = Json.toJson(person)
Json.fromJson[Person](json)
```

For Play-Json 2.6+, you can use @jsonDefaults as an equivalent to Json.using[Json.WithDefaults].format[T]. This creates a format where the Reads[T] will pull in default values from non-Option case classes. These must be static values (e.g. timestamp = DateTime.now is not a good default, as DateTime.now is a def).

```scala
@jsonDefaults case class Person(name: String, age: Int = 7)
Json.fromJson("{\"name\": \"Victor Hugo\"}")
> Person("Victor Hugo", 7)
```

## Installation

If you're using Play with SBT, you should add both the package and the "Macros Paradise" compiler plugin:

```scala
libraryDependencies += "com.github.vital-software" %% "json-annotation" % "0.6.0"
addCompilerPlugin("org.scalamacros" % "paradise" % "2.1.0" cross CrossVersion.full)
```

If you're not using Play, you will also need to add ```play-json``` to your dependencies:

```scala
resolvers += "Typesafe Repo" at "http://repo.typesafe.com/typesafe/releases/"
libraryDependencies += "com.typesafe.play" %% "play-json" % "2.6.7"
```

This library was tested with both Scala 2.11 and 2.12.
