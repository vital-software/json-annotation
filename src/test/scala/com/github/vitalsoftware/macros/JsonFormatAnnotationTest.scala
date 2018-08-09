package com.github.vitalsoftware.macros

import org.specs2.mutable.Specification
import play.api.libs.json._

@json case class Person(name: String, age: Int, gender: Option[String])
@jsonDefaults case class Person2(name: String, age: Int = 7, gender: Option[String] = None)
@jsonDefaults case class Test(f1: Int = 1, f2:  String = "2", f3: Boolean = true, f4: Option[Test])

class JsonFormatAnnotationTest extends Specification {

  "@json annotation" should {

    "create correct formatter for case class with >= 2 fields" in {

      val person = Person("Victor Hugo", 46, Some("Male"))
      val json = Json.toJson(person)
      json === Json.obj(
        "name" -> "Victor Hugo",
        "age" -> 46,
        "gender" -> "Male"
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

  "robustParsing" should {
    "make invalid option values None" in {
      val json = Json.obj(
        "name" -> "Victor Hugo",
        "age" -> 46,
        "gender" -> true
      )

      Json.fromJson[Person](json).asOpt must beSome(Person("Victor Hugo", 46, None))
      Json.fromJson[Person2](json).asOpt must beSome(Person2("Victor Hugo", 46))
    }

    "make invalid values with defaults fallback to the default" in {
      val json = Json.obj(
        "name" -> "Victor Hugo",
        "age" -> "non age"
      )

      Json.fromJson[Person2](json).asOpt must beSome(Person2("Victor Hugo", 7))
    }

    "throw on invalid values which are not optional or default" in {
      val json = Json.obj(
        "name" -> "Victor Hugo",
        "age" -> "non age"
      )

      val result = Json.fromJson[Person](json)

      result match {
        case e: JsError =>
          e.errors.head._1.path.head.asInstanceOf[KeyPathNode].key mustEqual("age")
          e.errors.head._2.head.message must contain("error.expected.jsnumber")
        case _ =>
          result must beAnInstanceOf[JsError]
      }
    }

    "multiple defaults must get replaced" in {
      val json = Json.obj("f1" -> "str", "f2" -> false, "f3" -> 3, "f4" -> "not test")
      val result = Json.fromJson[Test](json)
      result match {
        case JsSuccess(value, paths) =>
          value mustEqual(Test(f4 = None))
          paths.path.map(_.toString) must contain(allOf("/f1", "/f2", "/f3", "/f4"))
            .setMessage("success result should contain paths of failed keys")
        case _ => result must beAnInstanceOf[JsSuccess[Test]]
      }
    }
  }
}
