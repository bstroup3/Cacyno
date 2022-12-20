package com._2_ug_1.cacyno;

import com._2_ug_1.cacyno.game.HandChecker;
import com._2_ug_1.cacyno.game.HandComparator;
import com._2_ug_1.cacyno.game.Hands;
import com._2_ug_1.cacyno.models.Hand;
import org.json.JSONException;
import org.junit.Test;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class HandComparatorTest {

    @Mock
    HandChecker handChecker = mock(HandChecker.class);

    @Test
    public void TestHandChecker() {
        HandChecker handChecker = mock(HandChecker.class);
        HandComparator handComparator = new HandComparator();
        int[] hand1 = new int[]{1,2,3,4,5,8,36};
        int[] hand2 = new int[]{3,36,2,7,27,3,37};
        when(handChecker.getHandRank(hand1)).thenReturn(new Hand(Hands.STRAIGHTFLUSH, new int[]{5,4,3,2,1}));
        when(handChecker.getHandRank(hand2)).thenReturn(new Hand(Hands.PAIR, new int[]{3,3,11,10,7}));
        handComparator.compareHands(hand1,hand2);
        int value = handComparator.compareHands(hand1, hand2);
        assertTrue(value>0);
    }



    @Test
    public void TestHandCheckerEquals() {
        HandComparator handComparator = new HandComparator();
        int[] hand1 = new int[]{1,14,27,5,18,11,36};
        int[] hand2 = new int[]{1,14,27,5,18,15,29};
        when(handChecker.getHandRank(hand1)).thenReturn(new Hand(Hands.FULLHOUSE, new int[]{1,1,1,5,5}));
        when(handChecker.getHandRank(hand2)).thenReturn(new Hand(Hands.FULLHOUSE, new int[]{1,1,1,5,5}));
        handComparator.compareHands(hand1,hand2);
        int value = handComparator.compareHands(hand1, hand2);
        assertEquals(value, 0);
    }
}
