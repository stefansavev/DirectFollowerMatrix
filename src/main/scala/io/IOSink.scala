package io

import scala.collection.mutable


trait IOKeyedSink[K, V]{
  def add(key: K, value: V): Unit
}


class IOKeyedCounter[K, V](defaultValue: V, reduce: (V, V) => V) extends IOKeyedSink[K, V]{
  val store = mutable.Map[K, V]()
  def add(key: K, value: V): Unit = {
    val prev: V = store.getOrElse(key, defaultValue)
    store(key) = reduce(prev, value)
  }

  def getResults: Seq[(K, V)] = {
    store.toSeq
  }
}

