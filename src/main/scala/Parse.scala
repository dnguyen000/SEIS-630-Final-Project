import com.jayway.jsonpath.JsonPath
import spray.json._

import java.io._
import java.nio.charset.StandardCharsets
import java.nio.file.{Files, Paths}
import scala.collection.convert.ImplicitConversions.`seq AsJavaList`

final case class Actor(id: Int, name: String)
final case class Role(id: Int, movieId: Int, name: String)
final case class Cast(cast_id: Int, character: String, credit_id: String, gender: Int, id: Int, name: String, order: Int, profile_path: String)
final case class Crew(credit_id: String, department: String, gender: Int, id: Int, job: String, name: String, profile_path: String)
final case class Credit(cast: Array[Cast], crew: Array[Crew], id: String)
final case class CastArray(items: Array[Cast]) extends IndexedSeq[Cast] {
  def apply(index: Int): Cast = items(index)

  def length = items.length
}

final case class CreditArray(items: Array[Credit]) extends IndexedSeq[Credit] {
  def apply(index: Int): Credit = items(index)

  def length = items.length
}


object Parse extends App with DefaultJsonProtocol {
  val idSource = scala.io.Source.fromFile(s"/Users/Shinra/st_thomas/SEIS 630-01/Final Project/resources/movie_ids.csv")
  val titleSource = scala.io.Source.fromFile(s"/Users/Shinra/st_thomas/SEIS 630-01/Final Project/resources/movie_titles.csv")
  val genreSource = scala.io.Source.fromFile(s"/Users/Shinra/st_thomas/SEIS 630-01/Final Project/resources/movie_genres.csv")
  val releaseDateSource = scala.io.Source.fromFile(s"/Users/Shinra/st_thomas/SEIS 630-01/Final Project/resources/movie_release_date.csv")
  val creditsSource = scala.io.Source.fromFile(s"/Users/Shinra/st_thomas/SEIS 630-01/Final Project/resources/formattedCredits_final.json")

  def createInsertCommand(ids: List[String], titles: List[String], releaseDates: List[String], acc: List[String], counter: Int): List[String] = {
    if (counter < ids.size) {
      val cmd = s"INSERT INTO final_project.movies(id, name, release_date, last_updated) values(${ids(counter).toInt},'${titles(counter).replaceAll("'", "''")}','${releaseDates(counter)}', NOW());"
      createInsertCommand(ids, titles, releaseDates, acc :+ cmd, counter + 1)
    } else {
     acc
    }
  }

  def createActorsInsertCommand(actorsList: List[Actor], counter: Int, acc: List[String]): List[String] = {
    if (counter < actorsList.size) {
      val cmd = s"INSERT INTO final_project.actors(id, name, last_updated) values(${actorsList(counter).id},'${actorsList(counter).name}', NOW());"
      createActorsInsertCommand(actorsList, counter + 1, acc ::: List[String](cmd))
    } else {
      acc
    }
  }

  def createRolesInsertCommand(rolesList: List[Role], counter: Int, acc: List[String]): List[String] = {
    if (counter < rolesList.size) {
      val cmd = s"INSERT INTO final_project.roles(actorId, movieId, characterName, last_updated) values(${rolesList(counter).id}, ${rolesList(counter).movieId}, '${rolesList(counter).name}', NOW());"
      createRolesInsertCommand(rolesList, counter + 1, acc ::: List[String](cmd))
    } else {
      acc
    }
  }

  def writeFile(lines: Seq[String], fileName: String): Unit = {
    println(s"In writeFile")
    val file = new File(fileName)
    val bw = new BufferedWriter(new FileWriter(file))
    for (line <- lines) {
      bw.write(line.replaceAll("\\P{Print}", "") + "\n")
    }
    bw.close()
  }

  def writeFilteredJson(lines: String, fileName: String): Unit = {
    Files.write(Paths.get(fileName), lines.getBytes(StandardCharsets.UTF_8))
  }

  val ids = idSource.getLines.toList
  val titles = titleSource.getLines.toList
  val genres = genreSource.getLines.toList
  val releaseDates = releaseDateSource.getLines.toList
  val credits = creditsSource.mkString

  import JsonFormat._
  val creditOutput = credits.parseJson.convertTo[CreditArray]

  val cast = creditOutput.get(0).cast.toJson.convertTo[CastArray]


  def iterateCredits(credits: CreditArray, creditCounter: Int, actorsAcc: List[Actor]): List[Actor] = {
    if (creditCounter < credits.length) {
      println(s"creditCounter: $creditCounter")
      val actors: List[Actor] = credits(creditCounter).cast.foldLeft(List[Actor]()){
        case (acc, el) =>
          val actor: Actor = Actor(el.id, el.name.replaceAll("'", "''"))

          acc ::: List[Actor](actor)
      }
      iterateCredits(credits, creditCounter + 1, actorsAcc ::: actors)
    } else {
      actorsAcc
    }
  }

  def iterateCreditsForRoles(credits: CreditArray, creditCounter: Int, roleAcc: List[Role]): List[Role] = {
    if (creditCounter < credits.length) {
      println(s"creditCounter: $creditCounter")
      val roles: List[Role] = credits(creditCounter).cast.foldLeft(List[Role]()){
        case (acc, el) =>
          val role: Role = Role(el.id, credits(creditCounter).id.toInt, el.character.replaceAll("'", "''"))

          acc ::: List[Role](role)
      }
      iterateCreditsForRoles(credits, creditCounter + 1, roleAcc ::: roles)
    } else {
      roleAcc
    }
  }

  println(s"creditOutput size: ${creditOutput.size}")

//  val actorsList1: List[Actor] = iterateCredits(creditOutput, 0, List[Actor]())
//  val actorsSql: List[String] = createActorsInsertCommand(actorsList1, 0, List[String]())
//
//  writeFile(actorsSql, "actorsInsert.sql")

//  val rolesList: List[Role] = iterateCreditsForRoles(creditOutput, 0, List[Role]())
//  val rolesSql: List[String] = createRolesInsertCommand(rolesList, 0, List[String]())
//
//  writeFile(rolesSql, "rolesInsert.sql")

//  val actorsList: List[Actor] = iterateCredits(creditOutput, 0, List[Actor]())
//  val actorsSql: List[String] = createActorsInsertCommand(actorsList, 0, List[String]())

//  writeFile(actorsSql, "sqlActorsInsert.sql")
//
//  println(s"actorsList size: ${actorsList.size}")
//  val actors: List[Actor] = createActorsList(creditOutput, 0, List[Actor])


//  println(s"testOutput size: ${testOutput.size}")
//  val output = testClean(testOutput, 0, List[String]())
//  writeFile(output, "cleanedOutput.txt")
//  testFix.close
  val filteredCredits = JsonPath.read[AnyVal](credits, "$..[*]").toString
//val filteredCredits = JsonPath.read[AnyVal](credits, "$..[*]").toString

  writeFilteredJson(filteredCredits, "filteredCredits3.json")
//  println(filteredCredits)



//
//
//
//  println(s"output: ${output.get(0)}")



//  def iterateCastList(regex: String, credits: List[String], counter: Int, cleanedList: List[String]): List[String] = {
//    if (counter < credits.size) {
//      val line = credits(counter)
//      val m = Pattern.compile(regex).matcher(line)
//      m.region(0, line.length)
//      if (m.find()) {
//        val substring = m.group(0).split(": \"")(1).split("\",")(0)
//        val cleanedSubstring = substring.replaceAll("\"", "'")
//
//        val cleanedLine = line.replace(substring, cleanedSubstring)
//
//        iterateCastList(regex, credits, counter + 1, cleanedList ::: List[String](cleanedLine))
//      } else {
//        iterateCastList(regex, credits, counter + 1, cleanedList ::: List[String](line))
//      }
//    } else {
//      cleanedList
//    }
//  }

//  def printCast(): Unit = {
//    import JsonFormat._
//
//    for (line <- credits) {
//      val lines = line.parseJson
//      val castMembers = lines.convertTo[CastArray]
//
//      val castMember = castMembers(0)
//      println(s"actor name: ${castMember.name}\ncharacter name: ${castMember.character}")
//    }
//  }
//  val moviesList: List[String] = createInsertCommand(ids, titles, releaseDates, List[String](), 0)
//  writeFile(moviesList, "sqlMoviesInsert.sql")

  idSource.close
  titleSource.close
  genreSource.close
  creditsSource.close
}
