package di;

import annotations.Component;
import annotations.Inject;
import annotations.Singleton;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class DependencyInjector {

    private final Map<Class<?>, Object> singletons = new HashMap<>();
    private final Set<Class<?>> inCreation = new HashSet<>();

    public <T> T injectDependencies(T object) {
        Class<?> clas = object.getClass();

        for (Field field : clas.getDeclaredFields()) {
            if (field.isAnnotationPresent(Inject.class)) {
                Object dependency = getDependency(field.getType());
                field.setAccessible(true);
                try {
                    field.set(object, dependency);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException("Не удалось внедрить зависимость: " + field, e);
                }
            }
        }
        return object;
    }

    public Object getDependency(Class<?> type) {
        try {
            if (type.isAnnotationPresent(Singleton.class)) {
                if (inCreation.contains(type)) {
                    throw new RuntimeException("Обнаружена циклическая зависимость для класса: " + type);
                }

                Object instance = singletons.get(type);
                if (instance == null) {
                    inCreation.add(type);
                    try {
                        instance = createInstance(type);
                        singletons.put(type, instance);
                    } finally {
                        inCreation.remove(type);
                    }
                }
                return instance;
            }
            return createInstance(type);
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при создании зависимости: " + type, e);
        }
    }

    private Object createInstance(Class<?> type) {
        try {
            if (!type.isAnnotationPresent(Component.class)) {
                throw new RuntimeException("Класс " + type + " не помечен как @Component");
            }

            Object instance = type.getDeclaredConstructor().newInstance();

            // внедряем зависимости рекурсивно
            injectDependencies(instance);

            return instance;

        } catch (Exception e) {
            throw new RuntimeException("Не удалось создать компонент: " + type, e);
        }
    }
}