package example;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.Option;
import com.jayway.jsonpath.internal.DefaultsImpl;
import com.jayway.jsonpath.spi.json.JacksonJsonProvider;
import com.jayway.jsonpath.spi.json.JsonProvider;
import com.jayway.jsonpath.spi.mapper.JacksonMappingProvider;
import com.jayway.jsonpath.spi.mapper.MappingProvider;
import org.apache.commons.io.input.CountingInputStream;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.io.ByteArrayInputStream;
import java.math.BigInteger;
import java.util.EnumSet;
import java.util.Set;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

/**
 * Unit tests for the API
 */
@RunWith(SpringRunner.class)
@WebMvcTest(ExampleController.class)
public class ExampleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    // Reset to default configuration
    @After
    public void after(){
        Configuration.setDefaults(DefaultsImpl.INSTANCE);
    }

    @Test // fail
    public void testLongFail() throws Exception {
        this.mockMvc.perform(get("/test/long/fail"))
                .andDo(print())
                .andExpect(jsonPath("$.value", is(ExampleController.VALUE_LONG_FAIL)));
    }


    @Test // success
    public void testLongOk() throws Exception {
        this.mockMvc.perform(get("/test/long/ok"))
                .andDo(print())
                .andExpect(jsonPath("$.value", is(ExampleController.VALUE_LONG_OK)));
    }


    @Test // fail
    public void testDoubleFail() throws Exception {
        this.mockMvc.perform(get("/test/double/fail"))
                .andDo(print())
                .andExpect(jsonPath("$.value", is(ExampleController.VALUE_DOUBLE_FAIL)));
    }


    @Test // success
    public void testDoubleOk() throws Exception {
        this.mockMvc.perform(get("/test/double/ok"))
                .andDo(print())
                .andExpect(jsonPath("$.value", is(ExampleController.VALUE_DOUBLE_OK)));
    }


    @Test // fail
    public void testBigDecimalFail() throws Exception {
        this.mockMvc.perform(get("/test/bigdecimal"))
                .andDo(print())
                .andExpect(jsonPath("$.value", is(ExampleController.VALUE_BIG_DECIMAL_FAIL)));
    }

    @Test // success
    public void testBigDecimalOk() throws Exception {
        this.mockMvc.perform(get("/test/bigdecimal"))
                .andDo(print())
                .andExpect(jsonPath("$.value", is(ExampleController.VALUE_DOUBLE_OK)));
    }

    @Test // success
    public void testDoubleWithConfig() throws Exception {
        configJsonProvider();
        this.mockMvc.perform(get("/test/double/fail"))
                .andDo(print())
                .andExpect(jsonPath("$.value", is(ExampleController.VALUE_DOUBLE_FAIL)));
    }

    @Test // success
    public void testLongWithConfig() throws Exception {
        configJsonProvider();
        this.mockMvc.perform(get("/test/long/fail"))
                .andDo(print())
                .andExpect(jsonPath("$.value", is(ExampleController.VALUE_LONG_FAIL)));
    }

    @Test // success
    public void testBigDecimalWithConfig() throws Exception {
        final ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.enable(DeserializationFeature.USE_LONG_FOR_INTS);
        objectMapper.enable(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS); // default is Double

        configJsonProvider(objectMapper);
        this.mockMvc.perform(get("/test/bigdecimal"))
                .andDo(print())
                .andExpect(jsonPath("$.value", is(ExampleController.VALUE_BIG_DECIMAL_FAIL)));
    }

    private void configJsonProvider() {
        final ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.enable(DeserializationFeature.USE_LONG_FOR_INTS);

        configJsonProvider(objectMapper);
    }

    private void configJsonProvider(ObjectMapper objectMapper) {

        Configuration.setDefaults(new Configuration.Defaults() {

            private final JsonProvider jsonProvider = new JacksonJsonProvider(objectMapper);
            private final MappingProvider mappingProvider = new JacksonMappingProvider(objectMapper);

            @Override
            public JsonProvider jsonProvider() {
                return jsonProvider;
            }

            @Override
            public MappingProvider mappingProvider() {
                return mappingProvider;
            }

            @Override
            public Set<Option> options() {
                return EnumSet.noneOf(Option.class);
            }
        });
    }

}