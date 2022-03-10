package org.moparforia.server;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.moparforia.new_server.GolfServer;
import picocli.CommandLine;

import java.io.PrintWriter;
import java.io.StringWriter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.clearInvocations;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.withSettings;

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

        doReturn(mock(GolfServer.class)).when(launcher).getServer(anyString(), anyInt(), anyBoolean());
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

        verify(launcher, never()).getServer(anyString(), anyInt(), anyBoolean());
    }

    @Test
    void testValidPortAndHostname() {
        assertEquals(0, cmd.execute("-p", "1111", "-ip", "128.128.128.128"));
        verify(launcher).getServer(eq("128.128.128.128"), eq(1111), anyBoolean());

        assertEquals(0, cmd.execute("-p=2222", "-ip=127.127.127.127"));
        verify(launcher).getServer(eq("127.127.127.127"), eq(2222), anyBoolean());

        assertEquals(0, cmd.execute("-p=3333", "-ip=126.126.126.126"));
        verify(launcher).getServer(eq("126.126.126.126"), eq(3333), anyBoolean());
    }

    @Test
    void testOnlyPort() {
        assertEquals(0, cmd.execute("-p", "1111"));
        verify(launcher).getServer(eq(Launcher.DEFAULT_HOST), eq(1111), anyBoolean());
    }

    @Test
    void testOnlyHostname() {
        assertEquals(0, cmd.execute("-ip", "127.127.127.127"));
        verify(launcher).getServer(eq("127.127.127.127"), eq(DEFAULT_PORT), anyBoolean());
    }

    @Test
    void testDefaultValues() {
        assertEquals(0, cmd.execute());
        verify(launcher).getServer(eq(Launcher.DEFAULT_HOST), eq(DEFAULT_PORT), anyBoolean());
    }
}