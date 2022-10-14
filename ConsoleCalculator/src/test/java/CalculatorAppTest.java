import calculator.StackCalculator;
import command.*;
import context.CalculatorContext;
import context.ICalculatorContext;
import expression.parser.IExpressionParser;
import expression.parser.StringExpressionToOPZParser;
import expression.reader.IExpressionReader;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import stack.IStack;
import stack.Stack;
import uidriver.IUIDriver;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import static org.mockito.Mockito.*;

class CalculatorAppTest {
    private static final IExpressionReader readerMock = mock(IExpressionReader.class);
    private static final IUIDriver uiDriverMock = mock(IUIDriver.class);
    private static StackCalculator stackCalculator;

    @BeforeAll
    static void prepareCalculatorApp() {
        Map<Character, ICommand> commands = new HashMap<>();
        commands.put(AddCommand.getName(), new AddCommand());
        commands.put(SubCommand.getName(), new SubCommand());
        commands.put(MulCommand.getName(), new MulCommand());
        commands.put(DivCommand.getName(), new DivCommand());
        IStack<Double> stack = new Stack<>();
        ICalculatorContext context = new CalculatorContext(commands, stack);
        IExpressionParser parser = new StringExpressionToOPZParser(context);

        stackCalculator = new StackCalculator(context, readerMock, parser, uiDriverMock);
    }

    private static Stream<Arguments> provideTestData() {
        return Stream.of(
                Arguments.of("1+2-3", 0f),
                Arguments.of("5*2+10", 20f),
                Arguments.of("(6+10-4)/(1+1*2)+1", 5f)
        );
    }

    @ParameterizedTest
    @MethodSource("provideTestData")
    void calculate(String expression, double answer) {
        when(readerMock.hasExpression()).thenReturn(true).thenReturn(true).thenReturn(false);
        when(readerMock.readExpression()).thenReturn(expression);
        stackCalculator.start();
        verify(uiDriverMock, times(1)).showAnswer(answer);
    }

}