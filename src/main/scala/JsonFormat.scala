import spray.json.{DefaultJsonProtocol, JsArray, JsNumber, JsObject, JsString, JsValue, RootJsonFormat, enrichAny}

object JsonFormat extends DefaultJsonProtocol {

  implicit object CastJsonFormat extends RootJsonFormat[Cast] {
    def write(obj: Cast): JsValue = {
      JsObject(
        "cast_id" -> JsNumber(obj.cast_id),
        "character" -> JsString(obj.character),
        "credit_id" -> JsString(obj.credit_id),
        "gender" -> JsNumber(obj.gender),
        "id" -> JsNumber(obj.id),
        "name" -> JsString(obj.name),
        "order" -> JsNumber(obj.order),
        "profile_path" -> JsString(obj.profile_path)
      )
    }

    def read(json: JsValue): Cast = {
      val fields = json.asJsObject.fields
      Cast(
        fields.get("cast_id").map(_.convertTo[Int]).getOrElse(0),
        fields.get("character").map(_.convertTo[String]).getOrElse(""),
        fields.get("credit_id").map(_.convertTo[String]).getOrElse(""),
        fields.get("gender").map(_.convertTo[Int]).getOrElse(0),
        fields.get("id").map(_.convertTo[Int]).getOrElse(0),
        fields.get("name").map(_.convertTo[String]).getOrElse(""),
        fields.get("order").map(_.convertTo[Int]).getOrElse(0),
        fields.get("profile_path").map(_.convertTo[String]).getOrElse("")
      )
    }
  }

  //final case class Crew(credit_id: Int, department: String, gender: Int, id: Int, job: String, name: String, profile_path: String)
  implicit object CrewJsonFormat extends RootJsonFormat[Crew] {
    def write(obj: Crew): JsValue = {
      JsObject(
        "credit_id" -> JsString(obj.credit_id),
        "department" -> JsString(obj.department),
        "gender" -> JsNumber(obj.gender),
        "id" -> JsNumber(obj.id),
        "job" -> JsString(obj.job),
        "name" -> JsString(obj.name),
        "profile_path" -> JsString(obj.profile_path)
      )
    }

    def read(json: JsValue): Crew = {
      val fields = json.asJsObject.fields
      Crew(
        fields.get("credit_id").map(_.convertTo[String]).getOrElse(""),
        fields.get("department").map(_.convertTo[String]).getOrElse(""),
        fields.get("gender").map(_.convertTo[Int]).getOrElse(0),
        fields.get("id").map(_.convertTo[Int]).getOrElse(0),
        fields.get("job").map(_.convertTo[String]).getOrElse(""),
        fields.get("name").map(_.convertTo[String]).getOrElse(""),
        fields.get("profile_path").map(_.convertTo[String]).getOrElse("")
      )
    }
  }

  implicit object CreditJsonFormat extends RootJsonFormat[Credit] {
    def write(obj: Credit): JsValue = {
      JsObject(
        "cast" -> obj.cast.toJson,
        "crew" -> obj.crew.toJson,
        "id" -> JsNumber(obj.id)
      )
    }

    def read(json: JsValue): Credit = {
      val fields = json.asJsObject.fields
      Credit(
        fields.get("cast").map(_.convertTo[Array[Cast]]).getOrElse(Array()),
        fields.get("crew").map(_.convertTo[Array[Crew]]).getOrElse(Array()),
        fields.get("id").map(_.convertTo[String]).getOrElse("")
      )
    }
  }

  implicit object CastArrayJsonFormat extends RootJsonFormat[CastArray] {
    def read(value: JsValue): CastArray = CastArray(value.convertTo[Array[Cast]])

    def write(obj: CastArray): JsArray = JsArray(obj.map(_.toJson).toVector)
  }

  implicit object CrewArrayJsonFormat extends RootJsonFormat[CrewArray] {
    def read(value: JsValue): CrewArray = CrewArray(value.convertTo[Array[Crew]])

    def write(obj: CrewArray): JsArray = JsArray(obj.map(_.toJson).toVector)
  }

  implicit object CreditArrayJsonFormat extends RootJsonFormat[CreditArray] {
    def read(value: JsValue): CreditArray = CreditArray(value.convertTo[Array[Credit]])

    def write(obj: CreditArray): JsArray = JsArray(obj.map(_.toJson).toVector)
  }
}
