package org.moparforia.client;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.quality.Strictness;
import picocli.CommandLine;

import javax.swing.JFrame;
import java.io.PrintWriter;
import java.io.StringWriter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyBoolean;
import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.clearInvocations;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.withSettings;

/**
 * Tests that CLI parsing works as expected, it doesn't test the main method, but it tests the picocli annotations
 */
@ExtendWith(MockitoExtension.class)
class LauncherCLITest {

    private Launcher launcher;

    private CommandLine cmd;
    private StringWriter stdOut;
    private StringWriter stdErr;

    @BeforeEach
    void setUp() throws Exception {
        // Mock game
        launcher = mock(Launcher.class, withSettings()
                .strictness(Strictness.LENIENT)
                .withoutAnnotations());

        // Use real methods
        doCallRealMethod().when(launcher).call();
        doCallRealMethod().when(launcher).setPort(anyInt());
        doCallRealMethod().when(launcher).setHostname(anyString());

        doReturn(mock(JFrame.class)).when(launcher).createFrame();
        doAnswer((invocaton) -> {
            launcher.setPort(invocaton.getArgument(2));
            launcher.setHostname(invocaton.getArgument(1));
            return true;
        }).when(launcher).showSettingDialog(any(JFrame.class), anyString(), anyInt());


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
    void testInvalidPort() {
        assertNotEquals(0, cmd.execute("-p", "test"));
        assertNotEquals(0, cmd.execute("--port=test"));
        assertNotEquals(0, cmd.execute("-p"));
    }

    @Test
    void testInvalidLang() {
        assertNotEquals(0, cmd.execute("-l", "cs_CZ"));
        assertNotEquals(0, cmd.execute("-l", "en"));
    }

    @Test
    void testValidLang() {
        assertEquals(0, cmd.execute("-l", "en_US"));
        verify(launcher).launchGame(any(),
                eq(Launcher.DEFAULT_SERVER),
                eq(Launcher.DEFAULT_PORT),
                eq(Launcher.Language.EN_US),
                any(),
                anyBoolean(),
                anyBoolean());

        assertEquals(0, cmd.execute("--lang=Fi_fI"));
        verify(launcher).launchGame(any(),
                eq(Launcher.DEFAULT_SERVER),
                eq(Launcher.DEFAULT_PORT),
                eq(Launcher.Language.FI_FI),
                any(),
                anyBoolean(),
                anyBoolean());
    }

    @Test
    void testValidUsername() {
        assertEquals(0, cmd.execute("-u", "user"));
        verify(launcher).launchGame(any(),
                eq(Launcher.DEFAULT_SERVER),
                eq(Launcher.DEFAULT_PORT),
                any(),
                eq("user"),
                anyBoolean(),
                anyBoolean());
        assertEquals(0, cmd.execute("--username=user2"));
        verify(launcher).launchGame(any(),
                eq(Launcher.DEFAULT_SERVER),
                eq(Launcher.DEFAULT_PORT),
                any(),
                eq("user2"),
                anyBoolean(),
                anyBoolean());
    }

    @Test
    void testInvalidUsername() {
        assertEquals(2, cmd.execute("-u", "[/.]"));
        assertEquals(2, cmd.execute("--username='  '"));
    }

    @Test
    void testValidPortAndHostname() {
        assertEquals(0, cmd.execute("-p", "1111", "-ip", "128.128.128.128"));
        verify(launcher).launchGame(any(), eq("128.128.128.128"), eq(1111), any(), any(), anyBoolean(), anyBoolean());

        assertEquals(0, cmd.execute("-p=2222", "-ip=127.127.127.127"));
        verify(launcher).launchGame(any(), eq("127.127.127.127"), eq(2222), any(), any(), anyBoolean(), anyBoolean());

        assertEquals(0, cmd.execute("-p=3333", "-ip=126.126.126.126"));
        verify(launcher).launchGame(any(), eq("126.126.126.126"), eq(3333), any(), any(), anyBoolean(), anyBoolean());
    }

    @Test
    void testOnlyPort() {
        assertEquals(0, cmd.execute("-p", "1111"));
        verify(launcher).launchGame(any(), eq(Launcher.DEFAULT_SERVER), eq(1111), any(), any(), anyBoolean(), anyBoolean());
    }

    @Test
    void testOnlyHostname() {
        assertEquals(0, cmd.execute("-ip", "127.127.127.127"));
        verify(launcher).launchGame(any(), eq("127.127.127.127"), eq(Launcher.DEFAULT_PORT), any(), any(), anyBoolean(),
                anyBoolean());
    }

    @Test
    void testDefaultValues() {
        assertEquals(0, cmd.execute());
        verify(launcher).launchGame(
                any(),
                eq(Launcher.DEFAULT_SERVER),
                eq(Launcher.DEFAULT_PORT),
                eq(Launcher.Language.EN_US),
                eq(null),
                eq(false),
                eq(false)
        );
    }
}