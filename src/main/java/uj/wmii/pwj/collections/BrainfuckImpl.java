package uj.wmii.pwj.collections;

import java.io.InputStream;
import java.io.PrintStream;
import java.util.Map;
import java.io.IOException;
import java.util.Deque;
import java.util.ArrayDeque;
import java.util.HashMap;

public class BrainfuckImpl implements Brainfuck {
    private final String program;
    private final PrintStream out;
    private final InputStream in;
    private final byte[] memory;
    private final Map<Integer, Integer> bracketMap;

    public BrainfuckImpl(String program, PrintStream out, InputStream in, int stackSize) {

        if (program == null || program.isEmpty() || out == null || in == null || stackSize < 1) {
            throw new IllegalArgumentException("illegal arguments");
        }

        this.program = program;
        this.out = out;
        this.in = in;
        this.memory = new byte[stackSize];
        this.bracketMap = buildBracketMap(program);
    }

    @Override
    public void execute() {
        int dataPtr = 0; // data pointer
        int instrPtr = 0;  // instruction pointer

        try {
            while (instrPtr < program.length()) {
                char cmd = program.charAt(instrPtr);
                switch (cmd) {
                    case '>': dataPtr++; if (dataPtr >= memory.length) throw new IndexOutOfBoundsException("data pointer out of bounds"); break;
                    case '<': dataPtr--; if (dataPtr < 0) throw new IndexOutOfBoundsException("data pointer out of bounds"); break;
                    case '+': memory[dataPtr]++; break;
                    case '-': memory[dataPtr]--; break;
                    case '.': out.write(memory[dataPtr]); break;
                    case ',': memory[dataPtr] = (byte)in.read(); break;
                    case '[': if (memory[dataPtr] == 0) instrPtr = bracketMap.get(instrPtr); break;
                    case ']': if (memory[dataPtr] != 0) instrPtr = bracketMap.get(instrPtr); break;
                    default: break; // skip invalid characters
                }
                instrPtr++;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            out.flush();
        }
    }

    private Map<Integer, Integer> buildBracketMap(String program) {
        Map<Integer, Integer> map = new HashMap<>();
        Deque<Integer> stack = new ArrayDeque<>();
        for (int i = 0; i < program.length(); i++) {
            char c = program.charAt(i);
            if (c == '[') stack.push(i);
            else if (c == ']') {
                if (stack.isEmpty()) throw new RuntimeException("unmatched bracket ']' at " + i);
                int openBracketIndex = stack.pop();
                map.put(openBracketIndex, i);
                map.put(i, openBracketIndex);
            }
        }
        if (!stack.isEmpty()) throw new RuntimeException("unmatched bracket '[' at " + stack.peek());
        return map;
    }
}