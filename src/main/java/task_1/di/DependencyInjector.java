package task_1.di;

import task_1.annotations.Component;
import task_1.annotations.Inject;
import task_1.annotations.Singleton;
import task_1.config.ConfigManager;
import task_1.dao.hibernate.*;
import task_1.dao.*;

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

        ConfigManager.config(object);

        return object;
    }

    public Object getDependency(Class<?> type) {
        try {
            Class<?> resolvedType = resolveType(type);

            if (resolvedType.isAnnotationPresent(Singleton.class)) {
                if (inCreation.contains(resolvedType)) {
                    throw new RuntimeException("Обнаружена циклическая зависимость для класса: " + resolvedType);
                }

                Object instance = singletons.get(resolvedType);
                if (instance == null) {
                    inCreation.add(resolvedType);
                    try {
                        instance = createInstance(resolvedType);
                        singletons.put(resolvedType, instance);
                    } finally {
                        inCreation.remove(resolvedType);
                    }
                }
                return instance;
            }

            return createInstance(resolvedType);

        } catch (Exception e) {
            throw new RuntimeException("Ошибка при создании зависимости: " + type, e);
        }
    }

    private Class<?> resolveType(Class<?> type) {
        if (!type.isInterface()) {
            return type;
        }
        if (type == GuestDao.class) return GuestHibernateDao.class;
        if (type == RoomDao.class) return RoomHibernateDao.class;
        if (type == ResidenceDao.class) return ResidenceHibernateDao.class;
        if (type == ServiceDao.class) return ServiceHibernateDao.class;
        if (type == ServiceRecordDao.class) return ServiceRecordHibernateDao.class;

        throw new RuntimeException("Неизвестная реализация для интерфейса: " + type.getName());
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
