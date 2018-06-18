package controllers

import javax.inject.Inject
import play.api.mvc.{AbstractController, ControllerComponents}

class RootController @Inject()(cc: ControllerComponents) extends AbstractController(cc) {

  def index() = Action { implicit req =>
    Ok("hello world")
  }
}
