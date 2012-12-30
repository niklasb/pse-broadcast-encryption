package cryptocast.util;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import static org.mockito.Mockito.*;

import org.mockito.Mockito;

import com.google.common.collect.ImmutableList;

public class TestOptimisticGenerator {
    OptimisticGenerator<Integer> gen;
    Generator<Integer> inner;
    
    @Before
    @SuppressWarnings("unchecked")
    public void setUp() {
        inner = mock(Generator.class, 
                Mockito.CALLS_REAL_METHODS);
        for (int i = 0; i < 100; ++i) {
            doReturn(i).when(inner).get(i);
        }
        gen = new OptimisticGenerator<Integer>(inner);
    }

    @Test
    public void getWorks() {
        assertEquals(Integer.valueOf(10), gen.get(10));
        assertEquals(Integer.valueOf(0), gen.get(0));
        assertEquals(Integer.valueOf(19), gen.get(19));
    }

    @Test
    public void getRangeWorks() {
        assertEquals(ImmutableList.builder().add(1, 2, 3).build(),
                     gen.getRange(1, 4));
    }

    @Test
    public void itIsOptimistic() {
        gen.get(10);
        gen.get(10);
        gen.get(5);
        gen.get(1);
        verify(inner).getRange(anyInt(), anyInt());
        verify(inner, atLeast(0)).get(anyInt());
        verifyNoMoreInteractions(inner);
    }
}
