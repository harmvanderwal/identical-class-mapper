package nl.codedependency.classmapper.util;

import nl.codedependency.classmapper.IdenticalClassMapper;
import nl.codedependency.classmapper.util.testclass.destpkg.DestTestClass;
import nl.codedependency.classmapper.util.testclass.sourcepkg.SourceTestClass;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class IdenticalClassMapperTest {

    private SourceTestClass sourceClass;

    @BeforeEach
    public void setUp() {
        SourceTestClass sourceClass2 = new SourceTestClass();
        sourceClass2.setField1("field3");
        sourceClass2.setField2("field4");
        sourceClass2.setField3(10);

        sourceClass = new SourceTestClass();
        sourceClass.setField1("field1");
        sourceClass.setField2("field2");
        sourceClass.setField3(10);
        sourceClass.setObjField(sourceClass2);
        sourceClass.setListField(List.of(sourceClass2));
        sourceClass.setMapField(Map.of("MapField1", sourceClass2));
        sourceClass.setNumberMapField(Map.of(1, sourceClass2));
        sourceClass.setSetField(Set.of(sourceClass2));
    }

    @Test
    public void testMap() {
        compareClasses(sourceClass, Objects.requireNonNull(IdenticalClassMapper.map(sourceClass, DestTestClass.class)));
    }

    public void compareClasses(SourceTestClass sourceClass,
                               DestTestClass destClass) {
        assertEquals(sourceClass.getField1(), destClass.getField1());
        assertEquals(sourceClass.getField2(), destClass.getField2());
        assertEquals(sourceClass.getField3(), destClass.getField3());
        if (sourceClass.getObjField() != null) {
            compareClasses(sourceClass.getObjField(), destClass.getObjField());
        }
        assertEquals(0, destClass.getField4());
        assertNull(destClass.getField5());
        for (int i = 0; sourceClass.getListField() != null
                && i < sourceClass.getListField().size(); i++) {
            compareClasses(sourceClass.getListField().get(i), destClass.getListField().get(i));
        }
        if (sourceClass.getMapField() != null) {
            assertEquals(0, sourceClass.getMapField().entrySet()
                    .stream()
                    .filter(x -> !destClass.getMapField().containsKey(x.getKey()) && !destClass.getMapField().containsValue(IdenticalClassMapper.map(x, DestTestClass.class)))
                    .count());
        }
    }
}