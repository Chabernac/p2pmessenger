package chabernac.event;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

public class GenericTest extends TestCase {
    public void testGeneric() {
        genericMethod( new ArrayList< Integer >() );
    }

    private void genericMethod( List< Integer > aList ) {
// assertTrue( aList.getClass().getTypeParameters()[0].getGenericDeclaration().get
    }
}
