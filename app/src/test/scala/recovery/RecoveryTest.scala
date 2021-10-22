package recovery

import org.apache.commons.codec.binary.Hex
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers.include
import org.scalatest.matchers.should.Matchers.convertToAnyShouldWrapper
import org.scalatest.{EitherValues, OptionValues}

import java.nio.file.{Files, Path, Paths}
import java.util.stream.Collectors
import scala.collection.JavaConverters._

class RecoveryTest extends AnyFreeSpec with EitherValues with OptionValues {

  "test1" in {
    val outputDirPath = Paths.get("tmp/output")
    val discoveredFileSize = 10

    Files.list(outputDirPath).collect(Collectors.toList[Path]).asScala.foreach(Files.delete)
    Files.delete(outputDirPath)

    new Recovery(
      inputFilePath = Paths.get("app/src/test/resources/testfile.bin"),
      outputDirPath = outputDirPath,
      outputFileExtension = "bin",
      discoveredFileSize = discoveredFileSize,
      pattern = Array[Byte](0x01, 0x02, 0x03),
    ).scan()
    val outputFiles: Seq[Path] = Files.list(outputDirPath).collect(Collectors.toList[Path]).asScala
    outputFiles.length shouldBe 17
    outputFiles.map(Files.readAllBytes).foreach { x =>
      x.length shouldBe discoveredFileSize
      Hex.encodeHex(x).mkString should include("010203")
    }
  }

}
