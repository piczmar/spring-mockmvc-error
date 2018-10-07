package example;

import com.fasterxml.jackson.annotation.JsonCreator;

public class Dto<T> {
    private final T value;

    @JsonCreator
    public Dto(T value) {
        this.value = value;
    }

    public T getValue() {
        return value;
    }
}
