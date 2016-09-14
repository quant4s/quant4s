package com.bostontechnologies.quickfixs

sealed trait FixVersion

trait Fix50 extends FixVersion

trait Fix44 extends FixVersion

object FixVersion {

  implicit object Fix50 extends Fix50

  implicit object Fix44 extends Fix44

}