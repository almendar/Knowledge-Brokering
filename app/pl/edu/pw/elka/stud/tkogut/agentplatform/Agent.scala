package pl.edu.pw.elka.stud.tkogut.agentplatform

import play.Logger
import actors.{TIMEOUT, Actor}
import message.Message
import pl.edu.pw.elka.stud.tkogut.passim.agents.yellowpages.AgentRegistrationRequestMsg
import yellowpages.{AgentRegisteredACK, YellowPages}

/**
 * Created with IntelliJ IDEA.
 * User: tomek
 * Date: 20.06.12
 * Time: 17:23
 * To change this template use File | Settings | File Templates.
 */

abstract class Agent(name: String, description: String) extends Actor {
  /**
   * Return Agent's name
   */
  final def getName: String = name

  /**
   * Tells if is registered in Yellow pages.
   */
  protected var isRegistered = false

  def act();

  /**
   * Send message msg to adres
   * @param adress Agent that is the receiver of the message.
   * @param msg Message to be send.
   */
  def sendMessage(adress: Agent, msg: Message) {
    adress ! msg;
  }

  /**
   * Method make agent to speak text with his name.
   *
   * @param Obj
   */
  final def speak(Obj: Any) {
    //log.info(getName+ ":" + Obj.toString)
//    println(getName + ": " + Obj.toString)
    Logger.info(getName + ": " + Obj.toString)
  }


  /**
   *  Synchronous registration in yellow-pages.
   *  Watch out, cause it can block.
   */
  final protected def registerInYellowPages() {
    speak("Trying to register in Yellow Pages")
    val regMessage = new AgentRegistrationRequestMsg(this)
    val token = regMessage.token
    sendMessage(YellowPages, regMessage)
    receive {
      case msg: AgentRegisteredACK =>
        if (token == msg.token) {
          isRegistered = true
          speak("I was registered")
        }
        else {
          speak("I was *NOT* registered")
        }
    }
  }

  override def hashCode(): Int = {
    return getName.hashCode;
  }

  override def equals(that: Any): Boolean = {
    that match {
      case x: Agent => return (this.getClass == x.getClass) && (this.getName == x.getName)
      case _ => return false;
    }
  }

  override def toString = getName
}
