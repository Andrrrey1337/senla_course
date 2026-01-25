package task_1_2_3.annotations;

import java.lang.annotation.*;

@Target(value = ElementType.FIELD)
@Retention(value = RetentionPolicy.RUNTIME)
public @interface Inject { }
