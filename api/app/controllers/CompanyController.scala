package controllers

import com.zaneli.escalade.api.entity.{CompanyEntity, CompanyId, HasId, HasVersion}
import com.zaneli.escalade.api.repository.Result
import com.zaneli.escalade.api.service.CompanyMemberService
import io.circe.syntax._
import javax.inject.Inject
import play.api.Logger
import play.api.libs.circe.Circe
import play.api.mvc.{AbstractController, ControllerComponents}

class CompanyController @Inject()(
  cc: ControllerComponents,
  service: CompanyMemberService
) extends AbstractController(cc) with Circe {

  private[this] val logger = Logger(this.getClass)

  def list() = Action { implicit req =>
    Ok(service.findCompanies().asJson)
  }

  def get(id: Long) = Action { implicit req =>
    Ok(service.findCompanyById(CompanyId(id)).asJson)
  }

  def create()= Action(circe.json[CompanyEntity]) { implicit req =>
    service.save(req.body).fold(
      t => BadRequest(t.getMessage),
      {
        case Result.InsertSuccess(id) => Ok(id.asJson)
        case r@Result.UpdateSuccess(_) =>
          logger.warn(s"unexpected result: $r")
          Ok("")
      }
    )
  }

  def edit(id: Long) = Action(circe.json[CompanyEntity with HasId[CompanyId] with HasVersion]) { implicit req =>
    service.save(req.body).fold(
      t => BadRequest(t.getMessage),
      {
        case r@Result.InsertSuccess(id) =>
          logger.warn(s"unexpected result: $r")
          Ok(id.asJson)
        case Result.UpdateSuccess(_) => Ok("")
      }
    )
  }
}
