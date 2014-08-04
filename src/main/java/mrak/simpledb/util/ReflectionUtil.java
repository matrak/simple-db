package mrak.simpledb.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class ReflectionUtil {

	public static Class<?> getListClass(Field f) {
		Type type = f.getGenericType();
		
		if (type instanceof ParameterizedType) {
		    Type listType = ((ParameterizedType) type).getActualTypeArguments()[0];
		    Class<?> clazz = (Class<?>) listType;
		    return clazz;
		}
		else {
			return null;
		}
	}
	
	public static<A extends Annotation> List<Field> getFieldForAnnotation(Class<A> annotation, Class<?> clazz) {
		Field[] fields = clazz.getDeclaredFields();
		List<Field> result = new ArrayList<>();
		for(Field f : fields) {
			if(f.getAnnotation(annotation) != null) {
				result.add(f);
			}
		}
		return result;
	}
	
}
