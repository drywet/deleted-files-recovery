package recovery

import org.apache.commons.codec.binary.Hex

import java.nio.file.{Files, Path, Paths}

object Main {

  def main(args: Array[String]): Unit = {
    val config: Map[String, String] = args.toSeq.map(_.split("=")).map(x => x(0) -> x(1)).toMap
    val inputFilePath: Path = Paths.get(config("inputFilePath"))
    val outputDirPath: Path = Paths.get(config("outputDirPath"))
    val outputFileExtension: String = config("outputFileExtension")
    val discoveredFileSize: Int = config("discoveredFileSize").trim.toInt
    val patternArg: Option[Array[Byte]] = config.get("pattern").map(_.filterNot(_ == ',')).map(Hex.decodeHex)
    val sampleFilePath: Option[Path] = config.get("sampleFilePath").map(Paths.get(_))
    val sampleFilePatternLength: Option[Int] = config.get("sampleFilePatternLength").map(_.toInt)
    require(
      patternArg.isDefined || (sampleFilePath.isDefined && sampleFilePatternLength.isDefined),
      "either `pattern` or (`sampleFilePath` + `sampleFilePatternLength`) should be specified"
    )
    val pattern: Array[Byte] = patternArg.getOrElse(
      Files.readAllBytes(sampleFilePath.get).take(sampleFilePatternLength.get)
    )
    new Recovery(
      inputFilePath = inputFilePath,
      outputDirPath = outputDirPath,
      outputFileExtension = outputFileExtension,
      discoveredFileSize = discoveredFileSize,
      pattern = pattern,
    ).scan()
  }

}