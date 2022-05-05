package Web

import scalatags.Text.all._
import scalatags.Text.tags2
import Data.{MessageService, AccountService, SessionService, Session}


/**
  * Assembles the method used to layout ScalaTags
  */
object Layouts:
  def index()(session: Session) =
    html(
      head(
        script(src := "/static/js/main.js"),
        tag("link")(href := "/static/css/main.css", rel := "stylesheet")
      ),
      body(
        tag("nav")(
          a(
            cls := "nav-brand"
          )(
            "Bot-Tender"
          ),
          div(
            cls := "nav-item"
          )(
            a(href := "/logout")(
              session.getCurrentUser.map(u => a(href := "/logout")("Logout"))
                .getOrElse(a(href := "/login")("Login"))
            )
          )
        ),
        div(
          cls := "content"
        )(
          div(
            id := "boardMessage"
          )(
            div(cls:="msg", textAlign.center)("Please wait! the messages are loading!")
            /*div(
              cls := ".msg"
            )(
              span(cls:="author"),
              span(cls := "msg-content")
            )*/
          ),
          form(id := "msgForm", onsubmit := "submitMessageForm(); return false")(
            div(id := "errorDiv", cls := "errorMsg", display:="none")("Error"),
            label(`for`:="messageInput")("Your message:"),
            input(id:="messageInput", tpe:="text"),
            input(tpe:="submit")
          )
        )
      )
    )

end Layouts
