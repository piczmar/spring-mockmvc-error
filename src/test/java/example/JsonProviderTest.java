package example;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.spi.json.JacksonJsonProvider;
import com.jayway.jsonpath.spi.json.JsonProvider;
import com.jayway.jsonpath.spi.json.JsonSmartJsonProvider;
import org.assertj.core.util.Arrays;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.LinkedHashMap;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

@RunWith(Parameterized.class)
public class JsonProviderTest {

    @Parameters(name = "{index} | {0} | {1} | {2}")
    public static Collection data() {
        return Arrays.asList(new Object[][]{
                {"{\"value\":0.07185454505642408}",    BigDecimal.class, new JsonSmartJsonProvider()},
                {"{\"value\":0.07185454505642408}",    Double.class,     new JacksonJsonProvider()},
                {"{\"value\":0.07185454505642408}",    BigDecimal.class, new JacksonJsonProvider(getObjectMapper(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS))},
                {"{\"value\":0.071854545056424}",      Double.class,     new JsonSmartJsonProvider()},
                {"{\"value\":0.071854545056424}",      Double.class,     new JacksonJsonProvider()},
                {"{\"value\":1}",                      Integer.class,    new JsonSmartJsonProvider()},
                {"{\"value\":1}",                      Integer.class,    new JacksonJsonProvider()},
                {"{\"value\":1}",                      Long.class,       new JacksonJsonProvider(getObjectMapper(DeserializationFeature.USE_LONG_FOR_INTS))},
                {"{\"value\":" + Long.MAX_VALUE + "}", Long.class,       new JsonSmartJsonProvider()},
                {"{\"value\":" + Long.MAX_VALUE + "}", Long.class,       new JacksonJsonProvider()}
        });
    }

    private final JsonProvider provider;

    private String json;

    private Class clazz;

    public JsonProviderTest(String json, Class clazz, JsonProvider provider) {
        this.json = json;
        this.clazz = clazz;
        this.provider = provider;
    }

    @Test
    public void test() {
        Object o = provider.parse(json);
        assertThat(o.getClass(), equalTo(LinkedHashMap.class));
        LinkedHashMap map = (LinkedHashMap) o;
        assertThat(map.get("value").getClass(), equalTo(clazz));
    }

    private static ObjectMapper getObjectMapper(DeserializationFeature feature) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.enable(feature);
        return objectMapper;

    }
}
