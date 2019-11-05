package com.github.vitalsoftware.macros

import scala.reflect.macros._
import scala.language.experimental.macros
import scala.annotation.StaticAnnotation

import CrossVersionDefs._

/*
 * Derived from https://github.com/kifi/json-annotation
 *  - Removed distinction between 'json' and 'jsonstrict'
 *  - Added 'jsonDefaults' to use Play-Json 2.6+ format with default values on non-Option case class values
 */

object jsonMacroInstance extends jsonMacro(false)
object jsonDefaultsMacroInstance extends jsonMacro(true)

/**
 * "@json" macro annotation for case classes
 *
 * This macro annotation automatically creates a JSON serializer for the annotated case class.
 * The companion object will be automatically created if it does not already exist.
 */
class json extends StaticAnnotation {
  def macroTransform(annottees: Any*): Any = macro jsonMacroInstance.impl
}

/**
 * "@jsonDefaults" macro annotation for case classes
 *
 * Same as "@json" annotation, except that it uses Json.using[Json.WithDefaultValues] to allow default values
 * to be used for non-optional fields if they are not present during Reads[T].
 */
class jsonDefaults extends StaticAnnotation {
  def macroTransform(annottees: Any*): Any = macro jsonDefaultsMacroInstance.impl
}

class jsonMacro(useDefaults: Boolean) {
  def impl(c: CrossVersionContext)(annottees: c.Expr[Any]*): c.Expr[Any] = {
    import c.universe._

    def extractClassNameAndFields(classDecl: ClassDef) =
      try {
        val q"case class $className(..$fields) extends ..$bases { ..$body }" = classDecl
        (className, fields)
      } catch {
        case _: MatchError =>
          try {
            val q"final case class $className(..$fields) extends ..$bases { ..$body }" = classDecl
            (className, fields)
          } catch {
            case _: MatchError =>
              c.abort(c.enclosingPosition, "Annotation is only supported on case class")
          }
      }

    def jsonFormatter(className: TypeName, fields: List[ValDef]) =
      fields.length match {
        case 0 => c.abort(c.enclosingPosition, "Cannot create json formatter for case class with no fields")
        case _ =>
          if (useDefaults) {
            q"implicit val jsonAnnotationFormat = play.api.libs.json.Json.using[play.api.libs.json.Json.WithDefaultValues].format[$className]"
          } else {
            q"implicit val jsonAnnotationFormat = play.api.libs.json.Json.format[$className]"
          }
      }

    def modifiedCompanion(compDeclOpt: Option[ModuleDef], format: ValDef, className: TypeName) =
      compDeclOpt map { compDecl =>
        // Add the formatter to the existing companion object
        val q"object $obj extends ..$bases { ..$body }" = compDecl
        q"""
          object $obj extends ..$bases {
            ..$body
            $format
          }
        """
      } getOrElse {
        // Create a companion object with the formatter
        q"object ${className.toTermName} { $format }"
      }

    def modifiedDeclaration(classDecl: ClassDef, compDeclOpt: Option[ModuleDef] = None) = {
      val (className, fields) = extractClassNameAndFields(classDecl)
      val format = jsonFormatter(className, fields)
      val compDecl = modifiedCompanion(compDeclOpt, format, className)

      // Return both the class and companion object declarations
      c.Expr(q"""
        $classDecl
        $compDecl
      """)
    }

    annottees.map(_.tree) match {
      case (classDecl: ClassDef) :: Nil                          => modifiedDeclaration(classDecl)
      case (classDecl: ClassDef) :: (compDecl: ModuleDef) :: Nil => modifiedDeclaration(classDecl, Some(compDecl))
      case _                                                     => c.abort(c.enclosingPosition, "Invalid annottee")
    }
  }
}
