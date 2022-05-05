package Chat

import scala.annotation.tailrec

class UnexpectedTokenException(msg: String) extends Exception(msg) {}

class Parser(tokenized: Tokenized):

  import ExprTree._
  import Chat.Token._

  // Start the process by reading the first token.
  var curTuple: (String, Token) = tokenized.nextToken()

  def curValue: String = curTuple._1

  def curToken: Token = curTuple._2

  /** Reads the next token and assigns it into the global variable curTuple */
  def readToken(): Unit = curTuple = tokenized.nextToken()

  /** "Eats" the expected token and returns it value, or terminates with an error. */
  private def eat(token: Token): String =
    if token == curToken then
      val tmp = curValue
      readToken()
      tmp
    else expected(token)
  end eat

  /** Complains that what was found was not expected. The method accepts arbitrarily many arguments of type Token */
  private def expected(token: Token, more: Token*): Nothing =
    val expectedTokens = more.prepended(token).mkString(" or ")
    throw new UnexpectedTokenException(s"Expected: $expectedTokens, found: $curToken")
  end expected

  /** the root method of the parser: parses an entry phrase */
  def parsePhrases(): ExprTree =

    // A phrase may begin with a "BONJOUR"
    if curToken == BONJOUR then readToken()

    // A phrase is either an "EtatAme", "Identification", "Commande", "Solde", "Prix"
    // All except "Prix" begin with "JE" token

    // "EtatAme", "Identification", "Commande", "Solde"
    if curToken == JE then
      readToken()

      // "Commande", "Solde" begins with "Politesse"
      if curToken == VOULOIR then
        readToken()
        // "Commande"
        if curToken == COMMANDER then
          readToken()
          Command(parseProducts())
        // "Solde"
        else if curToken == CONNAITRE then
          readToken()
          eat(MON)
          eat(SOLDE)
          Balance()
        else expected(COMMANDER, CONNAITRE)
      // "Identification" with "me appelle"
      else if curToken == ME then
        readToken()
        eat(APPELER)
        parseName()
      // "Identification" with "suis", "EtatAme"
      else if curToken == ETRE then
        readToken()
        if curToken == PSEUDO then
          parseName()
        else if curToken == ASSOIFFE then
          readToken()
          Thirsty()
        else if curToken == AFFAME then
          readToken()
          Hungry()
        else expected(ASSOIFFE, AFFAME)
      else expected(VOULOIR, ME, ETRE)
    // "Prix"
    else
      if curToken == COMBIEN then
        readToken()
        eat(COUTER)
      else if curToken == QUEL then
        readToken()
        eat(ETRE)
        eat(LE)
        eat(PRIX)
        eat(DE)
      else expected(BONJOUR, JE, COMBIEN, QUEL)
      Price(parseProducts())
      
  end parsePhrases

  /**
    * Parse a series of products, maybe a single product.
    *
    * @return An expression tree with product(s) associated with AND/OR
    */
  private def parseProducts(): ExprTree =

    // First product
    val product = parseProduct()

    // Associate other products
    // To make a right associativity transform: leftAssociativity(And(exprTree, parseProduct()))
    //                                    into: And(exprTree, leftAssociativity(parseProduct()))
    // Change it for AND and OR
    @tailrec
    def leftAssociativity(exprTree: ExprTree): ExprTree =
      if curToken == ET then
        readToken()
        leftAssociativity(And(exprTree, parseProduct()))
      else if curToken == OU then
        readToken()
        leftAssociativity(Or(exprTree, parseProduct()))
      else exprTree
    end leftAssociativity

    leftAssociativity(product)
  end parseProducts

  /**
    * Parse a single product.
    *
    * @return Return a product ExprTree.
    */
  private def parseProduct(): ExprTree =
    var name = ""
    var quantity = 1
    var brand = ""

    // Getting quantity
    if curToken == NUM then
      quantity = Integer.parseInt(curValue)
      readToken()
    else expected(NUM)

    // Getting product type
    if curToken == PRODUCT then
      name = curValue
      readToken()
    else expected(PRODUCT)

    // Getting product brand (facultative)
    if curToken == MARQUE then
      brand = curValue
      readToken()
    end if
    Product(name, brand, quantity)
  end parseProduct

  /**
    * Parse a identification name.
    *
    * @return Returns an identification ExprTree with the corresponding name.
    */
  private def parseName(): ExprTree =
    if curToken == PSEUDO then
      // Removing starting '_'
      Identification(curValue.substring(1))
    else expected(PSEUDO)
  end parseName
end Parser


