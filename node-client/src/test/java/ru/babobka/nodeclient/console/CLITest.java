package ru.babobka.nodeclient.console;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.fail;
import static org.mockito.Mockito.*;

public class CLITest {

    private CLI cli;

    @Before
    public void setUp() {
        cli = spy(new TestableCLI());
    }

    @Test
    public void testOnStart() throws Exception {
        String[] args = new String[]{};
        Options options = mock(Options.class);
        doReturn(options).when(cli).buildOptions();
        CommandLine cmd = mock(CommandLine.class);
        doReturn(cmd).when(cli).parseCMD(options, args);
        cli.onStart(args);
        verify(cli).extraValidation(cmd);
        verify(cli).run(cmd);
    }

    @Test
    public void testOnStartFailedParse() throws Exception {
        String[] args = new String[]{};
        Options options = mock(Options.class);
        doReturn(options).when(cli).buildOptions();
        CommandLine cmd = mock(CommandLine.class);
        doNothing().when(cli).printHelp(options);
        doThrow(new RuntimeException()).when(cli).parseCMD(options, args);
        cli.onStart(args);
        verify(cli, never()).extraValidation(cmd);
        verify(cli, never()).run(cmd);
        verify(cli).printHelp(options);
    }

    @Test
    public void testOnStartFailedExtraValidation() throws Exception {
        String[] args = new String[]{};
        Options options = mock(Options.class);
        doReturn(options).when(cli).buildOptions();
        CommandLine cmd = mock(CommandLine.class);
        doNothing().when(cli).printHelp(options);
        doReturn(cmd).when(cli).parseCMD(options, args);
        doThrow(new RuntimeException()).when(cli).extraValidation(cmd);
        cli.onStart(args);
        verify(cli).extraValidation(cmd);
        verify(cli, never()).run(cmd);
        verify(cli).printHelp(options);
    }

    @Test
    public void testDoubleStart() throws Exception {
        String[] args = new String[]{};
        Options options = mock(Options.class);
        doReturn(options).when(cli).buildOptions();
        CommandLine cmd = mock(CommandLine.class);
        doReturn(cmd).when(cli).parseCMD(options, args);
        cli.onStart(args);
        verify(cli).run(cmd);
        try {
            cli.onStart(args);
            fail();
        } catch (IllegalStateException expected) {

        } catch (Exception e) {
            fail();
        }
    }

    private class TestableCLI extends CLI {

        @Override
        public List<Option> createOptions() {
            return null;
        }

        @Override
        public void run(CommandLine cmd) {

        }

        @Override
        public String getAppName() {
            return null;
        }
    }
}
