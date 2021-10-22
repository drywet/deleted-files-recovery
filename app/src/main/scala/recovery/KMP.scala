package recovery

/** Knuth–Morris–Pratt algorithm based on java implementation from the Internet */
object KMP {

  def indexOf(
               data: Array[Byte],
               dataOffset: Int,
               pattern: Array[Byte],
               failure: Array[Int],
             ): Int = {
    var j = 0
    (dataOffset until data.length).foreach { i =>
      while (j > 0 && pattern(j) != data(i)) j = failure(j - 1)
      if (pattern(j) == data(i)) j += 1
      if (j == pattern.length) return i - pattern.length + 1
    }
    -1
  }

  def computeFailure(pattern: Array[Byte]): Array[Int] = {
    val failure = new Array[Int](pattern.length)
    var j = 0
    (1 until pattern.length).foreach { i =>
      while (j > 0 && pattern(j) != pattern(i)) j = failure(j - 1)
      if (pattern(j) == pattern(i)) j += 1
      failure(i) = j
    }
    failure
  }

}
