package controllers

import akka.actor.{Actor, ActorRef, ActorSystem, Props}
import akka.stream.Materializer
import com.google.inject.Guice
import de.htwg.se.minesweeper.MinesweeperModuleEasy
import de.htwg.se.minesweeper.controller.controllerComponent.ControllerInterface
import de.htwg.se.minesweeper.controller.controllerComponent.controllerBaseImpl.Controller
import de.htwg.se.minesweeper.model.FieldComponent.FieldBaseImpl.Coordinates
import de.htwg.se.minesweeper.util.DifficultyFactory
import play.api.libs.json.{JsValue, Json}
import play.api.libs.streams.ActorFlow
import play.api.mvc._

import javax.inject._

/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's home page.
 */
@Singleton
class HomeController @Inject()(cc: ControllerComponents)(implicit system: ActorSystem, mat: Materializer) extends AbstractController(cc) {
  var size = ""
  var controller: ControllerInterface = GameController.getInstance
  private var clients: List[ActorRef] = List.empty

  def index(): Action[AnyContent] = Action {
    Ok(views.html.index())
  }

  def easy(): Action[AnyContent] = Action {
    GameController.resetInstance("1")
    controller = GameController.getInstance
    size = "easy"
    Ok(controller.gameToJson)
  }

  def medium(): Action[AnyContent] = Action {
    GameController.resetInstance("2")
    controller = GameController.getInstance
    size = "medium"
    Ok(controller.gameToJson)
  }

  def hard(): Action[AnyContent] = Action {
    GameController.resetInstance("3")
    controller = GameController.getInstance
    size = "hard"
    Ok(controller.gameToJson)
  }

  def revealValue(x: Integer, y: Integer): Action[AnyContent] = Action {
    controller.doAndPublish(controller.revealValue(new Coordinates(x, y)))
    Ok(controller.gameToJson)
  }

  def sendToClients(message: JsValue): Unit = {
    clients.foreach(_ ! message)
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
      size = "medium"
    } else if (controller.field.rows == 24) {
      size = "hard"
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

  def flagsLeft(): Action[AnyContent] = Action {
    val flagsLeftValue = controller.flagsLeft()
    val jsonResult = Json.obj("flags_left" -> flagsLeftValue)
    Ok(jsonResult)
  }

  def socket: WebSocket = WebSocket.accept[JsValue, JsValue] { _ =>
    ActorFlow.actorRef { out =>
      clients = out :: clients
      YourWebSocketActor.props(out, this)
    }
  }
}

class YourWebSocketActor(out: ActorRef, homeController: HomeController) extends Actor {
  def receive: Receive = {
    case msg: JsValue =>
      val command = (msg \ "command").as[String]
      command match {
        case "reveal" =>
          val x = (msg \ "x").as[Int]
          val y = (msg \ "y").as[Int]
          homeController.controller.doAndPublish(homeController.controller.revealValue(new Coordinates(x, y)))
          val updatedGameJson = homeController.controller.gameToJson
          out ! updatedGameJson
          homeController.sendToClients(updatedGameJson)
        case "flag" =>
          val x = (msg \ "x").as[Int]
          val y = (msg \ "y").as[Int]
          homeController.controller.doAndPublish(homeController.controller.setFlag(new Coordinates(x, y)))
          val updatedGameJson = homeController.controller.gameToJson
          out ! updatedGameJson
          homeController.sendToClients(updatedGameJson)
        case "bomb" =>
          val x = (msg \ "x").as[Int]
          val y = (msg \ "y").as[Int]
          val jsonObj = Json.obj(
            "event" -> "bombClick",
            "row" -> x,
            "col" -> y,
            "size" -> homeController.controller.field.rows
          )
          out ! jsonObj
          homeController.sendToClients(jsonObj)
        case "initialize" =>
          val difficulty = (msg \ "difficulty").as[String]
          GameController.resetInstance(difficulty)
          val initialGameJson = homeController.controller.gameToJson
          out ! initialGameJson
          homeController.sendToClients(initialGameJson)
      }
  }
}

object YourWebSocketActor {
  def props(out: ActorRef, homeController: HomeController): Props =
    Props(new YourWebSocketActor(out, homeController))
}

object GameController {
  private var controller: Option[ControllerInterface] = None

  def resetInstance(difficulty: String): Unit = synchronized {
    val newController = getInstance match {
      case _: Controller =>
        difficulty match {
          case "1" => Controller(DifficultyFactory("1").run)
          case "2" => Controller(DifficultyFactory("2").run)
          case "3" => Controller(DifficultyFactory("3").run)
          case _ => Controller(DifficultyFactory("1").run)
        }
    }
    newController.setBombs(newController.calculateBombAmount())
    controller = Some(newController)
  }

  def getInstance: ControllerInterface = synchronized {
    controller match {
      case Some(c) => c
      case None =>
        val injector = Guice.createInjector(new MinesweeperModuleEasy)
        val newController = injector.getInstance(classOf[ControllerInterface])
        controller = Some(newController)
        newController
    }
  }
}


