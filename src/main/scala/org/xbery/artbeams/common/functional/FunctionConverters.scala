package org.xbery.artbeams.common.functional

import org.xbery.overview.common.funs.CheckedFunction

/**
  * Conversions of Scala functions to Java functions.
  *
  * @author Radek Beran
  */
object FunctionConverters {
  import java.util.function.{ Function => JFunction, BiFunction => JFunction2, Predicate => JPredicate, BiPredicate }

  // usage example: `i: Int => 42`
  implicit def checkedJavaFun[A, B](f: A => B): CheckedFunction[A, B] = a => f(a)

  // usage example: `i: Int => 42`
  implicit def javaFun[A, B](f: A => B): JFunction[A, B] = a => f(a)

  implicit def javaFun2[A, B, C](f: (A, B) => C): JFunction2[A, B, C] = (a, b) => f(a, b)

  // usage example: `i: Int => true`
  implicit def javaPredicate[A](f: A => Boolean) = new JPredicate[A] {
    override def test(a: A): Boolean = f(a)
  }

  // usage example: `(i: Int, s: String) => true`
  implicit def javaBiPredicate[A, B](predicate: (A, B) => Boolean) =
    new BiPredicate[A, B] {
      def test(a: A, b: B) = predicate(a, b)
    }
}
