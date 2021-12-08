package org.moparforia.client;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import picocli.CommandLine;

import javax.swing.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.ParseException;
import java.util.prefs.Preferences;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Tests that loading/saving/resetting userPreferences works as expected
 */
@ExtendWith(MockitoExtension.class)
class UserPreferencesTest {

    private Launcher launcher;

    private CommandLine cmd;
    private StringWriter stdOut;
    private StringWriter stdErr;

    @BeforeEach
    void setUp() throws Exception {
        // Mock game
        launcher = mock(Launcher.class, withSettings()
                .lenient()
                .withoutAnnotations());

        // Use real methods
        doCallRealMethod().when(launcher).call();
        doCallRealMethod().when(launcher).setPort(anyInt());
        doCallRealMethod().when(launcher).setHostname(anyString());

        // Get rid of old preferences before test
        Preferences prefs = Preferences.userRoot().node("org.moparforia.client.Launcher");
        prefs.remove("timestamp");
        prefs.remove("version");
        prefs.remove("hostname");
        prefs.remove("port");
        //prefs.remove("lang");

        cmd = new CommandLine(launcher).setCaseInsensitiveEnumValuesAllowed(true);

        stdOut = new StringWriter();
        stdErr = new StringWriter();

        cmd.setOut(new PrintWriter(stdOut));
        cmd.setErr(new PrintWriter(stdErr));
    }

    @AfterEach
    void tearDown() {
        clearInvocations(launcher);
    }
    
    @Test
    void testPreferencesInvalidCLI() {
        assertNotEquals(0, cmd.execute("-r", "test"));
        assertNotEquals(0, cmd.execute("--reset", "test"));
        assertNotEquals(0, cmd.execute("-r=test"));
        assertNotEquals(0, cmd.execute("--reset=test"));
    }

    @Test
    void testPreferencesValidCLI() {
        // Prepare Test
        Preferences prefs = Preferences.userRoot().node("org.moparforia.client.Launcher");
        prefs.put("version", "1.2.3");
        prefs.put("hostname", "localhost");
        prefs.putInt("port", 4321);
        //prefs.putInt("lang", "en");
        
        // Normally launch Game
        try {
            doAnswer((invocaton) -> {
                launcher.setHostname(invocaton.getArgument(1));
                launcher.setPort(invocaton.getArgument(2));
                return true;
            }).when(launcher).showSettingDialog(any(JFrame.class), anyString(), anyInt());

        } catch (ParseException e) {
            //fail("showSettingDialog should not have thrown an Exception"); // TODO: fix
        }
        assertEquals(0, cmd.execute("-r"));
        verify(launcher).launchGame(any(),
                eq(Launcher.DEFAULT_SERVER),
                eq(Launcher.DEFAULT_PORT),
                eq(Launcher.Language.EN_US),
                anyBoolean());

        // Dismiss settings dialog TODO: fix this
        /*try {
            doAnswer((invocaton) -> {
                return false;
            }).when(launcher).showSettingDialog(any(JFrame.class), anyString(), anyInt());
        } catch (ParseException e) {
            //fail("showSettingDialog should not have thrown an Exception"); // TODO: fix
        }
        assertNotEquals(0, cmd.execute("--reset"));
        assertEquals("unknown", prefs.get("version", "unknown"));
        assertEquals("", prefs.get("hostname", ""));
        assertEquals(0, prefs.getInt("port", 0));
        assertEquals("", prefs.get("lang", ""));*/
    }

    @Test
    void testSavePreferences() {
        Preferences prefs = Preferences.userRoot().node("org.moparforia.client.Launcher");
        /*try {
            doAnswer((invocaton) -> {
                launcher.setHostname(invocaton.getArgument(1));
                launcher.setPort(invocaton.getArgument(2));
                return true;
            }).when(launcher).showSettingDialog(any(JFrame.class), anyString(), anyInt());

        } catch (ParseException e) {
            //fail("showSettingDialog should not have thrown an Exception"); // TODO: fix
        }
        assertEquals(0, cmd.execute());
        verify(launcher).launchGame(any(),
                eq(Launcher.DEFAULT_SERVER),
                eq(Launcher.DEFAULT_PORT),
                eq(Launcher.Language.EN_US),
                anyBoolean());
        assertEquals(Launcher.class.getPackage().getImplementationVersion(), prefs.get("version", "unknown"));
        assertEquals(Launcher.DEFAULT_SERVER, prefs.get("hostname", ""));
        assertEquals(Launcher.DEFAULT_PORT, prefs.getInt("port", 0));
        //assertEquals(Launcher.Language.EN_US, prefs.get("lang", ""));*/
    }

    @Test
    void testLoadPreferences() {
        Preferences prefs = Preferences.userRoot().node("org.moparforia.client.Launcher");
        prefs.put("version", "1.2.3");
        prefs.put("hostname", "localhost");
        prefs.putInt("port", 4321);
        //prefs.putInt("lang", "en");
        
        /*try {
            doAnswer((invocaton) -> {
                launcher.setHostname(invocaton.getArgument(1));
                launcher.setPort(invocaton.getArgument(2));
                return true;
            }).when(launcher).showSettingDialog(any(JFrame.class), anyString(), anyInt());

        } catch (ParseException e) {
            //fail("showSettingDialog should not have thrown an Exception"); // TODO: fix
        }
        assertEquals(0, cmd.execute());
        try {
            verify(launcher).showSettingDialog(any(),
                    eq("localhost"),
                    eq(4321)
                    //anyString(),
                );
        } catch (ParseException e) {
            //fail("showSettingDialog should not have thrown an Exception"); // TODO: fix
        }*/

    }
}
