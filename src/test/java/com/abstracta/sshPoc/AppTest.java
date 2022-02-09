package com.abstracta.sshPoc;

import org.junit.Test;

/**
 * Unit test for simple App.
 */
public class AppTest 
{
    /**
     * Rigorous Test :-)
     */
    @Test
    public void shouldAnswerWithTrue()
    {
        App app = new App("bara", "eisner.decus.org:22", "localhost1234", false);
        app.runShhClient();
        
    }
}
