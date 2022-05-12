package Web

import Chat.{AnalyzerService, TokenizerService}
import Data.{MessageService, AccountService, SessionService, Session}
import io.undertow.websockets.WebSocketConnectionCallback
import castor.Context.Simple.global
import sourcecode.Text.generate

/**
  * Assembles the routes dealing with the message board:
  * - One route to display the home page
  * - One route to send the new messages as JSON
  * - One route to subscribe with websocket to new messages
  *
  * @param log
  */
class MessagesRoutes(tokenizerSvc: TokenizerService,
                     analyzerSvc: AnalyzerService,
                     msgSvc: MessageService,
                     accountSvc: AccountService,
                     sessionSvc: SessionService)(implicit val log: cask.Logger) extends cask.Routes:
    import Decorators.getSession

    // var subscribers : List[cask.endpoints.WsChannelActor] = Nil

    @getSession(sessionSvc) // This decorator fills the `(session: Session)` part of the `index` method.
    @cask.get("/")
    def index()(session: Session) =

        // TODO - Part 3 Step 2: Display the home page (with the message board and the form to send new messages)
        Layouts.index()(session)
    end index

    // TODO - Part 3 Step 4b: Process the new messages sent as JSON object to `/send`. The JSON looks
    //      like this: `{ "msg" : "The content of the message" }`.
    //
    //      A JSON object is returned. If an error occurred, it looks like this:
    //      `{ "success" : false, "err" : "An error message that will be displayed" }`.
    //      Otherwise (no error), it looks like this:
    //      `{ "success" : true, "err" : "" }`
    //
    //      The following are treated as error:
    //      - No user is logged in
    //      - The message is empty
    //
    //      If no error occurred, every other user is notified with the last 20 messages
    @getSession(sessionSvc) // This decorator fills the `(session: Session)` part of the `index` method.
    @cask.postJson("/send")
    def sendMessage(msg: String)(session: Session) =

        if msg.isEmpty then
            ujson.Obj("success" -> false, "err" -> "A message cannot be empty")
        else if session.getCurrentUser.isEmpty then
            ujson.Obj("success" -> false, "err" -> "You must be logged in to send messages")
        else
            msgSvc.add(session.getCurrentUser.get, Layouts.message(session.getCurrentUser.get, msg), getMention(msg))
            // subscribers.foreach(sub => sub.send(cask.Ws.Text(msgSvc.getLatestMessages(20))))
            ujson.Obj("success" -> true, "err" -> "")
        end if

    end sendMessage

    private def getMention(msg: String) : Option[String] =
        if msg.charAt(0) == '@' then Some(msg.substring(1, msg.indexOf(" ")))
        else None
    end getMention

    // TODO - Part 3 Step 4c: Process and store the new websocket connection made to `/subscribe`
    @cask.websocket("/subscribe")
    def showUserProfile(): cask.WebsocketResult = ???/*{
      cask.WsHandler = cask.WsHandler { channel =>
        // subscribers = channel +: subscribers
        //channel.send(cask.Ws.Text("Someone joined"))
          cask.WsActor {
          case cask.Ws.Text(data) =>
            channel.send(cask.Ws.Text(" " + data))
        }

      }
    }*/

    // TODO - Part 3 Step 4d: Delete the message history when a GET is made to `/clearHistory`
    //
    // TODO - Part 3 Step 5: Modify the code of step 4b to process the messages sent to the bot (message
    //      starts with `@bot `). This message and its reply from the bot will be added to the message
    //      store together.
    //
    //      The exceptions raised by the `Parser` will be treated as an error (same as in step 4b)

    initialize()
end MessagesRoutes
