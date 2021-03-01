package nl.codedependency.classmapper.util.testclass.sourcepkg;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Setter
@Getter
public class SourceTestClass {
    private String field1;
    private String field2;
    private int field3;
    private int field4;
    private String field5;
    private SourceTestClass objField;
    private List<SourceTestClass> listField;
    private Map<String, SourceTestClass> mapField;
    private Map<Integer, SourceTestClass> numberMapField;
    private Set<SourceTestClass> setField;
}
