package spoon;

import org.junit.jupiter.api.Test;
import shadedspoon.com.github.gumtreediff.matchers.CompositeMatchers;

/* Created by pourya on 2025-01-26*/
public class Properties {
    @Test
    public void isSimpleAsSameAsSpnSimple() {
        Object o1 = new CompositeMatchers.SimpleGumtree();
        Object o2 = new com.github.gumtreediff.matchers.CompositeMatchers.SimpleGumtree();
        System.out.println();


    }

}
