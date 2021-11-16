package recovery

import java.io.{BufferedInputStream, FileInputStream}
import java.nio.file.{Files, Path}

class Recovery(
                inputFilePath: Path,
                outputDirPath: Path,
                outputFileExtension: String,
                discoveredFileSize: Int,
                pattern: Array[Byte],
              ) {

  private var photoN: Int = 0

  def scan(): Unit = {
    val maxBufSize: Int = Math.min(8 * 1024 * 1024, discoveredFileSize)

    val failure: Array[Int] = KMP.computeFailure(pattern)

    Files.createDirectories(outputDirPath)

    val largeBuf: Array[Byte] = new Array[Byte](discoveredFileSize * 2)
    var largeBufSize: Int = 0
    val buf: Array[Byte] = new Array[Byte](maxBufSize)
    var totalBytesRead: Int = 0
    var totalBytesReadReportTime: Long = System.currentTimeMillis
    var in: BufferedInputStream = null
    try {
      in = new BufferedInputStream(new FileInputStream(inputFilePath.toFile))
      var bufSize: Int = in.read(buf)
      while (bufSize > -1) {
        val toCopySize: Int = Math.min(largeBuf.length - largeBufSize, bufSize)
        System.arraycopy(buf, 0, largeBuf, largeBufSize, toCopySize)
        largeBufSize += toCopySize
        totalBytesRead += toCopySize
        if (totalBytesReadReportTime + 10 * 1000 < System.currentTimeMillis) {
          totalBytesReadReportTime = System.currentTimeMillis
          System.out.println(s"totalBytesRead: ${totalBytesRead / 1024 / 1024} + MB")
        }
        if (largeBufSize == largeBuf.length) {
          scanLargeBufAndSaveFiles(largeBuf, largeBufSize, discoveredFileSize, failure)
          System.arraycopy(largeBuf, discoveredFileSize, largeBuf, 0, discoveredFileSize)
          largeBufSize = discoveredFileSize
        }
        bufSize = in.read(buf)
      }
      scanLargeBufAndSaveFiles(largeBuf, largeBufSize, 1, failure)
    } finally {
      if (in != null) in.close()
    }
  }

  private def scanLargeBufAndSaveFiles(
                                        largeBuf: Array[Byte],
                                        largeBufSize: Int,
                                        minChunkSize: Int,
                                        failure: Array[Int],
                                      ): Unit = {
    // Files.write(logPath, largeBuf, StandardOpenOption.APPEND);
    // Files.write(logPath, newLine, StandardOpenOption.APPEND);
    var dataOffset = 0
    var index = KMP.indexOf(largeBuf, dataOffset, pattern, failure)
    while ((index > -1) && (index <= largeBufSize - minChunkSize)) {
      val buf = new Array[Byte](Math.min(discoveredFileSize, largeBufSize - index))
      System.arraycopy(largeBuf, index, buf, 0, buf.length)
      val destFilePath = outputDirPath.resolve(s"file_$photoN.$outputFileExtension")
      Files.write(destFilePath, buf)
      dataOffset = index + 1
      photoN += 1
      index = KMP.indexOf(largeBuf, dataOffset, pattern, failure)
    }
  }

}
