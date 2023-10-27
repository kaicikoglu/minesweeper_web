package controllers

import com.google.inject.Guice
import de.htwg.se.minesweeper.MinesweeperModuleEasy
import de.htwg.se.minesweeper.controller.controllerComponent.ControllerInterface
import de.htwg.se.minesweeper.model.FieldComponent.FieldBaseImpl.Coordinates
import play.api.mvc._

import javax.inject._

/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's home page.
 */
@Singleton
class HomeController @Inject()(cc: ControllerComponents) extends AbstractController(cc) {

  private val injector = Guice.createInjector(new MinesweeperModuleEasy)
  val controller: ControllerInterface = injector.getInstance(classOf[ControllerInterface])

  def index(): Action[AnyContent] = Action {
    Ok(views.html.index())
  }

  def createGame(difficulty: String): Action[AnyContent] = Action {
    Ok(views.html.displayGame(controller.createNewField(difficulty)))
  }

  def revealValue(x: Integer, y: Integer): Action[AnyContent] = Action {

    Ok(views.html.displayGame(controller.revealValue(new Coordinates(x, y))))
  }

  def setFlag(x: Integer, y: Integer): Action[AnyContent] = Action {
    Ok(views.html.displayGame(controller.setFlag(new Coordinates(x, y))))
  }

  def undo(): Action[AnyContent] = Action {
    Ok(views.html.displayGame(controller.undo))
  }

  def redo(): Action[AnyContent] = Action {
    Ok(views.html.displayGame(controller.redo))
  }
}
