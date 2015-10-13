package mrak.simpledb.util;

import java.io.File;
import java.io.FileWriter;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public class ClassMetaUtil {

	protected static String packageNameHelper(Class<?> from, String metaPackageName) {
		String pcgName = "";

		if (metaPackageName != null) {
			pcgName = from.getPackage().getName() + "." + metaPackageName;
		} else {
			pcgName = from.getPackage().getName();
		}

		return pcgName;
	}

	protected static String classNameHelper(Class<?> from) {
		return from.getSimpleName() + "_";
	}

	protected static String fieldNameHelper(Field from) {
		return from.getName();
	}

	public static String newMetaClass(Class<?> clazz, String metaPackageName) 
	{
		String className = classNameHelper(clazz);
		StringBuilder b = new StringBuilder();
		b.append("// generated in ").append(ClassMetaUtil.class.getCanonicalName()).append("\n");

		// package
		b.append("package ").append(packageNameHelper(clazz, metaPackageName)).append(";\n");

		// class definition
		b.append("public class ").append(className);

		// check super class
		Class<?> superClazz = clazz.getSuperclass();
		if (!superClazz.equals(Object.class)) {
			// extends BaseEntity_
			b.append(" extends ")
					.append(packageNameHelper(superClazz, metaPackageName))
					.append(".").append(superClazz.getSimpleName()).append("_");
		}
		b.append(" {\n");

		// class fields
		Field[] fields = clazz.getDeclaredFields();
		for (Field f : fields) {
			if (Modifier.isStatic(f.getModifiers()))
				continue;

			b.append("public static String ").append(f.getName()).append(" = ")
					.append("\"").append(fieldNameHelper(f)).append("\";\n");
		}
		b.append("}");
		return b.toString();
	}
	

    public static void saveMetaClassToFile(File baseDir, String clazzDef, String metaPackageName, Class<?> fromClazz) throws Exception 
    {
        String clazzPcg = packageNameHelper(fromClazz, metaPackageName);

        String[] pcg = clazzPcg.split("\\.");
        File dest = baseDir;
        for(String p : pcg)
        {
            dest = new File(dest, p);
            if(!dest.exists()) dest.mkdir();
        }

        dest = new File(dest, classNameHelper(fromClazz) + ".java");

        FileWriter write = new FileWriter(dest);
        write.write(clazzDef);
        write.close();
    }	
}
