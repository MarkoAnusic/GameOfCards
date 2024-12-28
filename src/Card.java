import java.util.ArrayList;
import java.util.List;

public record Card(String face,
                   Suit suit,
                   int rank) {

   enum Suit {CLUB, DIAMOND, HEART, SPADE}

   //ranks: A,K,Q,J,10,9,8,7,6,5,4,3,2
   public static Card getNumericCard(Suit suit, int number) {
      if(number > 1 && number < 11) {
         return new Card(String.valueOf(number), suit, number);
      }
      System.out.println("Invalid Numeric card selected");
      return null;
   }

   public static Card getFaceCard(Suit suit, char abbreviation) {
      int charIndex = ("AJQK").indexOf(abbreviation);
      if(charIndex > - 1) {
         return new Card("" + abbreviation, suit, charIndex + 11);
      }
      System.out.println("Invalid card properties.");
      return null;
   }

   public static List<Card> getStandardDeck() {
      List<Card> standardDeck = new ArrayList<>(52);
      char[] suits = {'A', 'J', 'Q', 'K'};
      for(Suit suit : Suit.values()) {
         for(int i = 2; i <= 10; i++) {
            standardDeck.add(getNumericCard(suit, i));
         }
         for(char c : suits) {
            standardDeck.add(getFaceCard(suit, c));
         }
      }

      return standardDeck;
   }

   public static void printDeck(List<Card> deck, String description, int rows) {

      int cardsInRow = deck.size() / rows;
      System.out.println(description);
      if(deck.size() % rows != 0) {
         // Ako je broj karata u ruci/stolu nezgodan, nije djeljiv sa 4, printaju se karte normalno samo u zadnjem redu dodamo "visak" kartu
         for(int i = 0; i < rows * cardsInRow; i++) {
            System.out.print(deck.get(i) + " ");
            if((i + 1) == rows * cardsInRow) {
               System.out.print(deck.get(i + 1) + " ");
            }
            if((i + 1) % cardsInRow == 0) {
               System.out.println();
            }
         }
      }
      else {
         for(int i = 0; i < rows * cardsInRow; i++) {
            System.out.print(deck.get(i) + " ");
            if((i + 1) % cardsInRow == 0) {
               System.out.println();
            }
         }
      }
   }

   public static void printDeck(List<Card> deck) {
      printDeck(deck, "Current deck", 4);
   }

   @Override
   public String toString() {
      return switch(suit) {
         case CLUB -> face + (char) 9827 + "(" + rank + ")";
         case DIAMOND -> face + (char) 9830 + "(" + rank + ")";
         case HEART -> face + (char) 9829 + "(" + rank + ")";
         case SPADE -> face + (char) 9824 + "(" + rank + ")";
      };
   }

}
