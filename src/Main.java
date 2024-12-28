import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.CollationElementIterator;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class Main {

   public static Scanner scanner = new Scanner(System.in).useDelimiter("[,\\s+]");
   public static BufferedReader br = new BufferedReader(new InputStreamReader(System.in)); // stackOverflow
   public static Random random = new Random();
   public static int pickedUpCardsLast = 0;

   public static void main(String[] args) {

      List<Card> deck = Card.getStandardDeck();
      Card.printDeck(deck);
      List<Card> playerOne = new ArrayList<>();
      List<Card> playerTwo = new ArrayList<>();
      List<Card> table = new ArrayList<>();
      List<Card> onePile = new ArrayList<>();
      List<Card> twoPile = new ArrayList<>();
      Card xCard = new Card("X", Card.Suit.CLUB, 0);
      String tableChoice = "", choices[] = new String[0];
      int handChoice = 0, nowPlaying = 0;
      boolean gameOver = false, pickingCards = true; //gameOver needs to be true for the game to end
      System.out.println("Ko dijeli prvi? 1 ili 2? Igraju se 3 runde (špila).");
      int deailingCards = scanner.nextInt();
      if(deailingCards >= 2) {
         deailingCards = 2;
         nowPlaying = 1;
      }
      else if(deailingCards <= 1) {
         deailingCards = 1;
         nowPlaying = 2;
      }
      getRandomTable(deck, table);
      while(! gameOver) {
         pickingCards = true;
         //dealing cards if there are still some in the deck
         if(playerOne.isEmpty() && playerTwo.isEmpty()) {
            if(table.isEmpty() && deck.isEmpty()) {
               gameOver = true;
            }
            System.out.println("Izvlacenje karata...");
            try {
               TimeUnit.SECONDS.sleep(1);
            } catch(InterruptedException e) {
               throw new RuntimeException(e);
            }
            if(table.isEmpty() && (! deck.isEmpty())) {
               getRandomTable(deck, table);
            }

            getRandomHand(deck, playerOne);
            getRandomHand(deck, playerTwo);
         }
         //different cases for each players turn (player who plays sees other players cards as X-es)
         switch(nowPlaying) {
            case 1: {
               printGame(playerOne, Collections.nCopies(playerTwo.size(), xCard), table, nowPlaying);
               int onePileSize = onePile.size();
               pickCards(table, playerOne, onePile);
               if(onePile.size() > onePileSize) {
                  pickedUpCardsLast = 1;
               }
               nowPlaying = 2;
            }
            break;
            case 2: {
               printGame(Collections.nCopies(playerOne.size(), xCard), playerTwo, table, nowPlaying);
               int twoPileSize = twoPile.size();
               pickCards(table, playerTwo, twoPile);
               if(twoPile.size() > twoPileSize) {
                  pickedUpCardsLast = 2;
               }
               nowPlaying = 1;
            }
            break;
         }

         if(gameOver) {
            System.out.println("Game ended.\nCounting points...");
            try {
               TimeUnit.SECONDS.sleep(1);
            } catch(InterruptedException e) {
               throw new RuntimeException(e);
            }
         }

         if(pickedUpCardsLast == 1 && deck.isEmpty()) {
            onePile.addAll(table);
         }
         else if(pickedUpCardsLast == 2 && deck.isEmpty()) {
            twoPile.addAll(table);
         }
      }

      pointCounter(onePile, twoPile);

   }

   public static void printGame(List<Card> pOne, List<Card> pTwo, List<Card> table, int nowPlaying) {

      System.out.println("CURRENT TABLE \n" + "-".repeat(20));
      if(nowPlaying == 2) {
         System.out.println("     Player One");
         System.out.println("Xx(X) ".repeat(pOne.size()));
         System.out.println("-".repeat(20));
         printTable(table);
         System.out.println();
         System.out.println("-".repeat(20));
         Card.printDeck(pTwo, "     Player Two", 1);
      }
      else if(nowPlaying == 1) {
         Card.printDeck(pOne, "     Player One", 1);
         System.out.println("-".repeat(20));
         printTable(table);
         System.out.println();
         System.out.println("-".repeat(20));
         System.out.println("     Player Two");
         System.out.println("Xx(X) ".repeat(pTwo.size()));
         System.out.println("-".repeat(20));
      }
      else {
         Card.printDeck(pOne, "     Player One", 1);
         System.out.println("-".repeat(20));
         printTable(table);
         System.out.println();
         System.out.println("-".repeat(20));
         Card.printDeck(pTwo, "     Player Two", 1);

      }

   }

   public static List<Card> getRandomHand(List<Card> deck, List<Card> hand) {
      Random rnd = new Random();
      Collections.shuffle(deck);
      int rndmIndex = 0;
      for(int i = 0; i < 4; i++) {
         rndmIndex = rnd.nextInt(deck.size());
         hand.add(deck.get(rndmIndex));
         deck.remove(rndmIndex);
      }
      return hand;
   }

   public static List<Card> getRandomTable(List<Card> deck, List<Card> table) {
      Random rnd = new Random();
      Collections.shuffle(deck);
      int rndmIndex = 0;
      for(int i = 0; i < 4; i++) {
         rndmIndex = rnd.nextInt(deck.size());
         while(deck.get(rndmIndex).rank() == 12) {
            rndmIndex = rnd.nextInt(deck.size());
         }
         table.add(deck.get(rndmIndex));
         deck.remove(rndmIndex);
      }
      return table;
   }

   public static void printTable(List<Card> table) {
      int cardsInRow = table.size() / 4;
      if(cardsInRow == 0) {
         for(int i = 0; i < table.size(); i++) {
            System.out.println(table.get(i) + " " + (i));
         }

      }
      if(table.size() % 4 != 0 && cardsInRow != 0) {
         // Ako je broj karata u ruci/stolu nezgodan, nije djeljiv sa 4, printaju se karte normalno samo u zadnjem redu dodamo "visak" kartu
         for(int i = 0; i < table.size(); i++) {
            System.out.print(table.get(i) + " ");

            // Check if the end of a full row is reached
            if((i + 1) % cardsInRow == 0) {
               System.out.print((i - (cardsInRow - 1)) + " - " + i);
               System.out.println(); // Move to the next line for the next row
            }
         }

         // Check for any remaining cards in the last row
         if(table.size() % cardsInRow != 0) {
            int start = table.size() - (table.size() % cardsInRow);
            int end = table.size() - 1;
            System.out.print(start + " - " + end);
            System.out.println();
         }

      }
      else {
         for(int i = 0; i < 4 * cardsInRow; i++) {
            System.out.print(table.get(i) + " ");
            if((i + 1) % cardsInRow == 0) {
               System.out.print(i - (cardsInRow - 1) + " - " + i);
               System.out.println();
            }
         }
      }
   }

   public static void pickCards(List<Card> table, List<Card> playerCards, List<Card> playerPile) {
      String tableChoice = "", choices[] = new String[0];
      int handChoice = 0;
      int tableCardSum = 0;
      boolean pickingCards = true;

      while(pickingCards) {
         pickingCards = false;
         System.out.println("Izaberi kartu ili karte sa stola biranjem indeksa koji pise sa strane od - do. x,y ili x y. Ako ne zelis nista pokupiti stisni enter. " +
                 "\nKarta, karte sa stola: ");
         try {
            tableChoice = br.readLine(); // stack overflow
         } catch(IOException e) {
            throw new RuntimeException(e);
         }
         System.out.println("Izaberi kartu iz ruke kojom kupis karte sa stola ili je bacas na sto. Indeks broja iz ruke od 1 do broja karata u ruci." +
                 "\nKarta iz ruke: ");
         handChoice = scanner.nextInt();
         tableChoice = tableChoice.replace(",", " ");
         choices = tableChoice.trim().split("\\s+"); // from stackOverFlow
         if(! tableChoice.isEmpty()) {
            for(String index : choices) {
               if(Integer.parseInt(index) >= table.size() || Integer.parseInt(index) < 0) {
                  pickingCards = true;
               }
            }
         }

         if(handChoice >= playerCards.size()) {
            pickingCards = true;
         }
         if(table.isEmpty()) {
            table.add(playerCards.get(handChoice));
            playerCards.remove(handChoice);
            return;
         }
      }
      if(playerCards.get(handChoice).rank() == 12 || playerCards.get(handChoice).face() == "J") { // if the card the playerCards throws is a jack ("J",12) every card from the table goes to that playerCards
         playerPile.addAll(table);
         table.clear();
         playerPile.add(playerCards.get(handChoice));
         playerCards.remove(handChoice);
         return;
      }
      else if(tableChoice.isEmpty()) {
         table.add(playerCards.get(handChoice));
         playerCards.remove(handChoice);
         return;
      }
      else if(tableChoice.length() == 1) {
         Card tableCard = table.get(Integer.valueOf(tableChoice));
         Card handCard = playerCards.get(Integer.valueOf(handChoice));
         if(tableCard.rank() == handCard.rank()) {
            playerPile.addAll(List.of(tableCard, handCard));
            table.remove(tableCard);
            playerCards.removeIf(c -> handCard.equals(c));
         }
         else {
            table.add(handCard);
            playerCards.removeIf(c -> handCard.equals(c));
         }
         return;
      }
      else if(tableChoice.length() > 1) {
         List<Card> tableCards = new ArrayList<>();
         Card pickedCard = playerCards.get(handChoice);
         for(String number : choices) {
            if(table.get(Integer.parseInt(number)).rank() == pickedCard.rank()) {
               playerPile.add(table.get(Integer.parseInt(number)));
               continue;
            }
            tableCardSum += table.get(Integer.parseInt(number)).rank();
            tableCards.add(table.get(Integer.parseInt(number)));
         }
         if(tableCards.size() % 2 == 0) {
            int numberOfCombinations = 0;

            for(int i = 0; i < tableCards.size(); i++) {
               if(playerPile.contains(tableCards.get(i))) {
                  continue;
               }
               for(int j = tableCards.size() - 1; j > i; j--) {
                  if(playerPile.contains(tableCards.get(j))) {
                     continue;
                  }
                  if(tableCards.get(i).rank() + tableCards.get(j).rank() == pickedCard.rank()) {
                     numberOfCombinations++;
                     playerPile.addAll(List.of(tableCards.get(i), tableCards.get(j)));
                     break;
                  }
                  if(tableCards.get(i).rank() == 11 || tableCards.get(j).rank() == 11) {  // A equals 1 or 11 depending of what you want
                     if(tableCards.get(i).rank() + 1 == pickedCard.rank()) {
                        numberOfCombinations++;
                        playerPile.addAll(List.of(tableCards.get(i), tableCards.get(j)));
                        break;
                     }
                     else {
                        numberOfCombinations++;
                        playerPile.addAll(List.of(tableCards.get(i), tableCards.get(j)));
                        break;
                     }
                  }
               }
               if(numberOfCombinations == tableCardSum / pickedCard.rank()) {
                  break;
               }
            }
            for(int i = 0; i < tableCards.size(); i++) {
               if(playerPile.contains(tableCards.get(i))) {
                  table.remove(tableCards.get(i));
               }
            }
         }
         else {
            for(int i = 0; i < tableCards.size(); i++) {
               if(playerPile.contains(tableCards.get(i))) {
                  continue;
               }
               for(int j = tableCards.size() - 1; j > i; j--) {
                  if(playerPile.contains(tableCards.get(j))) {
                     continue;
                  }
                  if(tableCards.get(i).rank() + tableCards.get(j).rank() == pickedCard.rank()) {
                     playerPile.addAll(List.of(tableCards.get(i), tableCards.get(j)));
                     break;
                  }
                  if(tableCards.get(i).rank() == 11 || tableCards.get(j).rank() == 11) { // A equals 1 or 11 depending of what you want
                     if(tableCards.get(i).rank() + 1 == pickedCard.rank()) {
                        playerPile.addAll(List.of(tableCards.get(i), tableCards.get(j)));
                        break;
                     }
                     else {
                        playerPile.addAll(List.of(tableCards.get(i), tableCards.get(j)));
                        break;
                     }
                  }
               }
            }
            for(Card card : tableCards) {
               if(playerPile.contains(card)) {
                  table.remove(card);
               }
            }
         }
      }
   }

   public static void pointCounter(List<Card> onePile, List<Card> twoPile) {
      int pOnePoints = 0;
      int pTwoPoints = 0;

      if(onePile.contains(Card.getNumericCard(Card.Suit.CLUB, 2))) { //mala (small card) 2 of clubs
         pOnePoints++;
      }
      else {
         pTwoPoints++;
      }

      if(onePile.contains(Card.getNumericCard(Card.Suit.DIAMOND, 10))) { //velika (big card) 10 of diamonds
         pOnePoints++;
      }
      else {
         pTwoPoints++;
      }

      if(onePile.size() > twoPile.size()) {
         pOnePoints += 2;
      }
      else if(onePile.size() < twoPile.size()) {
         pTwoPoints += 2;
      }

      onePile.sort(Comparator.comparing(Card::suit));
      twoPile.sort(Comparator.comparing(Card::suit));
      int oneClubCounter = 0, twoClubCounter = 0;
      for(Card card : onePile) {
         if(card.suit() == Card.Suit.CLUB) {
            oneClubCounter++;
         }
      }
      for(Card card : twoPile) {
         if(card.suit() == Card.Suit.CLUB) {
            twoClubCounter++;
         }
      }

      if(oneClubCounter > twoClubCounter) {
         pOnePoints += 2;
      }
      else {
         pTwoPoints += 2;
      }

      if(pOnePoints > pTwoPoints) {
         System.out.println("CONGRATULATIONS PLAYER 1 WON");
         System.out.println("Player One points : " + pOnePoints);
         System.out.println("Player Two points : " + pTwoPoints);
      }
      else if(pOnePoints < pTwoPoints) {
         System.out.println("CONGRATULATIONS PLAYER 2 WON");
         System.out.println("Player One points : " + pOnePoints);
         System.out.println("Player Two points : " + pTwoPoints);
      }
      else {
         System.out.println("CONGRATULATIONS IT'S A DRAW");
         System.out.println("Player One points : " + pOnePoints);
         System.out.println("Player Two points : " + pTwoPoints);
      }
   }


}





