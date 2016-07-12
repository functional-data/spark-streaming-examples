package com.io.functionaldata.util

import scala.io.Source

/**
  * Created by mbarak on 12/07/16.
  */
trait ResourceReader {
  def source: Source
}

case class FileResourceReader(path: String) extends ResourceReader {
  override def source: Source = Source.fromFile(path)
}

case class ClassPathResourceReader(path: String) extends ResourceReader {
  override def source: Source = Source.fromInputStream(getClass.getClassLoader.getResourceAsStream(path))
}

