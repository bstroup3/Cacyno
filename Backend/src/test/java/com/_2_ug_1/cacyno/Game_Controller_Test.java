package com._2_ug_1.cacyno;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

import com._2_ug_1.cacyno.models.Game;
import com._2_ug_1.cacyno.controllers.GameController;
import com._2_ug_1.cacyno.services.IGameService;

import java.util.ArrayList;

public class Game_Controller_Test {

    IGameService service = null;
    Game game = null;
    GameController controller = null;
    ArrayList<Game> list = null;

    @BeforeEach
    public void Game_Controller_Test() {
        service = mock(IGameService.class);
        game = new Game();
        controller = new GameController(service);
        list = new ArrayList<>();
    }

    //testGameInit is being Tested in Game_Service_Test

    //testUserPlay is being tested in Game_service Test


    @Test
    public void testGetAll() {
        when(service.findAll()).thenReturn(list);
        assertEquals(list, controller.getAll());
    }


    @Test
    public void testGetGameById() {
        game.setId("123");
        list.add(game);

        when(service.findById("123")).thenReturn(list.get(0));
        assertEquals(list.get(0), controller.getGameById("123"));
    }

    @Test
    public void testSaveGame() {
        ArrayList<Game> answerKey = new ArrayList<>();
        for (Game g : list) {
            answerKey.add(g);
        }
        answerKey.add(game);

        doAnswer(x -> {
            list.add(game);
            return null;
        }).when(service).save(game);

        controller.saveGame(game);

        assertEquals(list.toString(), answerKey.toString());
    }


    @Test
    public void testUpdateGame() {
        game.setId("123");
        list.add(game);

        Game game1 = new Game();
        game1.setId("456");

        ArrayList<Game> answerKey = new ArrayList<>();
        for (Game g : list) {
            answerKey.add(g);
        }
        answerKey.set(answerKey.indexOf(game), game1);

        doAnswer(x -> {
            list.set(list.indexOf(game), game1);
            return null;
        }).when(service).save(game);

        controller.updateGame(game);

        assertEquals(list.toString(), answerKey.toString());
    }


    @Test
    public void testDeleteGame() {
        game.setId("123");
        list.add(game);

        ArrayList<Game> answerKey = new ArrayList<>();
        for (Game g : list) {
            answerKey.add(g);
        }
        answerKey.remove(game);

        doAnswer(x -> {
            for (Game g : list) {
                if (g.getId().equals(x.getArgument(0))) {
                    list.remove(g);
                    break;
                }
            }
            return null;
        }).when(service).deleteById(any(String.class));

        controller.deleteGame("123");

        assertEquals(list.toString(), answerKey.toString());
    }
}
