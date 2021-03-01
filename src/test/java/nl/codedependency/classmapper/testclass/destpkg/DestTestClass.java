package nl.codedependency.classmapper.testclass.destpkg;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Setter
@Getter
public class DestTestClass {
    private String field1;
    private String field2;
    private int field3;
    private int field4;
    private String field5;
    private DestTestClass objField;
    private List<DestTestClass> listField;
    private Map<String, DestTestClass> mapField;
    private Map<Integer, DestTestClass> numberMapField;
    private Set<DestTestClass> setField;
}
