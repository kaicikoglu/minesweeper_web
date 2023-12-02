package controllers

import com.google.inject.Guice
import de.htwg.se.minesweeper.MinesweeperModuleEasy
import de.htwg.se.minesweeper.controller.controllerComponent.ControllerInterface
import de.htwg.se.minesweeper.controller.controllerComponent.controllerBaseImpl.Controller
import de.htwg.se.minesweeper.model.FieldComponent.FieldBaseImpl.Coordinates
import de.htwg.se.minesweeper.util.DifficultyFactory
import play.api.mvc._

import javax.inject._

/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's home page.
 */
@Singleton
class HomeController @Inject()(cc: ControllerComponents) extends AbstractController(cc) {


  private val injector = Guice.createInjector(new MinesweeperModuleEasy)
  var controller: ControllerInterface = injector.getInstance(classOf[ControllerInterface])

  var size = ""

  def index(): Action[AnyContent] = Action {
    Ok(views.html.index())
  }

  def easy(): Action[AnyContent] = Action {
    controller = Controller(DifficultyFactory("1").run)
    controller.setBombs(controller.calculateBombAmount())
    size = "easy"
    Ok(views.html.displayGame(controller, size))
  }

  def hard(): Action[AnyContent] = Action {
    controller = Controller(DifficultyFactory("2").run)
    controller.setBombs(controller.calculateBombAmount())
    size = "hard"
    Ok(views.html.displayGame(controller, size))
  }

  def extreme(): Action[AnyContent] = Action {
    controller = Controller(DifficultyFactory("3").run)
    controller.setBombs(controller.calculateBombAmount())
    size = "extreme"
    Ok(views.html.displayGame(controller, size))
  }

  def revealValue(x: Integer, y: Integer): Action[AnyContent] = Action {
    controller.doAndPublish(controller.revealValue(new Coordinates(x, y)))
    Ok(controller.gameToJson)
  }

  def setFlag(x: Integer, y: Integer): Action[AnyContent] = Action {
    controller.doAndPublish(controller.setFlag(new Coordinates(x, y)))
    Ok(controller.gameToJson)
  }

  def load(): Action[AnyContent] = Action {
    controller.doAndPublish(controller.load)
    if (controller.field.rows == 8) {
      size = "easy"
    } else if (controller.field.rows == 16) {
      size = "hard"
    } else if (controller.field.rows == 24) {
      size = "extreme"
    }
    Ok(views.html.displayGame(controller, size))
  }

  def save(): Action[AnyContent] = Action {
    controller.doAndPublish(controller.save)
    Ok(views.html.displayGame(controller, size))
  }

  def gameToJson: Action[AnyContent] = Action {
    Ok(controller.gameToJson)
  }

  import play.api.libs.json.Json

  def flagsLeft(): Action[AnyContent] = Action {
    val flagsLeftValue = controller.flagsLeft()
    val jsonResult = Json.obj("flags_left" -> flagsLeftValue)
    Ok(jsonResult)
  }

}
