package com.javaadash.tc2.core.board;

import junit.framework.TestCase;

import com.javaadash.tc2.core.CardType;
import com.javaadash.tc2.core.GameUtils;
import com.javaadash.tc2.core.MockCardLocation;
import com.javaadash.tc2.core.board.BoardClearer;
import com.javaadash.tc2.core.board.InGameDeck;
import com.javaadash.tc2.core.card.Card;
import com.javaadash.tc2.core.context.GameContext;
import com.javaadash.tc2.core.interfaces.player.Player;

public class BoardClearerTest extends TestCase
{
	private BoardClearer boardClearer = new BoardClearer();
	
	public void testClearEmptyPlayerBoardAndDiscard_Characters() throws Exception {
		int maxCharacterrs = 2;
		InGameDeck inGameDeck = GameUtils.getInGameDeck(maxCharacterrs, 0, 0);
		
		// nothing on board
		for(Integer cardLocation: MockCardLocation.values()) {
			for(Card c: inGameDeck.getCards(CardType.CHARACTER)) {
					inGameDeck.setCardLocation(c, cardLocation);
			}
			
			boardClearer.clearPlayerBoard(inGameDeck);
			// check cards stay in cardLocation
			if(cardLocation == CardLocation.BOARD || cardLocation == CardLocation.DISCARD) {
				assertEquals(0, inGameDeck.getCards(CardType.CHARACTER, cardLocation).size());
			} else {
				assertEquals(maxCharacterrs, inGameDeck.getCards(CardType.CHARACTER, cardLocation).size());
			}
		}
	}
	
	public void testClearPlayerBoard_Characters() throws Exception
	{
		int maxCharacterrs = 2;
		InGameDeck inGameDeck = GameUtils.getInGameDeck(maxCharacterrs, 0, 0);
		
		// keep an index of characters putted in discard
		int index= 0;
		
		for(Card c: inGameDeck.getCards(CardType.CHARACTER)) {
			index++;
			inGameDeck.setCardLocation(c, CardLocation.BOARD);
			assertTrue(inGameDeck.getCards(CardType.CHARACTER,CardLocation.BOARD).contains(c));
			
			boardClearer.clearPlayerBoard(inGameDeck);

			assertFalse(inGameDeck.getCards(CardType.CHARACTER,CardLocation.BOARD).contains(c));
			// when player still has characters in hand
			if(index != maxCharacterrs)
				assertTrue(inGameDeck.getCards(CardType.CHARACTER, CardLocation.DISCARD).contains(c));
			// when all characters are in discard, they return in hand
			else {
				assertTrue(inGameDeck.getCards(CardType.CHARACTER, CardLocation.HAND).contains(c));
				assertEquals(maxCharacterrs, inGameDeck.getCards(CardType.CHARACTER, CardLocation.HAND).size());
			}
		}
	}
	
	public void testClearEmptyPlayerBoard_Actions() throws Exception {
		int maxActions = 10;
		InGameDeck inGameDeck = GameUtils.getInGameDeck(0, maxActions, 2);
		
		// nothing on board
		for(Integer cardLocation: MockCardLocation.values()) {
			for(Card a: inGameDeck.getCards(CardType.ACTION)) {
					inGameDeck.setCardLocation(a, cardLocation);
			}
			
			boardClearer.clearPlayerBoard(inGameDeck);
			
			// check cards stay in cardLocation
			if(cardLocation == CardLocation.BOARD) {
				assertEquals(0, inGameDeck.getCards(CardType.ACTION,cardLocation).size());
			} else {
				assertEquals(maxActions, inGameDeck.getCards(CardType.ACTION,cardLocation).size());
			}
		}
	}
	
	public void testClearPlayerBoard_Actions() throws Exception {
		int maxActions = 10;
		InGameDeck inGameDeck = GameUtils.getInGameDeck(0, maxActions, 2);
		
		for(Card a: inGameDeck.getCards(CardType.ACTION)) {
			inGameDeck.setCardLocation(a, CardLocation.BOARD);
			assertTrue(inGameDeck.getCards(CardType.ACTION,CardLocation.BOARD).contains(a));
			
			boardClearer.clearPlayerBoard(inGameDeck);

			assertFalse(inGameDeck.getCards(CardType.ACTION,CardLocation.BOARD).contains(a));
			assertTrue(inGameDeck.getCards(CardType.ACTION,CardLocation.DISCARD).contains(a));
		}
	}
	
	public void testClearBoard() throws Exception
	{
		int maxCharacters = 3; 
		int maxActions = 10;
		Player p1 = GameUtils.getPlayer("junit1", maxCharacters, maxActions, 0);
		Player p2 = GameUtils.getPlayer("junit2", maxCharacters, maxActions, 0);
		
		GameContext context = new GameContext();
		context.setStartPlayer(p1);
		context.setNextPlayer(p2);
		
		// player 1 has an action on board, player 2 has a character on board
		p1.getIngameDeck().setCardLocation(
				p1.getIngameDeck().getCards(CardType.ACTION).get(0), CardLocation.BOARD);
		assertEquals(1, p1.getIngameDeck().getCards(CardType.ACTION,CardLocation.BOARD).size());
		assertEquals(maxActions - 1, p1.getIngameDeck().getCards(CardType.ACTION, CardLocation.STOCK).size());
		
		p2.getIngameDeck().setCardLocation(
				p2.getIngameDeck().getCards(CardType.CHARACTER).iterator().next(), CardLocation.BOARD);
		assertEquals(1, p2.getIngameDeck().getCards(CardType.CHARACTER, CardLocation.BOARD).size());
		assertEquals(maxCharacters - 1, p2.getIngameDeck().getCards(CardType.CHARACTER, CardLocation.HAND).size());
		
		// clear board and check
		boardClearer.clearBoard(context);
		
		assertEquals(0, p1.getIngameDeck().getCards(CardType.ACTION, CardLocation.BOARD).size());
		assertEquals(maxActions - 1, p1.getIngameDeck().getCards(CardType.ACTION, CardLocation.STOCK).size());
		assertEquals(1, p1.getIngameDeck().getCards(CardType.ACTION, CardLocation.DISCARD).size());
		
		assertEquals(0, p2.getIngameDeck().getCards(CardType.CHARACTER,CardLocation.BOARD).size());
		assertEquals(maxCharacters - 1, p2.getIngameDeck().getCards(CardType.CHARACTER,CardLocation.HAND).size());
		assertEquals(1, p2.getIngameDeck().getCards(CardType.CHARACTER,CardLocation.DISCARD).size());
		
		// now invert and check again
		// player 1 has a character on board, player 2 has an action on board
		p1.getIngameDeck().setCardLocation(
				p1.getIngameDeck().getCards(CardType.CHARACTER).iterator().next(), CardLocation.BOARD);
		assertEquals(1, p1.getIngameDeck().getCards(CardType.CHARACTER,CardLocation.BOARD).size());
		assertEquals(maxCharacters - 1, p1.getIngameDeck().getCards(CardType.CHARACTER,CardLocation.HAND).size());
		
		p2.getIngameDeck().setCardLocation(
				p2.getIngameDeck().getCards(CardType.ACTION).iterator().next(), CardLocation.BOARD);
		assertEquals(1, p2.getIngameDeck().getCards(CardType.ACTION,CardLocation.BOARD).size());
		assertEquals(maxActions - 1, p2.getIngameDeck().getCards(CardType.ACTION, CardLocation.STOCK).size());
		
		// clear board and check
		boardClearer.clearBoard(context);

		assertEquals(0, p1.getIngameDeck().getCards(CardType.CHARACTER, CardLocation.BOARD).size());
		assertEquals(maxCharacters - 1, p1.getIngameDeck().getCards(CardType.CHARACTER, CardLocation.HAND).size());
		assertEquals(1, p1.getIngameDeck().getCards(CardType.CHARACTER, CardLocation.DISCARD).size());
		
		
		assertEquals(0, p2.getIngameDeck().getCards(CardType.ACTION,CardLocation.BOARD).size());
		assertEquals(maxActions - 1, p2.getIngameDeck().getCards(CardType.ACTION,CardLocation.STOCK).size());
		assertEquals(1, p2.getIngameDeck().getCards(CardType.ACTION,CardLocation.DISCARD).size());
		
	}
	
}
