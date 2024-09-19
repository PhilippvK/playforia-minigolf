package org.moparforia.server;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import picocli.CommandLine;

import java.io.PrintWriter;
import java.io.StringWriter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Tests that CLI parsing works as expected, it doesn't test the main method, but it tests the picocli annotations
 */
@ExtendWith(MockitoExtension.class)
class LauncherCLITest {
    private static final int DEFAULT_PORT = Integer.parseInt(Launcher.DEFAULT_PORT);
    private Launcher launcher;

    private CommandLine cmd;
    private StringWriter stdErr;
    private StringWriter stdOut;

    @BeforeEach
    void setUp() {
        // Mock Launcher instance
        launcher = mock(Launcher.class, withSettings()
                .lenient()
                .withoutAnnotations());

        doReturn(mock(Server.class)).when(launcher).getServer(anyString(), anyInt(), anyBoolean(), any());
        when(launcher.call()).thenCallRealMethod();

        cmd = new CommandLine(launcher);
        cmd.setCaseInsensitiveEnumValuesAllowed(true);

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
    void testInvalidPort() {
        assertNotEquals(0, cmd.execute("-p", "test"));
        assertNotEquals(0, cmd.execute("--port=test"));
        assertNotEquals(0, cmd.execute("-p"));

        verify(launcher, never()).getServer(anyString(), anyInt(), anyBoolean(), anyString());
    }

    @Test
    void testValidOptions() {
        assertEquals(0, cmd.execute("-p", "1111", "-ip", "128.128.128.128", "--tracks-dir", "/some/path"));
        verify(launcher).getServer(eq("128.128.128.128"), eq(1111), eq(false), eq("/some/path"));

        assertEquals(0, cmd.execute("-p=2222", "-ip=127.127.127.127", "-v", "-t=/some/path"));
        verify(launcher).getServer(eq("127.127.127.127"), eq(2222), eq(true), eq("/some/path"));

        assertEquals(0, cmd.execute("--port=3333", "--hostname=126.126.126.126", "--verbose", "--tracks-dir=/some/path"));
        verify(launcher).getServer(eq("126.126.126.126"), eq(3333), eq(true), eq("/some/path"));
    }

    @Test
    void testOnlyPort() {
        assertEquals(0, cmd.execute("-p", "1111"));
        verify(launcher).getServer(eq(Launcher.DEFAULT_HOST), eq(1111), eq(false), eq(null));
    }

    @Test
    void testOnlyHostname() {
        assertEquals(0, cmd.execute("-ip", "127.127.127.127"));
        verify(launcher).getServer(eq("127.127.127.127"), eq(DEFAULT_PORT), eq(false), eq(null));
    }

    @Test
    void testOnlyTracksDirectory() {
        assertEquals(0, cmd.execute("--tracks-dir", "/some/path"));
        verify(launcher).getServer(eq(Launcher.DEFAULT_HOST), eq(DEFAULT_PORT), eq(false), eq("/some/path"));
    }

    @Test
    void testDefaultValues() {
        assertEquals(0, cmd.execute());
        verify(launcher).getServer(eq(Launcher.DEFAULT_HOST), eq(DEFAULT_PORT), eq(false), eq(null));
    }
}
