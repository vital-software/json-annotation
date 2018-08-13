package com.github.vitalsoftware.macros

import org.specs2.mutable.Specification
import play.api.libs.json._

@json case class Person(name: String, age: Int)
@jsonDefaults case class Person2(name: String, age: Int = 7)

class JsonFormatAnnotationTest extends Specification {

  "@json annotation" should {

    "create correct formatter for case class with >= 2 fields" in {

      val person = Person("Victor Hugo", 46)
      val json = Json.toJson(person)
      json === Json.obj(
        "name" -> "Victor Hugo",
        "age" -> 46
      )
      Json.fromJson[Person](json).asOpt must beSome(person)
    }
  }

  "@jsonDefaults annotation" should {

    "create correct formatter for case class with >= 2 fields" in {

      val person = Person2("Victor Hugo")
      val json = Json.toJson(person)
      json === Json.obj(
        "name" -> "Victor Hugo",
        "age" -> 7
      )
      Json.fromJson[Person2](Json.obj("name" -> "Victor Hugo")).asOpt must beSome(person)
    }
  }
}
