package example;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;


@RestController
public class ExampleController {

    public static final Double VALUE_DOUBLE_FAIL = 0.07185454505642408;
    public static final Double VALUE_DOUBLE_OK = 0.071854545056424;
    public static final BigDecimal VALUE_BIG_DECIMAL_FAIL = BigDecimal.valueOf(VALUE_DOUBLE_OK);
    public static final Long VALUE_LONG_FAIL = 1L;
    public static final Long VALUE_LONG_OK = 1132213213132L;


    @GetMapping(value = "/test/double/ok", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity getDoubleOk() {
        return new ResponseEntity(new Dto(VALUE_DOUBLE_OK), HttpStatus.OK);
    }

    @GetMapping(value = "/test/long/ok", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity getLongOk() {
        return new ResponseEntity(new Dto(VALUE_LONG_OK), HttpStatus.OK);
    }

    @GetMapping(value = "/test/double/fail", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity getDoubleFail() {
        return new ResponseEntity(new Dto(VALUE_DOUBLE_FAIL), HttpStatus.OK);
    }

    @GetMapping(value = "/test/long/fail", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity getLongFail() {
        return new ResponseEntity(new Dto(VALUE_LONG_FAIL), HttpStatus.OK);
    }

    @GetMapping(value = "/test/bigdecimal", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity getBigDecimalFail() {
        return new ResponseEntity(new Dto(VALUE_BIG_DECIMAL_FAIL), HttpStatus.OK);
    }
}

