package com.example.demo1;

import Models.GameInstance;
import Models.User;
import com.google.firebase.auth.FirebaseAuth;
import interfaces.ITextViews;
import org.json.JSONException;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.net.URISyntaxException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.*;

public class MoreTests {


    @Mock
    ITextViews views = mock(ITextViews.class);

    User user;

    GameInstance instance;

    @Before
    public void instantiation() throws JSONException, URISyntaxException {
        user = new User();
        instance = new GameInstance(user, views, true);
    }

    @Test
    public void userGetsAdded() {
        assertEquals(instance.getuserList().get(0), user);
    }

    @Test
    public void initializationTest() {
        assertNotNull(instance.getuserList());
    }

    @Test
    public void addAndRemovePlayerTest() {
        int[] index = {
                1,
                2,
                3,
                4,
                5
        };

        for(int i : index){
            instance.removePlayer(i, true);
        }

        for(int i : index){
            instance.setCurrentPlayerIndex(i);
            instance.toView(user, true);
        }

        verify(views, times(10));
    }

}
