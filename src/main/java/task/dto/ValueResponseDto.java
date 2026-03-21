package task.dto;

public class ValueResponseDto<T> {
    private T value;

    public ValueResponseDto() { }

    public ValueResponseDto(T value) {
        this.value = value;
    }

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }
}