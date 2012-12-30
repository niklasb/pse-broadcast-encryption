package cryptocast.util;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import org.mockito.Mockito;
import org.junit.Test;

import com.google.common.collect.ImmutableList;

public class TestGenerator {

	@Test
	public void getRangeWorks() {
		@SuppressWarnings("unchecked")
        Generator<Integer> gen = mock(Generator.class, Mockito.CALLS_REAL_METHODS);
		doReturn(5).when(gen).get(0);
		doReturn(4).when(gen).get(1);
		doReturn(3).when(gen).get(2);
		doReturn(2).when(gen).get(3);
		doReturn(1).when(gen).get(4);
		doReturn(0).when(gen).get(5);
		assertEquals(ImmutableList.builder().add(4, 3, 2).build(),
		             gen.getRange(1, 4));
	}

}
