/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package observers.events;

/**
 * Tipos de eventos
 * @author txaber gardeazabal
 */
public enum EventType {
        EditorStartPlay,
        EditorStopPlay,
        GameEngineStartPlay,
        GameEngineStopPlay,
        SaveLevel,
        LoadLevel,
        PlayLevel,
        OpenInEditor,
        EndWindow,
        Exit,
        UserEvent,
        
        // game events
        CoinGet,
        MarioWin,
        MarioDie,
        oneUp,
        ScoreUpdate
    };
