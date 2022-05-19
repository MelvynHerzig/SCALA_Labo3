package Web

import scalatags.Text.all._
import scalatags.Text.tags2
import Data.{MessageService, AccountService, SessionService, Session}


/**
  * Assembles the method used to layout ScalaTags
  */
object Layouts:

  private def headElem() = head(
    script(src := "/static/js/main.js"),
    tag("link")(href := "/static/css/main.css", rel := "stylesheet")
  )

  private def navElem(linkTitle: String, linkHref: String, session: Session) = tag("nav")(
    a(
      cls := "nav-brand"
    )(
      "Bot-Tender"
    ),
    div(
      session.getCurrentUser.map(u => "Hello " + u + "! ")
        .getOrElse(""),
      cls := "nav-item"
    )(
      a(href := linkHref)(linkTitle)
    )
  )

  private def singleInputForm(idForm: String,
                              myAction: String,
                              myMethod: String,
                              jsSubmitFunction: String,
                              label: String,
                              errorId: String,
                              errorText: String,
                              inputId: String) = form(id := idForm, onsubmit := jsSubmitFunction, action:=myAction, method:=myMethod)
    (
      div(id := errorId, cls := "errorMsg", if errorText.nonEmpty then display := "none")(errorText),
      tag("label")(attr("for") := inputId)(label),
      input(id := inputId, tpe := "text", name:=inputId),
      input(tpe := "submit")
    )


  def index()(session: Session) =
    html(
      headElem(),
      body(
        session.getCurrentUser.map(u => navElem("Logout", "/logout", session))
          .getOrElse(navElem("Login", "/login", session)),
        div(
          cls := "content"
        )(
          div(
            id := "boardMessage"
          )(
            div(cls := "msg", textAlign.center)("Please wait! the messages are loading!")
          ),
          singleInputForm("msgForm","","", "submitMessageForm(); return false;", "Your message:", "errorDiv", "", "messageInput"),
        )
      )
    )

  def loginPage(errorLogin:String = "", errorRegister:String = "")(session: Session) =
    html(
      headElem(),
      body(
        navElem("Go to the message board", "/", session),
        div(
          cls := "content"
        )(
          h1("Login"),
          singleInputForm("loginForm","/login", "post", "", "Username:", "errorLogin", errorLogin, "loginInput"),
          h1("Register"),
          singleInputForm("registerForm", "/register", "post", "", "Username:", "errorRegister",errorRegister, "registerInput")
        )
      )
    )

  def successPage(action: String)(session: Session) =
    html(
      headElem(),
      body(
        navElem("Go to the message board", "/", session),
        div(
          cls := "content"
        )(
          div(cls := "msg", textAlign.center)(s"You ${action} successfully!")
        )
      )
    )

  def message(author : String, content : Frag) : Frag =
   div(cls:="msg")(
        tag("span")(cls:="author")(author),
        content
      ) 

  def messageContent(message : String) : Frag =
     tag("span")(cls:="msg-content")(message)

end Layouts
