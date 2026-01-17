package task_2_3_4.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(value = RetentionPolicy.RUNTIME)
@Target(value = ElementType.FIELD)
public @interface ConfigProperty {
    String configFileName () default "hotel.properties";
    String propertyName () default "";
    ConfigType type() default ConfigType.STRING;
}
