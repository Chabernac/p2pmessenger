package chabernac.utils;

import java.io.File;
import java.io.IOException;

import junit.framework.TestCase;

public class IOToolsTest extends TestCase {
	public void testSaveObject() throws IOException {

		IOTools.saveObject("test", new File("c:\\temp\\test.bin"));

	}
}
