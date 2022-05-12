package Web

import Data.{AccountService, Session, SessionService}

/**
  * Assembles the routes dealing with the users:
  * - One route to display the login form and register form page
  * - One route to process the login form and display the login success page
  * - One route to process the register form and display the register success page
  * - One route to logout and display the logout success page
  *
  * The username of the current session user is stored inside a cookie called `username`.
  */
class UsersRoutes(accountSvc: AccountService,
                  sessionSvc: SessionService)(implicit val log: cask.Logger) extends cask.Routes:

    import Decorators.getSession

    // TODO - Part 3 Step 3a: Display a login form and register form page for the following URL: `/login`.
    @getSession(sessionSvc)
    @cask.get("/login")
    def loginPage()(session: Session) =
        Layouts.loginPage()(session)
    end loginPage

    // TODO - Part 3 Step 3b: Process the login information sent by the form with POST to `/login`,
    //      set the user in the provided session (if the user exists) and display a successful or
    //      failed login page.
    @getSession(sessionSvc)
    @cask.postForm("/login")
    def login(loginInput : String)(session: Session) =
      if loginInput.isEmpty then Layouts.loginPage("Incorrect username")(session)
        else if accountSvc.isAccountExisting(loginInput) then
            session.setCurrentUser(loginInput)
            Layouts.successPage("logged in")(session)
        else Layouts.loginPage("User not found")(session)
    end login

    // TODO - Part 3 Step 3c: Process the register information sent by the form with POST to `/register`,
    //      create the user, set the user in the provided session and display a successful
    //      register page.
    @getSession(sessionSvc)
    @cask.postForm("/register")
    def register(registerInput : String)(session: Session) =
        if registerInput.isEmpty then Layouts.loginPage("", "Incorrect username")(session)
        else if !accountSvc.isAccountExisting(registerInput) then
            accountSvc.addAccount(registerInput, 30)
            session.setCurrentUser(registerInput)
            Layouts.successPage("registered")(session)
        else Layouts.loginPage("", "User already exist")(session)
    end register

    // TODO - Part 3 Step 3d: Reset the current session and display a successful logout page.
    @getSession(sessionSvc)
    @cask.get("/logout")
    def logout()(session: Session) =
        session.reset()
        Layouts.successPage("logged out")(session)
    end logout



    initialize()
end UsersRoutes
