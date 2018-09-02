package ru.babobka.nodeclient.console;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import static org.mockito.Mockito.*;


/**
 * Created by 123 on 03.02.2018.
 */
public class CLITest {

    private CLI cli;

    @Before
    public void setUp() {
        cli = mock(CLI.class, Mockito.CALLS_REAL_METHODS);
    }

    @Test
    public void testOnMainParseException() throws Exception {
        String[] args = {"abc", "xyz"};
        CommandLineParser parser = mock(CommandLineParser.class);
        Options options = mock(Options.class);
        when(parser.parse(options, args)).thenThrow(new ParseException("test exception"));
        doReturn(parser).when(cli).createParser();
        doReturn(options).when(cli).createOptions();
        doNothing().when(cli).printHelp(options);
        cli.onMain(args);
        verify(cli).printHelp(options);
        verify(cli, never()).run(any(CommandLine.class));
    }

    @Test
    public void testOnMainFailedValidation() throws Exception {
        String[] args = {"abc", "xyz"};
        CommandLineParser parser = mock(CommandLineParser.class);
        Options options = mock(Options.class);
        CommandLine commandLine = mock(CommandLine.class);
        when(parser.parse(options, args)).thenReturn(commandLine);
        doReturn(parser).when(cli).createParser();
        doReturn(options).when(cli).createOptions();
        doNothing().when(cli).printHelp(options);
        doThrow(new ParseException("test exception")).when(cli).extraValidation(commandLine);
        cli.onMain(args);
        verify(cli).printHelp(options);
        verify(cli, never()).run(any(CommandLine.class));
    }

    @Test
    public void testOnMain() throws Exception {
        String[] args = {"abc", "xyz"};
        CommandLineParser parser = mock(CommandLineParser.class);
        Options options = mock(Options.class);
        CommandLine commandLine = mock(CommandLine.class);
        when(parser.parse(options, args)).thenReturn(commandLine);
        doReturn(parser).when(cli).createParser();
        doReturn(options).when(cli).createOptions();
        doNothing().when(cli).printHelp(options);
        doNothing().when(cli).run(commandLine);
        cli.onMain(args);
        verify(cli, never()).printHelp(options);
        verify(cli).run(commandLine);
    }
}
