# Json conversion errors with Spring MockMvc

MockMvc is a Spring class used for unit testing controllers without necessity to start server.
It has a fluent API for asserting response messages. E.g.: for JSON response we can write assertions like that: 

```java
 this.mockMvc.perform(get("/test"))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.value", is("Hello!")));

``` 

Underneath it's using [`jsonPath`](https://github.com/json-path/JsonPath).

It works nicely for String values assertions but can behave weird with numeric values.

Here is an example: 


When testing controller method: 

```java

    public static final double VALUE = 0.07185454505642408;

...

    @GetMapping(value = "/test", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity get() {
        Dto dto = new Dto();
        dto.setValue(VALUE);
        return new ResponseEntity(dto, HttpStatus.OK);
    }
```

using test:


```java
@RunWith(SpringRunner.class)
@WebMvcTest(ExampleController.class)
public class ExampleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void should_get_metadata_when_video_processed() throws Exception {
        this.mockMvc.perform(get("/test"))
                .andDo(print())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.value", is(ExampleController.VALUE)));
    }

}
```
the test fails with: 

```
java.lang.AssertionError: JSON path "$.value"
Expected: is <0.07185454505642408>
     but: was <0.07185454505642408>
Expected :is <0.07185454505642408>
     
Actual   :<0.07185454505642408>
```

Wow ! Both printed values, expected and actual, are the same.
What's going on?

In fact the problem is the type of expected and actual.
The expected is `Double` but the actual is `BigDecimal`.

I wouldn't have known it if I had not debugged it.

The reason is that MockMvc is using a `JsonSmartJsonProvider` for serialization.

When value is small, e.g. for Long it can be down-casted to Integer, for BigDecimal - to Double, then this is how 
`JsonSmartJsonProvider` works. 
It results in unexpected situations when we serialize values of specific type to JSON but it cannot be deserialized back 
to the same numeric type.

What is a solution then?
There is an another JSON provider which can be used instead - a `JacksonJsonProvider` which is predictive because 
you can configure it to always deserialize numeric values to Long and Double.

It's a pity that it is not the default implementation usd in jsonPath.

Here is a little Github project with some tests demonstrating issues with `JsonSmartJsonProvider` and how to solve them with
`JacksonJsonProvider`

In nutshell, the solution is simple.  
You need to add this config in your tests initialization: 

```java

        final ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.enable(DeserializationFeature.USE_LONG_FOR_INTS);
        objectMapper.enable(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS);

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
```

By setting `DeserializationFeature` you get integer values deserialized always to `Long` and float always to `BigDecimal`.


Hope this helps you to save some time on solving the weird errors I faced.