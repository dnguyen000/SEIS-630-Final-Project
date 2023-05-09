import java.io.{BufferedWriter, File, FileWriter}
import java.util.regex.Pattern
import scala.collection.convert.ImplicitConversions.`seq AsJavaList`

object Cleanser extends App {
//  val creditsSource = scala.io.Source.fromFile(s"/Users/Shinra/st_thomas/SEIS 630-01/Final Project/resources/cleanedOutput_v2.txt")
val creditsSource = scala.io.Source.fromFile(s"/Users/Shinra/dev/scala-sandbox/credit_test.txt")
  def writeFile(lines: Seq[String], fileName: String): Unit = {
    println(s"In writeFile")
    val file = new File(fileName)
    val bw = new BufferedWriter(new FileWriter(file))
    for (line <- lines) {
      bw.write(line.replaceAll("\\P{Print}", "") + "\n")
    }
    bw.close()
  }

  def testClean(testInput: List[String], counter: Int, output: List[String]): List[String] = {
    if (counter < testInput.size) {
      if (counter % 1000 == 0) {
        println(s"counter: $counter")
      }
      val line = testInput.get(counter)
      val regex = "\"[A-Za-z .]+ [\\(A-Za-z \\-\\.]+\"[A-Za-z ]+\"\\)\""
      val m = Pattern.compile(regex).matcher(line)
      m.region(0, line.length)

      if (m.find()) {
        val substring = m.group(0).split("\"character\": ")(0)
        val substring2 = substring.replaceAll(substring, "")
        println(s"substring: $substring")
        println(s"substring2: $substring2")
        val cleanedSubstring = substring
          .replaceAll(" \"", " '")
          .replaceAll("\" ", "' ")
        val cleanedLine = line.replaceAll(substring, cleanedSubstring)
        println(s"cleanedLine: $cleanedLine")
        testClean(testInput, counter + 1, output ::: List[String](cleanedLine))
      } else {
        testClean(testInput, counter + 1, output ::: List[String](line))
      }
    } else {
      output
    }
  }

  val credits = creditsSource.getLines.toList

  val output = testClean(credits, 0, List[String]())

//  writeFile(output, "cleanedOutput_v3.txt")
  println(output)

  creditsSource.close

}
